package com.telakuR.easyorder.main.repository

import com.telakuR.easyorder.main.models.NotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun saveNotification(notificationModel: NotificationModel?)

    fun getNotifications(): Flow<List<NotificationModel>>
}