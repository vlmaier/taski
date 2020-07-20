package com.vmaier.taski

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vmaier.taski.NotificationUtils.Companion.KEY_MESSAGE
import com.vmaier.taski.NotificationUtils.Companion.KEY_NOTIFICATION_ID
import com.vmaier.taski.NotificationUtils.Companion.KEY_TIMESTAMP
import com.vmaier.taski.NotificationUtils.Companion.KEY_TITLE
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
                val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1)
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            }
            else -> {
                val service = Intent(context, NotificationService::class.java)
                service.putExtra(KEY_TIMESTAMP, intent.getLongExtra(KEY_TIMESTAMP, 0))
                service.putExtra(KEY_TITLE, intent.getStringExtra(KEY_TITLE))
                service.putExtra(KEY_MESSAGE, intent.getStringExtra(KEY_MESSAGE))
                Timber.d("Reminder received: ${intent.extras.toString()}")
                context.startService(service)
            }
        }
    }
}