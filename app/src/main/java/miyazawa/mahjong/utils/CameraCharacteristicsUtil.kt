package miyazawa.mahjong.utils

import android.graphics.ImageFormat
import miyazawa.mahjong.services.CompareSizesByArea
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size

/**
 * Given `choices` of `Size`s supported by a camera, choose the smallest one that
 * is at least as large as the respective texture view size, and that is at most as large as the
 * respective max size, and whose aspect ratio matches with the specified value. If such size
 * doesn't exist, choose the largest one that is at most as large as the respective max size,
 * and whose aspect ratio matches with the specified value.
 *
 * @param textureViewWidth  The width of the texture view relative to sensor coordinate
 * @param textureViewHeight The height of the texture view relative to sensor coordinate
 * @param maxWidth          The maximum width that can be chosen
 * @param maxHeight         The maximum height that can be chosen
 * @param aspectRatio       The aspect ratio
 * @return The optimal `Size`, or an arbitrary one if none were big enough
 */

private const val MAX_PREVIEW_HEIGHT = 1440
private const val MAX_PREVIEW_WIDTH = 1920

fun CameraCharacteristics.isSupported(
    modes: CameraCharacteristics.Key<IntArray>, mode: Int
): Boolean {
    val ints = this.get(modes) ?: return false
    return mode in ints
}

fun CameraCharacteristics.isAutoExposureSupported(mode: Int): Boolean =
    isSupported(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, mode)

fun CameraCharacteristics.isContinuousAutoFocusSupported(): Boolean =
    isSupported(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES,
        CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

fun CameraCharacteristics.isAutoWhiteBalancesSupported(): Boolean =
    isSupported(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES,
        CameraCharacteristics.CONTROL_AWB_MODE_AUTO)

fun CameraCharacteristics.getPreviewSize(aspectRatio: Float): Size {
    val map: StreamConfigurationMap =
        get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: return Size(0, 0)
    return chooseOutputSize(
        map.getOutputSizes(SurfaceTexture::class.java).asList(),
        aspectRatio
    )
}

fun CameraCharacteristics.getCaptureSize(comparator: Comparator<Size>): Size {
    val map: StreamConfigurationMap =
        get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: return Size(0, 0)
    return map.getOutputSizes(ImageFormat.JPEG)
        .asList()
        .maxWith(comparator) ?: Size(0, 0)
}

fun chooseOutputSize(sizes: List<Size>, aspectRatio: Float): Size {
    if (aspectRatio > 1.0f) {
        val size = sizes.firstOrNull {
            it.height == it.width * 4 / 3 && it.height < 1440
        }
        return size ?: sizes[0]
    } else {
        val potentials = sizes.filter { it.height.toFloat() / it.width.toFloat() == aspectRatio }
        return if (potentials.isNotEmpty()) {
            potentials.firstOrNull { it.height == 1440 || it.height == 960} ?: potentials[0]
        } else {
            sizes[0]
        }
    }
}

fun CameraCharacteristics.chooseOptimalSize(textureViewWidth: Int,
                                            textureViewHeight: Int,
                                            maxWidth: Int,
                                            maxHeight: Int,
                                            aspectRatio: Size
): Size {
    var _maxWidth = maxWidth
    var _maxHeight = maxHeight

    if (_maxWidth > MAX_PREVIEW_WIDTH) {
        _maxWidth = MAX_PREVIEW_WIDTH
    }

    if (_maxHeight > MAX_PREVIEW_HEIGHT) {
        _maxHeight = MAX_PREVIEW_HEIGHT
    }

    val map = get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: return Size(0, 0)

    val choices = map.getOutputSizes(SurfaceTexture::class.java)

    // Collect the supported resolutions that are at least as big as the preview Surface
    val bigEnough = ArrayList<Size>()
    // Collect the supported resolutions that are smaller than the preview Surface
    val notBigEnough = ArrayList<Size>()
    val w = aspectRatio.width
    val h = aspectRatio.height
    for (option in choices) {
        if (option.width <= _maxWidth &&
            option.height <= _maxHeight &&
            option.height == option.width * h / w) {
            if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                bigEnough.add(option)
            } else {
                notBigEnough.add(option)
            }
        }
    }
    // Pick the smallest of those big enough. If there is no one big enough, pick the
    // largest of those not big enough.
    return when {
        bigEnough.size > 0 -> bigEnough.asSequence().sortedWith(CompareSizesByArea()).first()
        notBigEnough.size > 0 -> notBigEnough.asSequence().sortedWith(CompareSizesByArea()).last()
        else -> choices[0]
    }
}