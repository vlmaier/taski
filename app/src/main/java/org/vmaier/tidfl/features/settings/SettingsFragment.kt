package org.vmaier.tidfl.features.settings

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.PermissionManager
import org.vmaier.tidfl.R
import org.vmaier.tidfl.utils.Const
import org.vmaier.tidfl.views.EditTextDialog


/**
 * Created by Vladas Maier
 * on 20/04/2020
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
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val resetAvatarPref =
            preferenceScreen.findPreference(Const.Prefs.RESET_AVATAR) as Preference?
        resetAvatarPref?.setOnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.alert_reset_avatar))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.action_proceed_with_reset)) { _, _ ->
                    preferenceManager.sharedPreferences.edit()
                        .putString(Const.Prefs.USER_AVATAR, null).apply()
                    MainActivity.avatarView.setImageDrawable(
                        getDrawable(requireContext(), R.mipmap.ic_launcher_round)
                    )
                }
                .setNegativeButton(getString(R.string.action_cancel)) { dialog, _ ->
                    dialog.cancel()
                }
            dialogBuilder.create().show()
            true
        }
        val usernamePref = preferenceScreen.findPreference(Const.Prefs.USER_NAME) as Preference?
        usernamePref?.setOnPreferenceClickListener {
            val username = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(Const.Prefs.USER_NAME, getString(R.string.app_name))
            val dialog = EditTextDialog.newInstance(
                title = getString(R.string.heading_user_name),
                hint = getString(R.string.hint_user_name),
                text = username,
                positiveButton = R.string.action_set
            )
            dialog.onPositiveButtonClicked = {
                val textValue = dialog.editText.text.toString()
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit().putString(Const.Prefs.USER_NAME, textValue).apply()
                MainActivity.userNameView.text = textValue
            }
            dialog.onNegativeButtonClicked = {
                dialog.dismiss()
            }
            dialog.show(requireFragmentManager(), EditTextDialog::class.simpleName)
            true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Const.Prefs.CALENDAR_SYNC -> {
                val pref: CheckBoxPreference? = findPreference(key)
                val checkBoxValue = pref?.isChecked ?: false
                if (checkBoxValue) {
                    // initialize a list of required permissions to request runtime
                    val permissions = listOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    )
                    PermissionManager(
                        requireActivity(),
                        permissions,
                        1
                    )
                        .checkPermissions()
                }
                sharedPreferences.edit().putBoolean(Const.Prefs.CALENDAR_SYNC, checkBoxValue)
                    .apply()
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