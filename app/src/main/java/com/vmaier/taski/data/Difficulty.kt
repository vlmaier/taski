package com.vmaier.taski.data

import android.content.Context
import com.vmaier.taski.R

/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
enum class Difficulty(val value: String, val factor: Double) {
    TRIVIAL("trivial", 0.5),
    REGULAR("regular", 1.0),
    HARD("hard", 1.5),
    INSANE("insane", 2.0);

    fun getName(context: Context): String {
        return when (this) {
            TRIVIAL -> context.getString(R.string.difficulty_trivial_value)
            REGULAR -> context.getString(R.string.difficulty_regular_value)
            HARD    -> context.getString(R.string.difficulty_hard_value)
            INSANE  -> context.getString(R.string.difficulty_insane_value)
        }
    }
}