package com.vmaier.taski.utils

import android.content.Context
import com.vmaier.taski.services.PreferenceService
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Vladas Maier
 * on 28.06.2020
 * at 16:02
 */
class RequestCode {

    companion object {
        fun get(context: Context): Int {
            val prefService = PreferenceService(context)
            val current = AtomicInteger(
                prefService.getRequestCodeCounter()
            )
            val next = current.incrementAndGet()
            prefService.setRequestCodeCounter(next)
            return next
        }
    }
}