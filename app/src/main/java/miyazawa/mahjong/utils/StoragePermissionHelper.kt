package miyazawa.mahjong.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object StoragePermissionHelper {
    private const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    private const val READ_PERMISSION_CODE = 1001
    private const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private const val WRITE_PERMISSION_CODE = 1002

    fun hasReadPermission(activity: Activity) : Boolean =
        ContextCompat.checkSelfPermission(activity,
            READ_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    fun requestReadPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(READ_PERMISSION),
            READ_PERMISSION_CODE
        )
    }

    fun shouldShowRequestReadPermissionRational(activity: Activity) : Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(activity,
            READ_PERMISSION
        )
}