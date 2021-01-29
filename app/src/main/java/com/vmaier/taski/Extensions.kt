package com.vmaier.taski

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import com.vmaier.taski.data.DurationUnit
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.utils.Utils
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Vladas Maier
 * on 07.02.2020
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

fun Task.getDurationUnit(): DurationUnit {
    return when (this.duration) {
        in 1..59 -> DurationUnit.MINUTE
        in 60..1439 -> DurationUnit.HOUR
        in 1440..10079 -> DurationUnit.DAY
        in 10080..524159 -> DurationUnit.WEEK
        else -> DurationUnit.YEAR
    }
}

fun Task.convertDurationToMinutes(unit: DurationUnit): Int {
    return when (unit) {
        DurationUnit.MINUTE -> this.duration
        DurationUnit.HOUR -> this.duration.div(60)
        DurationUnit.DAY -> this.duration.div(60).div(24)
        DurationUnit.WEEK -> this.duration.div(60).div(24).div(7)
        DurationUnit.YEAR -> this.duration.div(60).div(24).div(7).div(52)
    }
}

fun Task.getHumanReadableDurationValue(context: Context): String {
    return when (val unit = this.getDurationUnit()) {
        DurationUnit.MINUTE -> context.getString(
            R.string.unit_minute_short,
            this.convertDurationToMinutes(unit)
        )
        DurationUnit.HOUR -> context.getString(
            R.string.unit_hour_short,
            this.convertDurationToMinutes(unit)
        )
        DurationUnit.DAY -> context.getString(
            R.string.unit_day_short,
            this.convertDurationToMinutes(unit)
        )
        DurationUnit.WEEK -> context.getString(
            R.string.unit_week_short,
            this.convertDurationToMinutes(unit)
        )
        DurationUnit.YEAR -> context.getString(
            R.string.unit_year_short,
            this.convertDurationToMinutes(unit)
        )
    }
}

fun Task.getHumanReadableCreationDate(): String {
    val now = System.currentTimeMillis()
    val createdAt = this.createdAt
    val format = Utils.getDateSpanFormat(now, createdAt)
    return DateUtils.getRelativeTimeSpanString(createdAt, now, format).toString()
}

fun Task.getHumanReadableDueDate(): String {
    val now = System.currentTimeMillis()
    if (dueAt != null) {
        val dueAt = this.dueAt
        val format = Utils.getDateSpanFormat(now, dueAt)
        return DateUtils.getRelativeTimeSpanString(dueAt, now, format).toString()
    }
    return ""
}

fun String.toast(context: Context, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, length).show()
}

fun Bitmap.encodeToBase64(): String? {
    val os = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, os)
    val b: ByteArray = os.toByteArray()
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
        App.dateTimeFormat.toPattern().split(" ")[0],
        Locale.getDefault()
    ).format(time)
}

fun Date.getTimeInAppFormat(): String {
    return SimpleDateFormat(
        App.dateTimeFormat.toPattern().split(" ")[1],
        Locale.getDefault()
    ).format(this.time)
}

fun Date.getDateTimeInAppFormat(): String {
    return SimpleDateFormat(App.dateTimeFormat.toPattern(), Locale.getDefault()).format(this.time)
}

fun String.parseToDate(): Date? {
    return try {
        App.dateTimeFormat.parse(this)
    } catch (e: ParseException) {
        try {
            App.dateFormat.parse(this)
        } catch (e: ParseException) {
            null
        }
    }
}

fun NumberPicker.getDurationUnit(): DurationUnit {
    return when (this.value) {
        1 -> DurationUnit.MINUTE
        2 -> DurationUnit.HOUR
        3 -> DurationUnit.DAY
        4 -> DurationUnit.WEEK
        5 -> DurationUnit.YEAR
        else -> DurationUnit.MINUTE
    }
}
