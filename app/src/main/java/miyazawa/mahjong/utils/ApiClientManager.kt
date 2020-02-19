package miyazawa.mahjong.utils

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class ApiClientManager {
    companion object {
        private const val ENDPOINT = "http://192.168.0.24:8080/"
        private val TAG = ApiClientManager::class.simpleName

        val apiClient: ApiClient
        get() = Retrofit.Builder()
            .client(getClient())
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiClient::class.java)

        private fun getClient(): OkHttpClient {
            return OkHttpClient
                .Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
//                .addInterceptor(HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                })
                .build()
        }
    }

}