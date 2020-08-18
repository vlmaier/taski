package com.vmaier.taski.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vmaier.taski.R
import timber.log.Timber


class PermissionUtils {

    companion object {
        var PICK_IMAGE_REQUEST_CODE = 1000
        var ACCESS_CALENDAR_REQUEST_CODE = 1001

        fun setupCalendarPermissions(context: Context) {
            val activity = context as Activity
            val res = context.resources
            val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
            val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
            if (read == PackageManager.PERMISSION_DENIED || write == PackageManager.PERMISSION_DENIED) {
                Timber.d("Permission to access calendar is denied")
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.WRITE_CALENDAR
                    )
                ) {
                    val builder = AlertDialog.Builder(context)
                    builder
                        .setMessage(res.getString(R.string.alert_calendar_access_required))
                        .setTitle(res.getString(R.string.alert_permission_required))
                        .setPositiveButton(res.getString(R.string.action_ok)) { _, _ ->
                            requestCalendarPermissions(context)
                        }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    requestCalendarPermissions(context)
                }
            }
            if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED) {
                Timber.d("Permissions to access calendar are granted")
            }
        }

        private fun requestCalendarPermissions(context: Context) {
            val activity = context as Activity
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                ),
                ACCESS_CALENDAR_REQUEST_CODE
            )
        }
    }
}