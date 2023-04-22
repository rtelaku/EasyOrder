package com.telakuR.easyorder.mainRepository

import com.telakuR.easyorder.models.NotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun saveNotification(notificationModel: NotificationModel?)

    fun getNotifications(): Flow<List<NotificationModel>>
}