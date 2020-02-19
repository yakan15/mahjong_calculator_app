package miyazawa.mahjong.activities

import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera2.*
import miyazawa.mahjong.*
import miyazawa.mahjong.fragments.ProgressDialogFragment

import miyazawa.mahjong.model.CameraPermissionHelper
import miyazawa.mahjong.model.Hand
import miyazawa.mahjong.model.ResultHand
import miyazawa.mahjong.utils.*
import miyazawa.mahjong.utils.ImageSaver
import miyazawa.mahjong.variables.Dummies
import miyazawa.mahjong.variables.Variables
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

enum class CameraMode {
    AUTO_FIT, FULL_SCREEN, OPENGL
}


class CameraActivity2 : AppCompatActivity() {

    private var camera: Camera? = null

    private lateinit var previewSize: Size

    private val cameraMode: CameraMode =
        CameraMode.AUTO_FIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        Toast.makeText(applicationContext, "OK", Toast.LENGTH_LONG).show()
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }
        if (!StoragePermissionHelper.hasReadPermission(this)) {
            StoragePermissionHelper.requestReadPermission(this)
            return
        }
        buttonShutter.setOnClickListener {
            saveImage()
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
           openCamera(width, height)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = true

        override fun onSurfaceTextureSizeChanged( surface: SurfaceTexture?, width: Int, height: Int ) { }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit
    }

    private fun openCamera(width: Int, height: Int) {
        val manager = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camera = Camera.initInstance(manager).apply {
            setUpCameraOutputs(width, height, this, cameraMode)
            configureTransform(width, height)
            this.open()
            val texture = textureView.surfaceTexture
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)
            this.start(Surface(texture))
            logd("===camera start")
        }

    }


    private fun setUpCameraOutputs(width: Int, height: Int, camera: Camera, mode: CameraMode) {
        try {
            val largest: Size = when(mode) {
                CameraMode.AUTO_FIT -> {
                    camera.getCaptureSize()
                }
                CameraMode.OPENGL, CameraMode.FULL_SCREEN -> {
                    val realSize = Point()
                    this.windowManager.defaultDisplay.getRealSize(realSize)
                    val aspectRatio = realSize.x.toFloat() / realSize.y.toFloat()
                    logd("===== aspect ratio ${aspectRatio}")
                    camera.getPreviewSize(aspectRatio)
                }
//                else -> camera.getCaptureSize()
            }
            val displayRotation = this.windowManager.defaultDisplay.rotation

            val sensorOrientation = camera.getSensorOrientation()

            val swappedDimensions = areDimensionsSwapped(sensorOrientation, displayRotation)

            val displaySize = Point()
            this.windowManager.defaultDisplay.getSize(displaySize)

            logd("===== display size ${displaySize.x} ${displaySize.y} ${largest} ")

            if (swappedDimensions) {
                previewSize = camera.chooseOptimalSize(
                    height,
                    width,
                    displaySize.x,
                    displaySize.y,
                    largest
                )
            } else {
                previewSize = camera.chooseOptimalSize(
                    width,
                    height,
                    displaySize.y,
                    displaySize.x,
                    largest
                )
            }
        } catch (e: CameraAccessException) {
            loge(e.toString())
        }
    }

    private fun areDimensionsSwapped(sensorOrientation: Int, displayRotation: Int) : Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientation % 180 == 90) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientation % 180 == 0) {
                    swappedDimensions = true
                }
            }
            else -> {
                // invalid display rotation
            }
        }
        return swappedDimensions
    }

    override fun onPause() {
        super.onPause()
        camera?.close()
    }

    /**
     * Configures the necessary [android.graphics.Matrix] transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val rotation = this.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (rotation in listOf(Surface.ROTATION_90, Surface.ROTATION_270)) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                viewHeight.toFloat() / previewSize.height,
                viewWidth.toFloat() / previewSize.width
            )
            with (matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
           matrix.postRotate(180f, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    private fun saveImage() {
        camera?.takePicture(object : Camera.ImageHandler {
           override fun handleImage(image: Image?) : Runnable {
               val appDir = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "mahjongCalculator")
               return ImageSaver(this@CameraActivity2, image, appDir)
           }
        })
    }

    private fun reopenCamera() {
        camera?.open()
        val texture = textureView.surfaceTexture
        camera?.start(Surface(texture))
    }


    fun sendImage(image: File) {
//        camera?.close()
        val dialog = ProgressDialogFragment()
        dialog.show(supportFragmentManager, "ProgressDialogFragment")
        val map: MutableMap<String, RequestBody> = hashMapOf()
//        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), image)
        val body = image.asRequestBody("image/jpg".toMediaTypeOrNull())
        map.put("upload\"; filename=\"image.jpg\"", body)
        try {
            val response = ApiClientManager.apiClient.upload(map).execute()
            val resultHand: ResultHand? = response.body()
            logd(response.body().toString())
            if (resultHand == null) {
                camera?.open()
                val texture = textureView.surfaceTexture
                camera?.start(Surface(texture))
                return
            }
            logd("${resultHand.toString()}")
            val hand = Hand(resultHand)
            if (!hand.validate()) {
                throw IllegalArgumentException("聴牌していません。")
            }
            runOnUiThread{
                dialog.dismiss()
                Toast.makeText(applicationContext, "OK", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, ResultActivity::class.java)
                intent.putExtra(ResultActivity.HAND, hand)
                startActivity(intent)
            }
            logd("ok!")
        }
        catch (e: IllegalArgumentException) {
            runOnUiThread{
                Toast.makeText(applicationContext, "判別エラー", Toast.LENGTH_LONG).show()
            }
            loge(e.toString())
            camera?.open()
            val texture = textureView.surfaceTexture
            camera?.start(Surface(texture))
            dialog.dismiss()
        }
        catch (e: Exception) {
            runOnUiThread{
                Toast.makeText(applicationContext, "通信エラー", Toast.LENGTH_LONG).show()
            }
            camera?.open()
            val texture = textureView.surfaceTexture
            camera?.start(Surface(texture))
            dialog.dismiss()
        }
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                Log.d("tst", "response=${it.agari}")
//            }, {
//                //                image.delete()
//            })
        logd("===== test")
    }


    override fun onResume() {
        super.onResume()
        Variables.initialize()
        textureView.surfaceTextureListener = surfaceTextureListener
    }


}
