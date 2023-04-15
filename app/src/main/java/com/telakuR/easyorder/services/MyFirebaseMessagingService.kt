package com.telakuR.easyorder.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.telakuR.easyorder.R
import com.telakuR.easyorder.models.RetrofitHelper
import com.telakuR.easyorder.modules.EasyOrderEntryPoint
import com.telakuR.easyorder.utils.EasyOrder
import com.telakuR.easyorder.utils.NotificationsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        fun sendMessage(fastFood: String) {
            CoroutineScope(IO).launch {
                val userDataRepositoryImpl = EasyOrderEntryPoint.getUserDataImplService()
                val deviceTokens = userDataRepositoryImpl.getTokens()
                val employeeName = userDataRepositoryImpl.getProfile()?.name
                val fcmService = RetrofitHelper.getFcmServiceApi()

                val context = EasyOrder.getInstance().applicationContext
                val title = context.getString(R.string.new_order)
                val message = String.format(context.getString(R.string.ordering_notification), employeeName, fastFood)

                deviceTokens.forEach { token ->
                    try {
                        val notification = JsonObject().apply {
                            addProperty("body", message)
                            addProperty("title", title)
                        }

                        val data = JsonObject().apply {
                            addProperty("message", message)
                        }

                        val payload = JsonObject().apply {
                            add("notification", notification)
                            add("data", data)
                            addProperty("to", token)
                        }

                        val response = fcmService.sendMessage(payload).execute()
                        Log.d("rigiii", "sendMessage: $response")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val message = remoteMessage.data["message"] ?: ""
        NotificationsUtils.createOrderNotification(message)
    }
}
