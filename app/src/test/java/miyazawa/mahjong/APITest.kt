package miyazawa.mahjong

import miyazawa.mahjong.model.Hand
import miyazawa.mahjong.model.ResultHand
import miyazawa.mahjong.utils.ApiClientManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class APITest {

    @Test
    fun FileTest() {
        val file = File("c:/AndroidStudioProjects/Mahjong/app/src/main/res/drawable/valid_hand.jpg")
        print(file.absolutePath)
        assertTrue(file.exists())
    }

    @Test
    fun apiTest001 () {
        val map: MutableMap<String, RequestBody> = hashMapOf()
        val image = File("c:/AndroidStudioProjects/Mahjong/app/src/main/res/drawable/valid_hand.jpg")
//        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), image)
        val body = image.asRequestBody("image/jpg".toMediaTypeOrNull())
        map.put("upload\"; filename=\"image.jpg\"", body)
        val response = ApiClientManager.apiClient.upload(map).execute()
        val resultHand: ResultHand? = response.body()
        val str = resultHand?.toString()
        print(str)
        val hand = Hand(resultHand!!)
        assertTrue(hand.validate())
    }
}