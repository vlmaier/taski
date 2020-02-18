package org.vmaier.tidfl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
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

fun DurationUnit.getResourceArrayId(): Int {
    return when (this) {
        DurationUnit.MINUTES -> R.array.duration_minutes
        DurationUnit.HOURS -> R.array.duration_hours
        DurationUnit.DAYS -> R.array.duration_days
    }
}

fun Task.getPosForUnitSpinner(): Int {
    return when (this.getDurationUnit()) {
        DurationUnit.MINUTES -> 0
        DurationUnit.HOURS -> 1
        DurationUnit.DAYS -> 2
    }
}

fun Task.getHumanReadableValue(): String {
    val unit = this.getDurationUnit()
    return "${this.convertToSpinnerValue(unit)} " + when (unit) {
        DurationUnit.MINUTES -> "min"
        DurationUnit.HOURS -> "h"
        DurationUnit.DAYS -> "d"
    }
}

fun Task.getPosForValueSpinner(): Int {
    return when (this.getPosForUnitSpinner()) {
        0 -> when (this.convertToSpinnerValue(DurationUnit.MINUTES)) {
            5 -> 0
            10 -> 1
            15 -> 2
            30 -> 3
            45 -> 4
            else -> 0
        }
        1 -> when (this.convertToSpinnerValue(DurationUnit.HOURS)) {
            1 -> 0
            2 -> 1
            3 -> 2
            4 -> 3
            8 -> 4
            12 -> 5
            16 -> 6
            20 -> 7
            else -> 0
        }
        2 -> when (this.convertToSpinnerValue(DurationUnit.DAYS)) {
            1 -> 0
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            7 -> 6
            else -> 0
        }
        else -> 0
    }
}

fun Task.getPosForDifficultySpinner(): Int {
    return when (this.difficulty) {
        Difficulty.TRIVIAL -> 0
        Difficulty.REGULAR -> 1
        Difficulty.HARD -> 2
        Difficulty.INSANE -> 3
    }
}

fun Task.getDurationUnit(): DurationUnit {
    return when (this.duration) {
        in 5..45 -> DurationUnit.MINUTES
        in 60..1200 -> DurationUnit.HOURS
        else -> DurationUnit.DAYS
    }
}

fun Task.convertToSpinnerValue(unit: DurationUnit): Int {
    return when (unit) {
        DurationUnit.MINUTES -> this.duration
        DurationUnit.HOURS -> this.duration.div(60)
        DurationUnit.DAYS -> this.duration.div(60).div(24)
    }
}

fun Int.convert(unit: DurationUnit): Int {
    return when (unit) {
        DurationUnit.MINUTES -> this
        DurationUnit.HOURS -> this.times(60)
        DurationUnit.DAYS -> this.times(60).times(24)
    }
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