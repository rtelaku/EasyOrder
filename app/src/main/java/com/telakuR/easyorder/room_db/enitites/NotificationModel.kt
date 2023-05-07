package com.telakuR.easyorder.room_db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_table")
data class NotificationModel(
    @PrimaryKey val id: Double,
    val ownerName: String,
    val fastFood: String? = null,
    val currentTimeInMillis: Long? = null
)