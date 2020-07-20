package com.vmaier.taski.features.reminders

import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 20:01
 */
class NotificationId {

    companion object {
        private val c: AtomicInteger = AtomicInteger(1)
        fun getId(): Int {
            return c.incrementAndGet()
        }
    }
}