package com.vmaier.taski.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.vmaier.taski.R


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

    fun getThemeByName(context: Context, theme: String?): Int {
        val resources = context.resources
        return when (theme) {
            resources.getString(R.string.theme_default_name) -> R.style.Theme_Default
            resources.getString(R.string.theme_sailor_name) -> R.style.Theme_Sailor
            resources.getString(R.string.theme_royal_name) -> R.style.Theme_Royal
            resources.getString(R.string.theme_mercury_name) -> R.style.Theme_Mercury
            resources.getString(R.string.theme_mocca_name) -> R.style.Theme_Mocca
            resources.getString(R.string.theme_creeper_name) -> R.style.Theme_Creeper
            resources.getString(R.string.theme_flamingo_name) -> R.style.Theme_Flamingo
            resources.getString(R.string.theme_pilot_name) -> R.style.Theme_Pilot
            resources.getString(R.string.theme_coral_name) -> R.style.Theme_Coral
            resources.getString(R.string.theme_blossom_name) -> R.style.Theme_Blossom
            else -> R.style.Theme_Default
        }
    }
}