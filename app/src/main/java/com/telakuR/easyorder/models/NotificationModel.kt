package com.telakuR.easyorder.models

data class NotificationModel(val id: Double, val ownerName: String, val fastFood: String? = null, val currentTimeInMillis: Long? = null)