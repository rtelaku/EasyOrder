package com.telakuR.easyorder.utils

import android.app.Application

class EasyOrder : Application() {
    override fun onCreate() {
        super.onCreate()
        easyOrder = this
    }

    companion object {
        private lateinit var easyOrder: EasyOrder
        fun getInstance(): EasyOrder {
            return easyOrder
        }
    }
}

