package org.vmaier.tidfl.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * Created by Vladas Maier
 * on 19/04/2020.
 * at 12:09
 */
class PermissionManager(private val activity: Activity,
                        private val permissions: List<String>,
                        private val code: Int) {

    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        }
        // already granted
    }

    private fun isPermissionsGranted(): Int {

        var counter = 0
        for (permission in permissions) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }

    private fun deniedPermission(): String {

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_DENIED)
                return permission
        }
        return ""
    }

    private fun requestPermissions() {

        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // TODO: explanation dialog
        } else {
            ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), code)
        }
    }
}