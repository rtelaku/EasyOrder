package com.telakuR.easyorder.main.repository.impl

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.telakuR.easyorder.main.enums.DBCollectionEnum
import com.telakuR.easyorder.main.repository.NotificationsRepository
import com.telakuR.easyorder.main.models.NotificationModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService
): NotificationsRepository {

    private val TAG = NotificationsRepositoryImpl::class.simpleName

    override fun saveNotification(notificationModel: NotificationModel?) {
        if(notificationModel != null) {
            val docRef = fireStore.collection(DBCollectionEnum.NOTIFICATIONS.title)
                .document(accountService.currentUserId)

            docRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val document = task.result
                    if(document.exists()) {
                        docRef.update(Constants.NOTIFICATIONS, FieldValue.arrayUnion(notificationModel))
                    } else {
                        docRef.set(mapOf(Constants.NOTIFICATIONS to listOf(notificationModel)))
                    }
                } else {
                    Log.d(TAG, "Couldn't save notification: ${task.exception}")
                }
            }
        }
    }

    override fun getNotifications(): Flow<List<NotificationModel>> = callbackFlow {
        val docRef = fireStore.collection(DBCollectionEnum.NOTIFICATIONS.title)
            .document(accountService.currentUserId)

        val subscription = docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "Couldn't get notifications: $exception")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val jsonNotificationsList = snapshot.get(Constants.NOTIFICATIONS) as List<Map<String, Any>>

                val notifications = jsonNotificationsList.map { map ->
                    val id = map["id"].toString().toDouble()
                    val ownerName = map["ownerName"] as String
                    val fastFood = map["fastFood"] as? String
                    val currentTimeInMillis = map["currentTimeInMillis"] as? Long
                    NotificationModel(id, ownerName, fastFood, currentTimeInMillis)
                }

                this.trySend(notifications.reversed()).isSuccess
            } else {
                this.trySend(emptyList<NotificationModel>()).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }.flowOn(ioDispatcher)

}