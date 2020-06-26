package com.vmaier.taski

import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Vladas Maier
 * on 26.06.2020
 * at 20:01
 */
class NotificationID {

    companion object {
        private val c: AtomicInteger = AtomicInteger(0)
        fun getID(): Int {
            return c.incrementAndGet()
        }
    }
}