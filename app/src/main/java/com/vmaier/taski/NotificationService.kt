package com.vmaier.taski

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import com.vmaier.taski.utils.NotificationId
import com.vmaier.taski.utils.RequestCode
import com.vmaier.taski.utils.Utils
import timber.log.Timber
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 19:40
 */
class NotificationService : IntentService("NotificationService") {

    private lateinit var notification: Notification

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = this.applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.enableVibration(true)
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.lightColor = Utils.getThemeColor(context, R.attr.colorPrimary)
            channel.description = "Task reminders"
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.vmaier.taski.default.channel"
        const val CHANNEL_NAME = "Task reminders"
        const val ACTION_DISMISS = "taski.action.dismiss"
        var ACTION_TAP_REQUEST_CODE by Delegates.notNull<Int>()
        var ACTION_DISMISS_REQUEST_CODE by Delegates.notNull<Int>()
    }

    override fun onHandleIntent(intent: Intent?) {
        createChannel()

        var timestamp: Long = 0
        var title = ""
        var message = ""
        if (intent != null && intent.extras != null) {
            val extras: Bundle = intent.extras!!
            timestamp = extras.getLong("timestamp")
            title = extras.getString("title", "")
            message = extras.getString("message", "")
        }

        if (timestamp > 0) {
            val context = this.applicationContext
            val notifyIntent = Intent(this, MainActivity::class.java)
            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("message", message)
            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            ACTION_TAP_REQUEST_CODE = RequestCode.get(context)
            ACTION_DISMISS_REQUEST_CODE = RequestCode.get(context)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val pendingIntent = PendingIntent.getActivity(
                context,
                ACTION_TAP_REQUEST_CODE,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val res = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationId = NotificationId.getId()

            val dismissIntent = Intent(this, ReminderReceiver::class.java).apply {
                action = ACTION_DISMISS
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("title", title)
                putExtra("message", message)
                putExtra("timestamp", timestamp)
                putExtra("notificationId", notificationId)
            }
            val dismissPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(this, ACTION_DISMISS_REQUEST_CODE, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val dismissAction: Notification.Action = Notification.Action.Builder(
                R.drawable.ic_baseline_access_time_24, "Dismiss", dismissPendingIntent)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = Notification.Builder(this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_stat_taski)
                    .setColor(Utils.getThemeColor(context, R.attr.colorPrimary))
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(message)
                    )
                    .setContentText(message)
                    .addAction(dismissAction)
                    .build()
            } else {
                notification = Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_stat_taski)
                    .setColor(Utils.getThemeColor(context, R.attr.colorPrimary))
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(message)
                    )
                    .setSound(uri)
                    .setContentText(message)
                    .addAction(dismissAction)
                    .build()
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
            Timber.d("Notification sent: $notification")
        }
    }
}