package com.vmaier.taski.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.features.reminders.NotificationId
import com.vmaier.taski.features.reminders.ReminderReceiver
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

    companion object {
        const val CHANNEL_ID = "com.vmaier.taski.default.channel"
        const val CHANNEL_NAME = "Task reminders"
        const val ACTION_DISMISS = "taski.action.dismiss"

        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_NOTIFICATION_ID = "notificationId"

        var ACTION_TAP_REQUEST_CODE by Delegates.notNull<Int>()
        var ACTION_DISMISS_REQUEST_CODE by Delegates.notNull<Int>()
    }

    override fun onHandleIntent(intent: Intent?) {
        createChannel()

        val timestamp = intent?.extras?.getLong(KEY_TIMESTAMP) ?: 0
        val title = intent?.extras?.getString(KEY_TITLE, "") ?: ""
        val message = intent?.extras?.getString(KEY_MESSAGE, "") ?: ""

        if (timestamp > 0) {
            val context = this.applicationContext
            val notifyIntent = Intent(this, MainActivity::class.java)
            notifyIntent.putExtra(KEY_TITLE, title)
            notifyIntent.putExtra(KEY_MESSAGE, message)
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

            val notificationId = NotificationId.getId()
            val dismissIntent = Intent(this, ReminderReceiver::class.java).apply {
                action = ACTION_DISMISS
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(KEY_TITLE, title)
                putExtra(KEY_MESSAGE, message)
                putExtra(KEY_TIMESTAMP, timestamp)
                putExtra(KEY_NOTIFICATION_ID, notificationId)
            }
            val dismissPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this,
                ACTION_DISMISS_REQUEST_CODE,
                dismissIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val dismissAction: Notification.Action = Notification.Action.Builder(
                R.drawable.ic_time_24, getString(R.string.action_dismiss),
                dismissPendingIntent
            ).build()

            val resources = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notification = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    Notification.Builder(this, CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_stat_taski)
                        .setColor(Utils.getThemeColor(context, R.attr.colorPrimary))
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setStyle(Notification.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .addAction(dismissAction)
                        .build()
                }
                else -> {
                    Notification.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_stat_taski)
                        .setColor(Utils.getThemeColor(context, R.attr.colorPrimary))
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(title)
                        .setStyle(Notification.BigTextStyle().bigText(message))
                        .setSound(uri)
                        .setContentText(message)
                        .addAction(dismissAction)
                        .build()
                }
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
            Timber.d("Notification sent: $notification")
        }
    }

    fun setReminder(timeInMs: Long, title: String, message: String, activity: Activity, requestCode: Int) {
        val intent = Intent(activity.applicationContext, ReminderReceiver::class.java)
        intent.putExtra(KEY_TIMESTAMP, timeInMs)
        intent.putExtra(KEY_TITLE, title)
        intent.putExtra(KEY_MESSAGE, message)
        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMs
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val type = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(type, calendar.timeInMillis, pendingIntent)
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                alarmManager.setExact(type, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.set(type, calendar.timeInMillis, pendingIntent)
            }
        }
        Timber.d("Reminder for '$title' at ${calendar.time} created.")
    }

    fun cancelReminder(activity: Activity, requestCode: Int?) {
        if (requestCode != null) {
            val intent = Intent(activity.applicationContext, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                activity,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            Timber.d("Reminder canceled.")
        }
    }

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
            channel.description = CHANNEL_NAME
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
    }
}