package miyazawa.mahjong.utils

import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import miyazawa.mahjong.services.CompareSizesByArea
import java.lang.IllegalStateException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


enum class WBMode {
    AUTO, SUNNY, INCANDECENT
}

interface OnFocusListener {
    fun onFocusStateChanged(focusState: Int)
}

class Camera constructor(private val cameraManager: CameraManager) {
    private enum class State {
        PREVIEW,
        WAITING_LOCK,
        WAITING_PRECAPTURE,
        WAITING_NON_PRECAPTURE,
        TAKEN
    }

    private var focusListener: OnFocusListener? = null

    private var preAfState: Int? = null

    private var state = State.PREVIEW

    private var isClosed = true

    private val cameraId: String

    private val characteristics: CameraCharacteristics

    private val openLock = Semaphore(1)

    private var cameraDevice: CameraDevice? = null

    private var imageReader: ImageReader? = null

    private var aeMode = CaptureRequest.CONTROL_AE_MODE_ON
    private var wbMode = WBMode.AUTO

    /*
     * バックグラウンド処理用
     */
    private var backgroundHandler: Handler? = null

    /*
     * UI描画を妨げない処理用のスレッド？
     */
    private var backgroundThread: HandlerThread? = null

    private var surface: Surface? = null

    private var captureSession: CameraCaptureSession? = null

    companion object {
        @Volatile var instance: Camera? = null
            private set
        fun initInstance(cameraManager: CameraManager): Camera {
            val i = instance
            if (i != null) {
                return i
            }
            return synchronized(this) {
                val created = Camera(cameraManager)
                instance = created
                created
            }
        }
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            openLock.release()
            isClosed = false
        }
        override fun onClosed(camera: CameraDevice) {
            isClosed = true
        }
        override fun onDisconnected(camera: CameraDevice) {
            openLock.release()
            camera?.close()
            cameraDevice = null
            isClosed = true
        }
        override fun onError(camera: CameraDevice, error: Int) {
            openLock.release()
            camera?.close()
            cameraDevice = null
            isClosed = true
        }

    }


    private val captureStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            logd("===== configure failed")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            logd("===== configured")
            captureSession = session
            startPreview()
        }
    }
    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        private fun process(result: CaptureResult) {
//            Thread.sleep(50)
            when (state) {
                State.PREVIEW -> {
                    val afState = result.get(CaptureResult.CONTROL_AF_STATE) ?: return
                    if (afState == preAfState) {
                        return
                    }
                    preAfState = afState
                    focusListener?.onFocusStateChanged(afState)
                }
                State.WAITING_LOCK -> {
                    val afState = result.get(CaptureResult.CONTROL_AF_STATE)
                    Thread.sleep(600)
                    // Auto Focus state is not ready in the first place
                    if (afState == null) {
                        runPreCapture()
                    } else if (CaptureResult.CONTROL_AF_STATE_INACTIVE == afState ||
                        CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                        CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            captureStillPicture()
                        } else {
                            runPreCapture()
                        }
                    } else {
                        captureStillPicture()
                    }
                }

                State.WAITING_PRECAPTURE -> {
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null
                        || aeState == CaptureRequest.CONTROL_AE_STATE_PRECAPTURE
                        || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED
                        || aeState == CaptureRequest.CONTROL_AE_STATE_CONVERGED) {
                        state = State.WAITING_NON_PRECAPTURE
                    }
                }

                State.WAITING_NON_PRECAPTURE -> {
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureRequest.CONTROL_AE_STATE_PRECAPTURE) {
                        captureStillPicture()
                    }
                }
                else -> { }
            }
        }

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            process(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            process(result)
        }

    }

    init {
        cameraId = setUpCameraId(manager = cameraManager)
        characteristics = cameraManager.getCameraCharacteristics(cameraId)
    }

    private fun setUpCameraId(manager: CameraManager): String {
        for (cameraId in manager.cameraIdList) {
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                continue
            }
            return cameraId
        }
        throw IllegalStateException("Could not set Camera Id")
    }

    fun getPreviewSize(aspectRatio: Float) = characteristics.getPreviewSize(aspectRatio)

    fun getCaptureSize() = characteristics.getCaptureSize(CompareSizesByArea())

    fun getSensorOrientation() = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

    fun chooseOptimalSize(textureViewWidth: Int,
                          textureViewHeight: Int,
                          maxWidth: Int,
                          maxHeight: Int,
                          aspectRatino: Size): Size =
        characteristics.chooseOptimalSize(
            textureViewWidth,
            textureViewHeight,
            maxWidth,
            maxHeight,
            aspectRatino
        )

    private fun startBackgroundHandler() {
        if (backgroundThread != null) return
        backgroundThread = HandlerThread("Camera-$cameraId").also {
            it.start()
            backgroundHandler = Handler(it.looper)
        }
    }

    fun open() {
        try {
            if (!openLock.tryAcquire(3L, TimeUnit.SECONDS)) {
                throw IllegalStateException("Camera launch failed")
            }
            if (cameraDevice != null) {
                openLock.release()
                return
            }
            startBackgroundHandler()
            cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler)
        } catch (e: SecurityException) {

        }
    }

    fun close() {
        try {
            if (openLock.tryAcquire(3L, TimeUnit.SECONDS)) {
                isClosed = true
            }
            captureSession?.close()
            captureSession = null

            cameraDevice?.close()
            cameraDevice = null

            surface?.release()
            surface = null

            imageReader?.close()
            imageReader = null
            stopBackGroundHandler()
        } catch (e: InterruptedException) {
            loge("Error closing camera $e")
        } finally {
            logd("===== camera closed")
            openLock.release()
        }

    }

    private fun stopBackGroundHandler() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            loge("===== stop background error $e")
        }
    }

    fun start(surface: Surface) {
        logd("===== start")
        this@Camera.surface = surface
        val size = characteristics.getCaptureSize(CompareSizesByArea())
        imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 1)
        cameraDevice?.createCaptureSession(
            listOf(surface, imageReader?.surface),
            captureStateCallback,
            backgroundHandler
        )
    }

    private fun startPreview() {
        logd("===== start preview")
        try {
            if (!openLock.tryAcquire(1L, TimeUnit.SECONDS)) {
                logd("===== acquire failed")
                return
            }
            logd("===== acquire success")
            val builder = createPreviewRequestBuilder()
            builder?.set(
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
            builder?.build()?.let {captureRequest ->
                captureSession?.setRepeatingRequest(
                    captureRequest, captureCallback, backgroundHandler
                )
            }
        } catch (e: IllegalStateException) {
            loge(e.toString())
        } catch (e: CameraAccessException) {
            loge(e.toString())

        } catch (e: InterruptedException) {
            loge(e.toString())

        } finally {
            openLock.release()
        }
    }

    @Throws(CameraAccessException::class)
    private fun createPreviewRequestBuilder(): CaptureRequest.Builder? {
        val builder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        logd("===== surface: ${surface}")
        surface?.let {surface ->
            builder?.addTarget(surface)
            logd("add surface")
        }
        enableDefaultModes(builder)
        return builder
    }

    /*
     * モード初期設定
     */
    private fun enableDefaultModes(builder: CaptureRequest.Builder?) {
        if (builder == null) return
        builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)

        if (characteristics.isContinuousAutoFocusSupported()) {
            builder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
        } else {
            builder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_AUTO
            )
        }

        if (characteristics.isAutoExposureSupported(aeMode)) {
            builder.set(CaptureRequest.CONTROL_AWB_MODE, aeMode)
        } else {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
        }

        when(wbMode) {
            WBMode.AUTO -> {
                if (characteristics.isAutoWhiteBalancesSupported()) {
                    builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
                }
            }
            WBMode.SUNNY -> {
                builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT)
            }
            WBMode.INCANDECENT -> {
                builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT)
            }
        }

        builder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY)
    }

    private fun lockFocus() {
        try {
            val builder = createPreviewRequestBuilder()
            if (!characteristics.isContinuousAutoFocusSupported()) {
                builder?.set(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_START
                )
            }
            builder?.build()?.let {captureRequest ->
               captureSession?.capture(captureRequest, captureCallback, backgroundHandler)
            }
            state = State.WAITING_LOCK
        } catch (e: CameraAccessException) {
            loge("lock focus $e")
        }
    }

    private fun runPreCapture() {
        try {
            state = State.WAITING_PRECAPTURE
            val builder = createPreviewRequestBuilder()
            builder?.set(
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
            builder?.build()?.let { captureRequest ->
                captureSession?.capture(
                    captureRequest,
                    captureCallback,
                    backgroundHandler
                )
            }
        } catch (e: CameraAccessException) {
            loge("runPreCapture $e")
        }
    }

    private fun captureStillPicture() {
        state = State.TAKEN
        val builder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        enableDefaultModes(builder)
        imageReader?.surface?.let { builder?.addTarget(it) }
        surface?.let { builder?.addTarget(it) }
        captureSession?.stopRepeating()
        builder?.build()?.let {captureRequest ->
            captureSession?.capture(
                captureRequest,
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                       // ここに処理
                    }
                },
                backgroundHandler
            )
        }
    }

    interface ImageHandler {
        fun handleImage(image: Image?) : Runnable
    }

    fun takePicture(handler: ImageHandler) {
        if (isClosed) return
        lockFocus()
        imageReader?.setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                val image = reader?.acquireNextImage()
                backgroundHandler?.post(handler.handleImage(image = image))
            }
        }, backgroundHandler)

    }
}