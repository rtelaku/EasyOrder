package com.telakuR.easyorder.main.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.telakuR.easyorder.R
import com.telakuR.easyorder.main.enums.NotificationTypeEnum
import com.telakuR.easyorder.main.models.CreateNotificationModel
import com.telakuR.easyorder.main.models.RetrofitHelper
import com.telakuR.easyorder.modules.EasyOrderEntryPoint
import com.telakuR.easyorder.room_db.enitites.NotificationModel
import com.telakuR.easyorder.utils.Constants.BODY
import com.telakuR.easyorder.utils.Constants.COMPANY_ID
import com.telakuR.easyorder.utils.Constants.DATA
import com.telakuR.easyorder.utils.Constants.FAST_FOOD
import com.telakuR.easyorder.utils.Constants.MESSAGE
import com.telakuR.easyorder.utils.Constants.NOTIFICATION
import com.telakuR.easyorder.utils.Constants.NOTIFICATION_TYPE
import com.telakuR.easyorder.utils.Constants.OWNER
import com.telakuR.easyorder.utils.Constants.TITLE
import com.telakuR.easyorder.utils.Constants.TO
import com.telakuR.easyorder.utils.EasyOrder
import com.telakuR.easyorder.utils.EasyOrderPreferences
import com.telakuR.easyorder.utils.NotificationsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val fcmService = RetrofitHelper.getFcmServiceApi()
        private val userDataRepositoryImpl = EasyOrderEntryPoint.getUserDataImplService()
        private val scope: CoroutineScope = CoroutineScope(IO + SupervisorJob())

        fun sendNewOrderMessage(fastFood: String) {
            scope.launch {
                val companyId = userDataRepositoryImpl.getCompanyId() ?: ""
                val deviceTokens = userDataRepositoryImpl.getTokens(companyId)
                val myDeviceToken = EasyOrderPreferences.getCurrentDeviceToken()
                val ownerName = userDataRepositoryImpl.getProfileFromDB()?.name

                val context = EasyOrder.getInstance().applicationContext
                val title = context.getString(R.string.new_order)
                val message = String.format(context.getString(R.string.ordering_notification), ownerName, fastFood)

                deviceTokens.forEach { token ->
                    if(myDeviceToken != token) {
                        try {
                            val notification = JsonObject().apply {
                                addProperty(BODY, message)
                                addProperty(TITLE, title)
                            }

                            val data = JsonObject().apply {
                                addProperty(OWNER, ownerName)
                                addProperty(FAST_FOOD, fastFood)
                                addProperty(NOTIFICATION_TYPE, NotificationTypeEnum.NEW_ORDER.id)
                            }

                            val payload = JsonObject().apply {
                                add(NOTIFICATION, notification)
                                add(DATA, data)
                                addProperty(TO, token)
                            }

                            fcmService.sendMessage(payload).execute()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        fun sendNewMenuItemMessage(ownerId: String) {
            scope.launch {
                val ownerDeviceToken = userDataRepositoryImpl.getOrderOwnerDeviceToken(ownerId = ownerId)
                val myDeviceToken = EasyOrderPreferences.getCurrentDeviceToken()

                if (ownerDeviceToken != null && ownerDeviceToken != myDeviceToken) {
                    val ownerName = userDataRepositoryImpl.getProfileFromDB()?.name

                    val context = EasyOrder.getInstance().applicationContext
                    val title = context.getString(R.string.new_menu_item_added)
                    val message = String.format(context.getString(R.string.employee_added_menu_item), ownerName)

                    try {
                        val notification = JsonObject().apply {
                            addProperty(BODY, message)
                            addProperty(TITLE, title)
                        }

                        val data = JsonObject().apply {
                            addProperty(OWNER, ownerName)
                            addProperty(
                                NOTIFICATION_TYPE,
                                NotificationTypeEnum.NEW_MENU_ITEM.id
                            )
                        }

                        val payload = JsonObject().apply {
                            add(NOTIFICATION, notification)
                            add(DATA, data)
                            addProperty(TO, ownerDeviceToken)
                        }

                        fcmService.sendMessage(payload).execute()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun sendRequestStateMessage(employeeId: String, hasBeenAccepted: Boolean, ownerId: String = "") {
            scope.launch {
                val employeeDeviceToken = userDataRepositoryImpl.getOrderOwnerDeviceToken(ownerId = employeeId)
                val myDeviceToken = EasyOrderPreferences.getCurrentDeviceToken()

                if (employeeDeviceToken != null && employeeDeviceToken != myDeviceToken) {
                    val ownerName = userDataRepositoryImpl.getProfileFromDB()?.name

                    val context = EasyOrder.getInstance().applicationContext
                    val titleMessageId = if(hasBeenAccepted) R.string.you_have_been_accepted else R.string.you_have_been_not_accepted
                    val title = context.getString(titleMessageId)

                    val messageId = if(hasBeenAccepted) R.string.you_have_been_accepted_to_company else R.string.you_have_been_declined_from_company
                    val message = String.format(context.getString(messageId), ownerName)

                    val notificationType = if(hasBeenAccepted) NotificationTypeEnum.ACCEPTED_TO_COMPANY.id else NotificationTypeEnum.REJECTED_FROM_COMPANY.id

                    try {
                        val notification = JsonObject().apply {
                            addProperty(BODY, message)
                            addProperty(TITLE, title)
                        }

                        val data = JsonObject().apply {
                            addProperty(OWNER, ownerName)
                            addProperty(COMPANY_ID, ownerId)
                            addProperty(NOTIFICATION_TYPE, notificationType)
                        }

                        val payload = JsonObject().apply {
                            add(NOTIFICATION, notification)
                            add(DATA, data)
                            addProperty(TO, employeeDeviceToken)
                        }

                        fcmService.sendMessage(payload).execute()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun sendNewEmployeeRequestMessage(companyId: String) {
            scope.launch {
                val ownerDeviceToken = userDataRepositoryImpl.getOrderOwnerDeviceToken(ownerId = companyId)
                val myDeviceToken = EasyOrderPreferences.getCurrentDeviceToken()

                if (ownerDeviceToken != null && ownerDeviceToken != myDeviceToken) {
                    val ownerName = userDataRepositoryImpl.getProfileFromDB()?.name

                    val context = EasyOrder.getInstance().applicationContext
                    val title = context.getString(R.string.new_request)
                    val message = String.format(context.getString(R.string.employee_requesting_to_join_company), ownerName)

                    try {
                        val notification = JsonObject().apply {
                            addProperty(BODY, message)
                            addProperty(TITLE, title)
                        }

                        val data = JsonObject().apply {
                            addProperty(OWNER, ownerName)
                            addProperty(
                                NOTIFICATION_TYPE,
                                NotificationTypeEnum.NEW_EMPLOYEE_REQUEST.id
                            )
                        }

                        val payload = JsonObject().apply {
                            add(NOTIFICATION, notification)
                            add(DATA, data)
                            addProperty(TO, ownerDeviceToken)
                        }

                        fcmService.sendMessage(payload).execute()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationsRepositoryImpl = EasyOrderEntryPoint.getNotificationsImplService()
        val notificationType = remoteMessage.data[NOTIFICATION_TYPE]?.toDouble()
        val ownerName = remoteMessage.data[OWNER] ?: ""
        val message = remoteMessage.data[MESSAGE] ?: ""

        var notificationModel: NotificationModel? = null
        val currentTimeInMillis = System.currentTimeMillis()

        val context = EasyOrder.getInstance().applicationContext
        var title = ""

        when(notificationType) {
            NotificationTypeEnum.NEW_ORDER.id -> {
                val fastFood = remoteMessage.data[FAST_FOOD] ?: ""
                title = context.getString(R.string.new_order)
                notificationModel = NotificationModel(id = notificationType, ownerName = ownerName, fastFood = fastFood, currentTimeInMillis = currentTimeInMillis)
            }
            NotificationTypeEnum.NEW_MENU_ITEM.id -> {
                title = context.getString(R.string.new_menu_item_added)
                notificationModel = NotificationModel(id = notificationType, ownerName = ownerName, currentTimeInMillis = currentTimeInMillis)
            }
            NotificationTypeEnum.ACCEPTED_TO_COMPANY.id -> {
                title = context.getString(R.string.you_have_been_accepted)
                notificationModel = NotificationModel(id = notificationType, ownerName = ownerName, currentTimeInMillis = currentTimeInMillis)
                EasyOrderPreferences.saveRequestedCompanyId(companyId = "")

                scope.launch {
                    val companyId = remoteMessage.data[COMPANY_ID] ?: ""
                    EasyOrderEntryPoint.getEasyOrderDB().profileDao().setCompanyId(companyId = companyId)
                }
            }
            NotificationTypeEnum.REJECTED_FROM_COMPANY.id -> {
                title = context.getString(R.string.you_have_been_not_accepted)
                notificationModel = NotificationModel(id = notificationType, ownerName = ownerName, currentTimeInMillis = currentTimeInMillis)
                EasyOrderPreferences.saveRequestedCompanyId(companyId = "")
            }
            NotificationTypeEnum.NEW_EMPLOYEE_REQUEST.id -> {
                title = context.getString(R.string.new_request)
                notificationModel = NotificationModel(id = notificationType, ownerName = ownerName, currentTimeInMillis = currentTimeInMillis)
            }
        }

        NotificationsUtils.createNotification(CreateNotificationModel(title = title, message = message, context = context))
        notificationsRepositoryImpl.saveNotification(notificationModel)
    }
}
