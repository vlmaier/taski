package org.vmaier.tidfl.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * Created by Vladas Maier
 * on 19/04/2020
 * at 12:09
 */
class PermissionManager(
    private val activity: Activity,
    private val permissions: List<String>,
    private val code: Int
) {

    fun checkPermissions() {
        if (!arePermissionsGranted(permissions)) {
            requestPermissions()
        }
        // already granted
    }

    private fun arePermissionsGranted(permissions: List<String>): Boolean {
        var counter = 0
        for (permission in permissions) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), code)
    }
}