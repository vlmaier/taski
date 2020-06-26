package com.vmaier.taski

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import java.util.*


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 19:50
 */
class NotificationUtils {

    fun setNotification(timeInMilliSeconds: Long, title: String, message: String, activity: Activity) {
        if (timeInMilliSeconds > 0) {
            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, NotificationReceiver::class.java)

            alarmIntent.putExtra("timestamp", timeInMilliSeconds)
            alarmIntent.putExtra("title", title)
            alarmIntent.putExtra("message", message)
            alarmIntent.putExtra("reason", "notification")

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds

            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
}