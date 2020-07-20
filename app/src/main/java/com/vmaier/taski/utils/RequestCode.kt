package com.vmaier.taski.utils

import android.content.Context
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.vmaier.taski.Constants
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Vladas Maier
 * on 28.06.2020
 * at 16:02
 */
class RequestCode {

    companion object {
        fun get(context: Context): Int {
            val sharedPreferences = getDefaultSharedPreferences(context)
            val current = AtomicInteger(
                sharedPreferences.getInt(Constants.Prefs.REQUEST_CODE_COUNTER, 1)
            )
            val next = current.incrementAndGet()
            sharedPreferences.edit().putInt(Constants.Prefs.REQUEST_CODE_COUNTER, next).apply()
            return next
        }
    }
}