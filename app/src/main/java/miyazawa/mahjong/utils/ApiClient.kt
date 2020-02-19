package miyazawa.mahjong.utils

import io.reactivex.Observable
import miyazawa.mahjong.model.ResultHand
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiClient {
    @GET("result")
    fun getResult(): Observable<ResultHand>

    @Multipart
    @JvmSuppressWildcards
    @POST("upload")
    fun upload(
        @PartMap params: Map<String, RequestBody>): Call<ResultHand>

}