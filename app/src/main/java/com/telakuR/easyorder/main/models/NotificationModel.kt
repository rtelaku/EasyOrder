package com.telakuR.easyorder.main.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_table")
data class NotificationModel(@PrimaryKey val id: Double, val ownerName: String, val fastFood: String? = null, val currentTimeInMillis: Long? = null)
data class CreateNotificationModel(val title: String, val message: String, val context: Context)