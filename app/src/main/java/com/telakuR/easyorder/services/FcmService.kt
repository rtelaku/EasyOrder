package com.telakuR.easyorder.services

import com.google.gson.JsonObject
import com.telakuR.easyorder.models.RetrofitHelper.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmService {
    @Headers("Content-Type: application/json", "Authorization: key=$SERVER_KEY")
    @POST("fcm/send")
    fun sendMessage(@Body payload: JsonObject): Call<ResponseBody>
}
