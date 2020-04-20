package org.vmaier.tidfl

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat


/**
 * Created by Vladas Maier
 * on 20/04/2020.
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (key == "calendar_sync") {
            val pref: CheckBoxPreference = findPreference(key) as CheckBoxPreference
            sharedPreferences.edit().putBoolean("calendar_sync", pref.isChecked).apply()
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