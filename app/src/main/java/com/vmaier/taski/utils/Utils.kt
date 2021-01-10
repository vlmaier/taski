package com.vmaier.taski.utils

import android.content.Context
import android.graphics.Color
import android.text.format.DateUtils
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.vmaier.taski.R
import com.vmaier.taski.data.Difficulty
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * Created by Vladas Maier
 * on 13.06.2020
 * at 20:49
 */
object Utils {

    @ColorInt
    fun getThemeColor(context: Context, @AttrRes attributeColor: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attributeColor, value, true)
        return value.data
    }

    fun getMaterialColors(context: Context): IntArray {
        val colors = context.resources.obtainTypedArray(R.array.md_colors_300)
        val colorInts = mutableListOf<Int>()
        for (i in 0 until colors.length()) {
            colorInts.add(colors.getColor(i, Color.BLACK))
        }
        colors.recycle()
        return colorInts.toIntArray()
    }

    @ColorInt
    fun getRandomMaterialColor(context: Context): Int {
        val colors = context.resources.obtainTypedArray(R.array.md_colors_300)
        val index = (Math.random() * colors.length()).toInt()
        val randomColor = colors.getColor(index, Color.BLACK);
        colors.recycle()
        return randomColor
    }

    fun getThemeByName(context: Context, theme: String?): Int {
        val resources = context.resources
        return when (theme) {
            resources.getString(R.string.theme_default) -> R.style.Theme_Default
            resources.getString(R.string.theme_sailor) -> R.style.Theme_Sailor
            resources.getString(R.string.theme_royal) -> R.style.Theme_Royal
            resources.getString(R.string.theme_mercury) -> R.style.Theme_Mercury
            resources.getString(R.string.theme_mocca) -> R.style.Theme_Mocca
            resources.getString(R.string.theme_creeper) -> R.style.Theme_Creeper
            resources.getString(R.string.theme_flamingo) -> R.style.Theme_Flamingo
            resources.getString(R.string.theme_pilot) -> R.style.Theme_Pilot
            resources.getString(R.string.theme_coral) -> R.style.Theme_Coral
            resources.getString(R.string.theme_blossom) -> R.style.Theme_Blossom
            else -> R.style.Theme_Default
        }
    }

    fun calculateXp(difficulty: Difficulty, duration: Int): Int {
        val xp = 5 * ((difficulty.factor.times(duration) / 5).roundToInt())
        return if (xp == 0) 5 else xp
    }

    fun getDateSpanFormat(now: Long, time: Long): Long {
        return when (abs(now - time)) {
            in 0..60000 -> DateUtils.SECOND_IN_MILLIS
            in 60001..3600000 -> DateUtils.MINUTE_IN_MILLIS
            in 3600001..86400000 -> DateUtils.HOUR_IN_MILLIS
            in 86400001..604800000 -> DateUtils.DAY_IN_MILLIS
            in 604800001..31449600000 -> DateUtils.WEEK_IN_MILLIS
            in 31449600001..Long.MAX_VALUE -> DateUtils.YEAR_IN_MILLIS
            else -> DateUtils.DAY_IN_MILLIS
        }
    }

    fun getStartOfDay(calendar: Calendar = Calendar.getInstance()): Long {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time.time
    }

    fun getEndOfDay(calendar: Calendar = Calendar.getInstance()): Long {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.time.time
    }
}