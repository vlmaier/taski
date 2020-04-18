package org.vmaier.tidfl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DurationUnit
import org.vmaier.tidfl.data.entity.Task


/**
 * Created by Vladas Maier
 * on 07/02/2020.
 * at 21:03
 */

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }
    val bitmap = Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Drawable?.setThemeTint(context: Context) {

    if (this == null) return
    this.clearColorFilter()
    DrawableCompat.setTint(this, ContextCompat.getColor(context, R.color.colorSecondary))
}

fun SeekBar.getHumanReadableValue(): String {
    return when (this.progress) {
        1 -> "5 minutes"
        2 -> "10 minutes"
        3 -> "15 minutes"
        4 -> "30 minutes"
        5 -> "45 minutes"
        6 -> "1 hour"
        7 -> "2 hours"
        8 -> "3 hours"
        9 -> "4 hours"
        10 -> "6 hours"
        11 -> "8 hours"
        12 -> "12 hours"
        13 -> "16 hours"
        14 -> "1 day"
        15 -> "2 days"
        16 -> "3 days"
        17 -> "4 days"
        18 -> "5 days"
        19 -> "6 days"
        20 -> "1 week"
        else -> ""
    }
}

fun SeekBar.getDurationInMinutes(): Int {
    return when (this.progress) {
        1 -> 5                  // 5 minutes
        2 -> 10                 // 10 minutes
        3 -> 15                 // 15 minutes
        4 -> 30                 // 30 minutes
        5 -> 45                 // 45 minutes
        6 -> 60                 // 1 hour
        7 -> 120                // 2 hours
        8 -> 180                // 3 hours
        9 -> 240                // 4 hours
        10 -> 360               // 6 hours
        11 -> 480               // 8 hours
        12 -> 720               // 12 hours
        13 -> 960               // 16 hours
        14 -> 1440              // 1 day
        15 -> 2880              // 2 days
        16 -> 4320              // 3 days
        17 -> 5760              // 4 days
        18 -> 7200              // 5 days
        19 -> 8640              // 6 days
        20 -> 10080             // 1 week
        else -> 0
    }
}

fun Task.getSeekBarValue(): Int {
    return when (this.duration) {
        5 -> 1                  // 5 minutes
        10 -> 2                 // 10 minutes
        15 -> 3                 // 15 minutes
        30 -> 4                 // 30 minutes
        45 -> 5                 // 45 minutes
        60 -> 6                 // 1 hour
        120 -> 7                // 2 hours
        180 -> 8                // 3 hours
        240 -> 9                // 4 hours
        360 -> 10               // 6 hours
        480 -> 11               // 8 hours
        720 -> 12               // 12 hours
        960 -> 13               // 16 hours
        1440 -> 14              // 1 day
        2880 -> 15              // 2 days
        4320 -> 16              // 3 days
        5760 -> 17              // 4 days
        7200 -> 18              // 5 days
        8640 -> 19              // 6 days
        10080 -> 20             // 1 week
        else -> 0
    }
}

fun Task.getDurationUnit(): DurationUnit {
    return when (this.duration) {
        in 5..45 -> DurationUnit.MINUTE
        in 60..960 -> DurationUnit.HOUR
        in 1440..8640 -> DurationUnit.DAY
        else -> DurationUnit.WEEK
    }
}

fun Task.convertDurationToMinutes(unit: DurationUnit): Int {
    return when (unit) {
        DurationUnit.MINUTE -> this.duration
        DurationUnit.HOUR -> this.duration.div(60)
        DurationUnit.DAY -> this.duration.div(60).div(24)
        DurationUnit.WEEK -> this.duration.div(60).div(24).div(7)
    }
}

fun Task.getHumanReadableDurationValue(): String {
    val unit = this.getDurationUnit()
    return "${this.convertDurationToMinutes(unit)} " + when (unit) {
        DurationUnit.MINUTE -> "min"
        DurationUnit.HOUR -> "h"
        DurationUnit.DAY -> "d"
        DurationUnit.WEEK -> "w"
    }
}
