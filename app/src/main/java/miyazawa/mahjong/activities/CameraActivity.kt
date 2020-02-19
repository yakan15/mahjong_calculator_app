package miyazawa.mahjong.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.surfaceView
import miyazawa.mahjong.R
import miyazawa.mahjong.utils.StoragePermissionHelper
import miyazawa.mahjong.model.CameraPermissionHelper
import miyazawa.mahjong.utils.ApiClientManager
import miyazawa.mahjong.utils.logd
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class CameraActivity : AppCompatActivity() {
    private val cameraOpenCloseLock = Semaphore(1)
    lateinit private var imageReader: ImageReader
    lateinit private var captureSession: CameraCaptureSession
    lateinit private var previewRequest: CaptureRequest
    lateinit private var cameraDevice: CameraDevice

    private val MAX_WIDTH = 1920
    private val MAX_HEIGHT = 1080

    val surfaceReadyCallback = object: SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) { }

        override fun surfaceDestroyed(holder: SurfaceHolder?) { }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            startCameraSession()
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession.close()
            cameraDevice.close()
            imageReader.close()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }
        if (!StoragePermissionHelper.hasReadPermission(this)) {
            StoragePermissionHelper.requestReadPermission(this)
            return
        }
        setContentView(R.layout.activity_camera)
        surfaceView.holder.addCallback(surfaceReadyCallback)
            buttonShutter.setOnClickListener {
                val appDir = File(getExternalFilesDir(DIRECTORY_DOCUMENTS), "mahjongCalculator")
                if (!appDir.exists()) {
                    appDir.mkdirs()
                }
                try {
                    captureSession.stopRepeating()
//                    val dialog = ProgressDialogFragment()
//                    dialog.show(supportFragmentManager, "ProgressDialogFragment")
                    val filename = "tmp.jpg"
                    // is available が必要？

                    val saveFile = saveImage(appDir, filename)
                    postImage(saveFile)
                    logd("--------------再開--------------")
    //                dialog.dismiss()
                }
                catch (e: Exception) {
                    e.printStackTrace()
//                    Log.e(this::class.java.simpleName, e.toString())
                }
                finally {
                    logd("session repeat")
                    captureSession.setRepeatingRequest(previewRequest,
                        object : CameraCaptureSession.CaptureCallback() {},
                        Handler { true })
                }
            }
    }

    private fun saveImage(appDir: File, filename: String) : File {
        val saveFile = File(appDir, filename)
        imageReader.acquireLatestImage()?.let {image ->
            val imageBuf = image.planes[0].buffer
            val bytes = ByteArray(imageBuf.remaining())
            imageBuf.get(bytes)
//            var fos: FileOutputStream? = null
            try {
                saveFile.writeBytes(bytes)
//                fos = FileOutputStream(saveFile).apply {
//                    write(bytes)
//                }
                logd("Saved image to ${appDir.toString()}")
            } catch (e: IOException) {
                Log.e(this::class.simpleName, e.toString())
            } finally {
                imageBuf.clear()
                image.close()
//                fos?.let {
//                    try {
//                        it.close()
//                    } catch (e: IOException) {
//                        Log.e(this::class.simpleName, e.toString())
//                    }
//                }
            }
        }
        return saveFile
    }

    private fun postImage (image: File) {
        val map: MutableMap<String, RequestBody> = hashMapOf()
//        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), image)
        val body = image.asRequestBody("image/jpg".toMediaTypeOrNull())
        map.put("upload\"; filename=\"image.jpg\"", body)
        ApiClientManager.apiClient.upload(map)
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                image.delete()
//                Log.d("tst", "response=${it.agari}")
//            }, {
////                image.delete()
//            })
    }

    override fun onRequestPermissionsResult (requestCode: Int,
                                             permissions: Array<out String>,
                                             grantResults: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "カメラへのアクセス権がありません。", Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }

        if (!StoragePermissionHelper.hasReadPermission(this)) {
            Toast.makeText(this, "ストレージへのアクセス権がありません。", Toast.LENGTH_LONG).show()
            if (!StoragePermissionHelper.shouldShowRequestReadPermissionRational(
                    this
                )
            ) {
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
        recreate()
    }

    override fun onPause() {
        closeCamera()
        super.onPause()
    }

    @SuppressLint("ServiceCast", "MissingPermission")
    private fun startCameraSession() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (cameraManager.cameraIdList.isEmpty()) {
            return
        }
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MICROSECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
        } finally {
            cameraOpenCloseLock.release()
        }
        val firstCamera = cameraManager.cameraIdList[0]

        cameraManager.openCamera(firstCamera, object: CameraDevice.StateCallback() {
            override fun onDisconnected(camera: CameraDevice) {
                cameraOpenCloseLock.release()
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                onDisconnected(camera)
                finish()
            }

            override fun onOpened(camera: CameraDevice) {
                val cameraCharastaristics =
                    cameraManager.getCameraCharacteristics(camera.id)
                cameraCharastaristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]?.let { streamConfigurationMap ->
                    streamConfigurationMap.getOutputSizes(ImageFormat.JPEG)
                        ?.let { yuvSizes ->
                            yuvSizes[0].height
                            val previewSize = yuvSizes
                                .filter{size ->  size.height * 4 == size.width * 3}
                                .maxBy {size ->  size.height}!!
                            logd("-------yuv size---------")
                            logd(previewSize.toString())

                            val displayRotation = windowManager.defaultDisplay.rotation

                            val swappedDimensions = areDimensionsSwapped(displayRotation,
                                cameraCharastaristics)

                            val rotatedPreviewWidth = if (swappedDimensions) previewSize.height
                            else previewSize.width
                            val rotatedPreviewHeight = if (swappedDimensions) previewSize.width
                            else previewSize.height
                            surfaceView.holder.setFixedSize(rotatedPreviewWidth,
                                rotatedPreviewHeight)
                            imageReader = ImageReader.newInstance(rotatedPreviewWidth,
                                rotatedPreviewHeight,
                                ImageFormat.JPEG, 2)
                            imageReader.setOnImageAvailableListener({
                                it.acquireLatestImage()?.let {image ->
                                    image.close()
                                    // process
                                }
                            }, Handler { true })
                            val previewSurface = surfaceView.holder.surface
                            val recordingSurface = imageReader.surface
                            val captureCallback = object : CameraCaptureSession.StateCallback() {
                                override fun onConfigureFailed(session: CameraCaptureSession) { }
                                override fun onConfigured(session: CameraCaptureSession) {
                                    captureSession = session
                                    val previewRequestBuilder =
                                        camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                                            addTarget(previewSurface)
                                            addTarget(recordingSurface)
                                        }
                                    previewRequest = previewRequestBuilder.build()

                                    session.setRepeatingRequest(
                                        previewRequest,
                                        object: CameraCaptureSession.CaptureCallback() {},
                                        Handler { true }
                                    )
                                }
                            }
                            camera.createCaptureSession(mutableListOf(previewSurface,
                                recordingSurface),captureCallback,
                                Handler { true })
                        }
                }
                cameraDevice = camera
            }
        }, Handler { true })

    }



    private fun areDimensionsSwapped(displayRotation: Int,
                                     cameraCharacteristics: CameraCharacteristics) : Boolean {
        var swappedDimensions = false
        val sensor_orientation: Int? = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensor_orientation != null && sensor_orientation % 180 == 90) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensor_orientation != null && sensor_orientation % 180 == 0) {
                    swappedDimensions = true
                }
            }
            else -> {
                // invalid display rotation
            }
        }
        return swappedDimensions
    }
}
