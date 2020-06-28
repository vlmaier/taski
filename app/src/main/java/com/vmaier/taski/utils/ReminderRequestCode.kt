package com.vmaier.taski.utils

import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Vladas Maier
 * on 28.06.2020
 * at 16:02
 */
class ReminderRequestCode {
    companion object {
        private val c: AtomicInteger = AtomicInteger(1)
        fun getRequestCode(): Int {
            return c.incrementAndGet()
        }
    }
}