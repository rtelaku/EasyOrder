package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.main.models.NotificationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {

    @Query("SELECT * FROM notifications_table")
    fun getNotifications(): Flow<List<NotificationModel>>

    @Transaction
    suspend fun deleteAndInsertNotifications(notifications: List<NotificationModel>) {
        deleteNotifications()
        insertNotifications(notifications = notifications)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotifications(notifications: List<NotificationModel>)

    @Query("DELETE FROM notifications_table")
    suspend fun deleteNotifications()

}