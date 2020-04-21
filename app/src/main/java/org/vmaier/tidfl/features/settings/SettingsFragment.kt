package org.vmaier.tidfl.features.settings

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import org.vmaier.tidfl.R
import org.vmaier.tidfl.util.PermissionManager


/**
 * Created by Vladas Maier
 * on 20/04/2020.
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var permissionManager: PermissionManager
    private val permRequestCode = 1337

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (key == "calendar_sync") {
            val pref: CheckBoxPreference? = findPreference(key)
            val value = pref?.isChecked ?: false
            if (value) {
                // initialize a list of required permissions to request runtime
                val permissions = listOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
                permissionManager = PermissionManager(this.requireActivity(), permissions, permRequestCode)
                permissionManager.checkPermissions()
            }
            sharedPreferences.edit().putBoolean("calendar_sync", value).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}