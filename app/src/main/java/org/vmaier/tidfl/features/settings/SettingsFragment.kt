package org.vmaier.tidfl.features.settings

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.utils.Const
import org.vmaier.tidfl.utils.PermissionManager


/**
 * Created by Vladas Maier
 * on 20/04/2020.
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.toolbar.title = getString(R.string.heading_settings)
        MainActivity.bottomNav.visibility = View.GONE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Const.Prefs.USER_NAME -> {
                val pref: EditTextPreference? = findPreference(key)
                val textValue = pref?.text.toString()
                if (textValue.isNotBlank()) {
                    sharedPreferences.edit().putString(Const.Prefs.USER_NAME, textValue).apply()
                    MainActivity.userNameView.text = textValue
                }
            }
            Const.Prefs.CALENDAR_SYNC -> {
                val pref: CheckBoxPreference? = findPreference(key)
                val checkBoxValue = pref?.isChecked ?: false
                if (checkBoxValue) {
                    // initialize a list of required permissions to request runtime
                    val permissions = listOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    )
                    PermissionManager(requireActivity(), permissions, 1)
                        .checkPermissions()
                }
                sharedPreferences.edit().putBoolean(Const.Prefs.CALENDAR_SYNC, checkBoxValue).apply()
            }
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