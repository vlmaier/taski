package org.vmaier.tidfl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DurationUnit
import org.vmaier.tidfl.data.entity.Task
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Vladas Maier
 * on 07/02/2020
 * at 21:03
 */

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.setIcon(iconId: Int) {
    val drawable = App.iconPack.getIcon(iconId)?.drawable
    if (drawable != null) {
        drawable.colorFilter = null
        this.background = drawable
        this.tag = iconId
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

fun SeekBar.getHumanReadableValue(): String {
    return when (this.progress) {
        1 -> resources.getQuantityString(R.plurals.duration_minute, 5, 5)
        2 -> resources.getQuantityString(R.plurals.duration_minute, 10, 10)
        3 -> resources.getQuantityString(R.plurals.duration_minute, 15, 15)
        4 -> resources.getQuantityString(R.plurals.duration_minute, 30, 30)
        5 -> resources.getQuantityString(R.plurals.duration_minute, 45, 45)
        6 -> resources.getQuantityString(R.plurals.duration_hour, 1, 1)
        7 -> resources.getQuantityString(R.plurals.duration_hour, 2, 2)
        8 -> resources.getQuantityString(R.plurals.duration_hour, 3, 3)
        9 -> resources.getQuantityString(R.plurals.duration_hour, 4, 4)
        10 -> resources.getQuantityString(R.plurals.duration_hour, 6, 6)
        11 -> resources.getQuantityString(R.plurals.duration_hour, 8, 8)
        12 -> resources.getQuantityString(R.plurals.duration_hour, 12, 12)
        13 -> resources.getQuantityString(R.plurals.duration_hour, 16, 16)
        14 -> resources.getQuantityString(R.plurals.duration_day, 1, 1)
        15 -> resources.getQuantityString(R.plurals.duration_day, 2, 2)
        16 -> resources.getQuantityString(R.plurals.duration_day, 3, 3)
        17 -> resources.getQuantityString(R.plurals.duration_day, 4, 4)
        18 -> resources.getQuantityString(R.plurals.duration_day, 5, 5)
        19 -> resources.getQuantityString(R.plurals.duration_day, 6, 6)
        20 -> resources.getQuantityString(R.plurals.duration_week, 1, 1)
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

fun Task.getHumanReadableDurationValue(context: Context): String {
    return when (val unit = this.getDurationUnit()) {
        DurationUnit.MINUTE -> context
            .getString(R.string.unit_minute_short, this.convertDurationToMinutes(unit))
        DurationUnit.HOUR -> context
            .getString(R.string.unit_hour_short, this.convertDurationToMinutes(unit))
        DurationUnit.DAY -> context
            .getString(R.string.unit_day_short, this.convertDurationToMinutes(unit))
        DurationUnit.WEEK -> context
            .getString(R.string.unit_week_short, this.convertDurationToMinutes(unit))
    }
}

fun String.toast(context: Context, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, length).show()
}

fun Bitmap.encodeTobase64(): String? {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun Bitmap.compress(quality: Int): Bitmap {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    val byteArray = stream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun String.decodeBase64(): Bitmap? {
    val decodedByte: ByteArray = Base64.decode(this, 0)
    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
}

fun Date.getDateInAppFormat(): String {
    return SimpleDateFormat(
        App.dateFormat.toPattern().split(" ")[0],
        Locale.getDefault()
    ).format(this.time)
}

fun Date.getTimeInAppFormat(): String {
    return SimpleDateFormat(
        App.dateFormat.toPattern().split(" ")[1],
        Locale.getDefault()
    ).format(this.time)
}
