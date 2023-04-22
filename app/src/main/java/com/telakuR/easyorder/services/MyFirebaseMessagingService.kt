package com.telakuR.easyorder.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.telakuR.easyorder.R
import com.telakuR.easyorder.enums.NotificationTypeEnum
import com.telakuR.easyorder.models.NotificationModel
import com.telakuR.easyorder.models.RetrofitHelper
import com.telakuR.easyorder.modules.EasyOrderEntryPoint
import com.telakuR.easyorder.utils.EasyOrder
import com.telakuR.easyorder.utils.EasyOrderPreferences
import com.telakuR.easyorder.utils.NotificationsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        fun sendNewOrderMessage(fastFood: String) {
            CoroutineScope(IO).launch {
                val userDataRepositoryImpl = EasyOrderEntryPoint.getUserDataImplService()
                val deviceTokens = userDataRepositoryImpl.getTokens()
                val myDeviceToken = EasyOrderPreferences.getCurrentDeviceToken()
                val ownerName = userDataRepositoryImpl.getProfile()?.name
                val fcmService = RetrofitHelper.getFcmServiceApi()

                val context = EasyOrder.getInstance().applicationContext
                val title = context.getString(R.string.new_order)
                val message = String.format(context.getString(R.string.ordering_notification), ownerName, fastFood)

                deviceTokens.forEach { token ->
                    if(myDeviceToken != token) {
                        try {
                            val notification = JsonObject().apply {
                                addProperty("body", message)
                                addProperty("title", title)
                            }

                            val data = JsonObject().apply {
                                addProperty("owner_name", ownerName)
                                addProperty("fast_food", fastFood)
                                addProperty("notification_type", NotificationTypeEnum.NEW_ORDER.id)
                            }

                            val payload = JsonObject().apply {
                                add("notification", notification)
                                add("data", data)
                                addProperty("to", token)
                            }

                            fcmService.sendMessage(payload).execute()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationsRepositoryImpl = EasyOrderEntryPoint.getNotificationsImplService()
        val notificationType = remoteMessage.data["notification_type"]?.toDouble()
        val ownerName = remoteMessage.data["owner_name"] ?: ""
        var notificationModel: NotificationModel? = null
        val currentTimeInMillis = System.currentTimeMillis()

        when(notificationType) {
            NotificationTypeEnum.NEW_ORDER.id -> {
                val message = remoteMessage.data["message"] ?: ""
                val fastFood = remoteMessage.data["fast_food"] ?: ""

                NotificationsUtils.createOrderNotification(message)
                notificationModel = NotificationModel(id = notificationType, ownerName = ownerName, fastFood = fastFood, currentTimeInMillis = currentTimeInMillis)
            }
        }

        notificationsRepositoryImpl.saveNotification(notificationModel)
    }
}
