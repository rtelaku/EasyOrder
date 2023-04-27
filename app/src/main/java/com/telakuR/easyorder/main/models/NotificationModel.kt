package com.telakuR.easyorder.main.models

import android.content.Context

data class NotificationModel(val id: Double, val ownerName: String, val fastFood: String? = null, val currentTimeInMillis: Long? = null)
data class CreateNotificationModel(val title: String, val message: String, val context: Context)