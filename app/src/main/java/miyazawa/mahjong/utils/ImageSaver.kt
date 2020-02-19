package miyazawa.mahjong.utils

import android.media.Image
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import miyazawa.mahjong.activities.CameraActivity2
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal class ImageSaver(
    private val activity: CameraActivity2,
    /**
     * The JPEG image
     */
    private val image: Image?,

    /**
     * The file we save the image into.
     */
    private val file: File
) : Runnable {

    override fun run() {
        if (!file.exists()) {
            file.mkdirs()
        }
        val saveFile = File(file, "tmp.png")
        if (saveFile.exists()) {
            saveFile.delete()
        }
        if (image == null) return
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(saveFile).apply {
                write(bytes)
            }
        } catch (e: Throwable) {
            loge(e.toString())
        } finally {
            image.close()
            output?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    loge(e.toString())
                }
            }
            Thread.sleep(200)
            GlobalScope.async {
                launch {
                    activity.sendImage(saveFile)
                }
            }
            logd("===== this thread is alive!")
        }
    }

    companion object {
        /**
         * Tag for the [Log].
         */
        private val TAG = "ImageSaver"
    }
}
