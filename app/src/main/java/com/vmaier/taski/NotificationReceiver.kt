package com.vmaier.taski

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 19:37
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, NotificationService::class.java)
        service.putExtra("reason", intent.getStringExtra("reason"))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))
        service.putExtra("title", intent.getStringExtra("title"))
        service.putExtra("message", intent.getStringExtra("message"))
        context.startService(service)
    }
}