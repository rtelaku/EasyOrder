package com.telakuR.easyorder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.ui.activities.HomeActivity
import com.telakuR.easyorder.models.CreateNotificationModel

object NotificationsUtils {

    fun createNotification(createNotificationModel: CreateNotificationModel) {
        val channelId = "easyOrderChannelId"
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(createNotificationModel.context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(createNotificationModel.context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "easyOrderChannel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)

            val notificationManager = EasyOrder.getInstance().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(createNotificationModel.context, channelId)
            .setSmallIcon(R.drawable.ic_easy_order_logo)
            .setContentTitle(createNotificationModel.title)
            .setContentText(createNotificationModel.message)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSound)

        val notificationManager = NotificationManagerCompat.from(createNotificationModel.context)
        notificationManager.notify(200, notificationBuilder.build())
    }
}