package com.vmaier.taski

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
            NotificationService.ACTION_SNOOZE -> {
                // TODO: handle action
            }
            NotificationService.ACTION_DONE -> {
                // TODO: handle action
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