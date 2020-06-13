package org.vmaier.tidfl.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt


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
}