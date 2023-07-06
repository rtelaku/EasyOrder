package com.telakuR.easyorder.main.models

import com.telakuR.easyorder.main.services.FcmService
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
    const val SERVER_KEY = ""
}