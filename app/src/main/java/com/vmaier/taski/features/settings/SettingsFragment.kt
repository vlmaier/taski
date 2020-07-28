package com.vmaier.taski.features.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.vmaier.taski.*
import com.vmaier.taski.R
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.views.EditTextDialog
import timber.log.Timber
import java.util.*


/**
 * Created by Vladas Maier
 * on 20/04/2020
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs: SharedPreferences

    companion object {
        lateinit var calendarSyncPref: CheckBoxPreference
    }

    override fun onStart() {
        super.onStart()
        MainActivity.toolbar.title = getString(R.string.heading_settings)
        MainActivity.fab.hide()
        MainActivity.bottomNav.visibility = View.GONE
        MainActivity.bottomBar.visibility = View.GONE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = getDefaultSharedPreferences(context)
        val changeAvatar = preferenceScreen.findPreference(Const.Prefs.CHANGE_AVATAR) as Preference?
        changeAvatar?.setOnPreferenceClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(
                    Intent.createChooser(
                        galleryIntent,
                        getString(R.string.heading_select_image)
                    ), PermissionUtils.PICK_IMAGE_REQUEST_CODE
                )
            }
            true
        }
        val resetAvatar = preferenceScreen.findPreference(Const.Prefs.RESET_AVATAR) as Preference?
        resetAvatar?.setOnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.alert_reset_avatar))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.action_proceed_with_reset)) { _, _ ->
                    prefs.edit()
                        .putString(Const.Prefs.USER_AVATAR, null)
                        .apply()
                    MainActivity.avatarView.setImageDrawable(
                        getDrawable(requireContext(), R.mipmap.ic_launcher)
                    )
                    Timber.d("Avatar resetted")
                }
                .setNegativeButton(getString(R.string.action_cancel)) { dialog, _ ->
                    dialog.cancel()
                }
            dialogBuilder.create().show()
            true
        }
        val username = preferenceScreen.findPreference(Const.Prefs.USER_NAME) as Preference?
        username?.setOnPreferenceClickListener {
            val usernameValue = prefs
                .getString(Const.Prefs.USER_NAME, getString(R.string.app_name))
            val dialog = EditTextDialog.newInstance(
                title = getString(R.string.heading_user_name),
                hint = getString(R.string.hint_user_name),
                text = usernameValue,
                positiveButton = R.string.action_set
            )
            dialog.onPositiveButtonClicked = {
                val textValue = dialog.editText.text.toString().trim()
                prefs.edit()
                    .putString(Const.Prefs.USER_NAME, textValue)
                    .apply()
                MainActivity.userNameView.text = textValue
                Timber.d("Username changed.")
            }
            dialog.onNegativeButtonClicked = {
                dialog.dismiss()
            }
            dialog.show(requireFragmentManager(), EditTextDialog::class.simpleName)
            true
        }
        val darkMode = preferenceScreen.findPreference(Const.Prefs.DARK_MODE) as SwitchPreference?

        // preselect dark mode icon
        val isDarkModeOn = prefs.getBoolean(Const.Prefs.DARK_MODE, Const.Defaults.DARK_MODE)
        darkMode?.setIcon(
            if (isDarkModeOn) R.drawable.ic_light_mode_24 else R.drawable.ic_dark_mode_24)
        darkMode?.title = getString(
            if (isDarkModeOn) R.string.heading_light_mode else R.string.heading_dark_mode)

        // preselect theme value
        val appTheme = preferenceScreen.findPreference(Const.Prefs.THEME) as ListPreference?
        val prefTheme = prefs.getString(Const.Prefs.THEME, Const.Defaults.THEME)
        val themeValues = resources.getStringArray(R.array.theme_values_array)
        appTheme?.setValueIndex(themeValues.indexOf(prefTheme))
        
        // preselect language value
        val appLanguage = preferenceScreen.findPreference(Const.Prefs.LANGUAGE) as ListPreference?
        val prefLanguage = prefs.getString(Const.Prefs.LANGUAGE, Const.Defaults.LANGUAGE)
        val languageValues = resources.getStringArray(R.array.language_values_array)
        appLanguage?.setValueIndex(languageValues.indexOf(prefLanguage))
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            Const.Prefs.CALENDAR_SYNC -> {
                calendarSyncPref = findPreference(key)!!
                val isCalendarSyncOn = calendarSyncPref.isChecked
                if (isCalendarSyncOn) {
                    PermissionUtils.setupCalendarPermissions(requireContext())
                }
                prefs.edit()
                    .putBoolean(Const.Prefs.CALENDAR_SYNC, isCalendarSyncOn)
                    .apply()
                Timber.d(
                    "Calendar synchronization is %s.",
                    if (isCalendarSyncOn) "enabled" else "disabled"
                )
            }
            Const.Prefs.DARK_MODE -> {
                val pref: SwitchPreference? = findPreference(key)
                val isDarkModeOn = pref?.isChecked ?: false
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                prefs.edit()
                    .putBoolean(Const.Prefs.DARK_MODE, isDarkModeOn)
                    .apply()
                Timber.d("Dark mode is %s.", if (isDarkModeOn) "enabled" else "disabled")
            }
            Const.Prefs.THEME -> {
                val pref: ListPreference? = findPreference(key)
                val prefTheme = pref?.value
                prefs.edit()
                    .putString(Const.Prefs.THEME, prefTheme)
                    .apply()
                activity?.recreate()
                Timber.d("Theme changed to '%s'", prefTheme)
            }
            Const.Prefs.LANGUAGE -> {
                val pref: ListPreference? = findPreference(key)
                val prefLanguage = pref?.value
                prefs.edit()
                    .putString(Const.Prefs.LANGUAGE, prefLanguage)
                    .apply()
                setLocale(Locale(prefLanguage))
                Timber.d("Language changed to '%s'", prefLanguage?.toUpperCase(Locale.getDefault()))
            }
        }
    }

    private fun setLocale(locale: Locale) {
        val metrics: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.locale = locale
        resources.updateConfiguration(config, metrics)
        activity?.recreate()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == PermissionUtils.PICK_IMAGE_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK) {
            if (intent != null && intent.data != null) {
                val filePath = intent.data
                val contentResolver = requireActivity().contentResolver
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val compressedBitmap = bitmap.compress(10)
                MainActivity.avatarView.setImageBitmap(compressedBitmap)
                prefs.edit()
                    .putString(Const.Prefs.USER_AVATAR, compressedBitmap.encodeTobase64())
                    .apply()
                Timber.d("Avatar changed.")
            }
        }
    }
}