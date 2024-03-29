package com.telakuR.easyorder.main.repository

import com.telakuR.easyorder.room_db.enitites.NotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun saveNotification(notificationModel: NotificationModel?)

    fun getNotificationsFromDB(): Flow<List<NotificationModel>>

    fun getNotificationsFromAPI(): Flow<List<NotificationModel>>

    suspend fun saveNotificationsOnDB(notifications: List<NotificationModel>)
}