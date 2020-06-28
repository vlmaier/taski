package com.vmaier.taski

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import timber.log.Timber
import java.util.*


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 19:50
 */
class NotificationUtils {

    companion object {
        fun setReminder(
            timeInMs: Long,
            taskId: Long,
            title: String,
            message: String,
            activity: Activity,
            requestCode: Int
        ) {
            val intent = Intent(activity.applicationContext, ReminderReceiver::class.java)
            intent.putExtra("taskId", taskId)
            intent.putExtra("timestamp", timeInMs)
            intent.putExtra("title", title)
            intent.putExtra("message", message)
            val pendingIntent = PendingIntent.getBroadcast(
                activity,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMs
            val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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
    }
}