package com.vmaier.taski

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 19:37
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationService.ACTION_DISMISS -> {
                Timber.d("Dismiss pressed.")
                val notificationId = intent.getIntExtra("notificationId", 1)
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            }
            else -> {
                val service = Intent(context, NotificationService::class.java)
                service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))
                service.putExtra("title", intent.getStringExtra("title"))
                service.putExtra("message", intent.getStringExtra("message"))
                Timber.d("Reminder received: ${intent.extras.toString()}")
                context.startService(service)
            }
        }
    }
}