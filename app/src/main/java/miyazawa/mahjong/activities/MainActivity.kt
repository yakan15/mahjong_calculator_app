package miyazawa.mahjong.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
//import rx.subscriptions.CompositeSubscription
import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.schedulers.Schedulers
import android.content.Intent
import miyazawa.mahjong.variables.Dummies
import miyazawa.mahjong.utils.ApiClientManager
import miyazawa.mahjong.R
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            ApiClientManager.apiClient.getResult()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("tst", "response=${it.agari}")
                }, {
                })
        }

        cameraButton.setOnClickListener {
            val intent = Intent(applicationContext, CameraActivity2::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val intent = Intent(applicationContext, ResultActivity::class.java)
            try {
                intent.putExtra(ResultActivity.HAND, Dummies.hand8.apply { validate() })
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}
