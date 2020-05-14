package org.vmaier.tidfl.features.settings

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import org.vmaier.tidfl.R
import org.vmaier.tidfl.util.Const
import org.vmaier.tidfl.util.PermissionManager


/**
 * Created by Vladas Maier
 * on 20/04/2020.
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var permManager: PermissionManager
    private val permRequestCode = 1337

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Const.Prefs.CALENDAR_SYNC) {
            val pref: CheckBoxPreference? = findPreference(key)
            val checkBoxValue = pref?.isChecked ?: false
            if (checkBoxValue) {
                // initialize a list of required permissions to request runtime
                val permissions = listOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
                permManager = PermissionManager(requireActivity(), permissions, permRequestCode)
                permManager.checkPermissions()
            }
            sharedPreferences.edit().putBoolean(Const.Prefs.CALENDAR_SYNC, checkBoxValue).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}