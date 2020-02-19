package miyazawa.mahjong.model

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

object CameraPermissionHelper {
    private const val CAMERA_PERMISSION_CODE = 0
    private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    /*
     * 権限があるか否かのチェック
     */
    fun hasCameraPermission(activity: Activity) : Boolean =
        ContextCompat.checkSelfPermission(activity,
            CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED

    /*
     *  権限のリクエスト
     */
    fun requestCameraPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(CAMERA_PERMISSION), CAMERA_PERMISSION_CODE
        )
    }

    /*
     * 許可の根拠を見せる必要があるか否かのチェック
     */
    fun shouldShowRequestPermissionRationale(activity: Activity) : Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)

    /*
     * 権限を付与するためにアプリ設定画面を表示させる
     */
    fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}