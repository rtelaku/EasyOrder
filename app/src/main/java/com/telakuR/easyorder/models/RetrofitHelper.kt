package com.telakuR.easyorder.models

import com.telakuR.easyorder.services.FcmService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private lateinit var apiService: FcmService

    fun getFcmServiceApi(): FcmService {
        if (!this::apiService.isInitialized) {
            apiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FcmService::class.java)
        }
        return apiService
    }

    private const val BASE_URL = "https://fcm.googleapis.com/"
    const val SERVER_KEY = "AAAA63ra0-I:APA91bHvsgqYxKAxHlxC2bPSX-uS0uIpB42WYI-9TdON5vEZGOHJU1NK7DL9nMgmLV7FRV8XYgL2FuwqoP1S3ycwNsel96Q4Q40C7L6XyN0wB2I1YRXxNh34oO_azhy__T6ef1u0CQdt"
}