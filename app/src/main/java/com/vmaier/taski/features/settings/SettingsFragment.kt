package com.vmaier.taski.features.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.vmaier.taski.*
import com.vmaier.taski.MainActivity.Companion.avatarView
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.views.EditTextDialog
import timber.log.Timber
import worker8.com.github.radiogroupplus.RadioGroupPlus
import java.util.*


/**
 * Created by Vladas Maier
 * on 20/04/2020
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs: SharedPreferences

    companion object {
        lateinit var calendarSyncPref: CheckBoxPreference
        lateinit var prefTheme: String

        fun isCalendarSyncPrefInitialized() = ::calendarSyncPref.isInitialized
    }

    override fun onStart() {
        super.onStart()
        toolbar.title = getString(R.string.heading_settings)
        toggleBottomMenu(false, View.GONE)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = getDefaultSharedPreferences(context)

        setChangeAvatarClickListener()
        setResetAvatarClickListener()
        setUsernameClickListener()
        setThemeClickListener()

        // preselect dark mode icon
        val isDarkModeOn = prefs.getBoolean(Const.Prefs.DARK_MODE, Const.Defaults.DARK_MODE)
        val darkMode = preferenceScreen.findPreference(Const.Prefs.DARK_MODE) as SwitchPreference?
        darkMode?.setIcon(if (isDarkModeOn) R.drawable.ic_light_mode_24 else R.drawable.ic_dark_mode_24)
        darkMode?.title = getString(if (isDarkModeOn) R.string.heading_light_mode else R.string.heading_dark_mode)

        // preselect language value
        val appLanguage = preferenceScreen.findPreference(Const.Prefs.LANGUAGE) as ListPreference?
        val prefLanguage = prefs.getString(Const.Prefs.LANGUAGE, Const.Defaults.LANGUAGE)
        val languageValues = resources.getStringArray(R.array.language_values_array)
        appLanguage?.setValueIndex(languageValues.indexOf(prefLanguage))

        val calendarTasksPref: CheckBoxPreference? = findPreference(Const.Prefs.DELETE_COMPLETED_TASKS)
        calendarTasksPref?.isEnabled = prefs.getBoolean(Const.Prefs.CALENDAR_SYNC, Const.Defaults.CALENDAR_SYNC)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            Const.Prefs.CALENDAR_SYNC -> {
                calendarSyncPref = findPreference(key)!!
                val isCalendarSyncOn = calendarSyncPref.isChecked
                if (isCalendarSyncOn) PermissionUtils.setupCalendarPermissions(requireContext())
                prefs.edit()
                    .putBoolean(Const.Prefs.CALENDAR_SYNC, isCalendarSyncOn)
                    .apply()
                Timber.d("Calendar synchronization is ${if (isCalendarSyncOn) "enabled" else "disabled"}.")
                val calendarTasksPref: CheckBoxPreference? = findPreference(Const.Prefs.DELETE_COMPLETED_TASKS)
                calendarTasksPref?.isEnabled = isCalendarSyncOn
                // unselect "Delete completed tasks" if "Calendar sync" gets disabled
                if (!isCalendarSyncOn) {
                    calendarTasksPref?.isChecked = isCalendarSyncOn
                    prefs.edit()
                        .putBoolean(Const.Prefs.DELETE_COMPLETED_TASKS, false)
                        .apply()
                }
            }
            Const.Prefs.DELETE_COMPLETED_TASKS -> {
                val pref: CheckBoxPreference? = findPreference(key)
                val calendarSyncPref: CheckBoxPreference? = findPreference(Const.Prefs.CALENDAR_SYNC)
                val isCalendarSyncOn = calendarSyncPref?.isChecked ?: false
                if (isCalendarSyncOn) {
                    val value = pref?.isChecked ?: Const.Defaults.DELETE_COMPLETED_TASKS
                    prefs.edit()
                        .putBoolean(Const.Prefs.DELETE_COMPLETED_TASKS, value)
                        .apply()
                }
            }
            Const.Prefs.DARK_MODE -> {
                val pref: SwitchPreference? = findPreference(key)
                val isDarkModeOn = pref?.isChecked ?: false
                setDefaultNightMode(if (isDarkModeOn) MODE_NIGHT_YES else MODE_NIGHT_NO)
                prefs.edit()
                    .putBoolean(Const.Prefs.DARK_MODE, isDarkModeOn)
                    .apply()
                Timber.d("Dark mode is ${if (isDarkModeOn) "enabled" else "disabled"}.")
            }
            Const.Prefs.LANGUAGE -> {
                val pref: ListPreference? = findPreference(key)
                val prefLanguage = pref?.value ?: Const.Defaults.LANGUAGE
                prefs.edit()
                    .putString(Const.Prefs.LANGUAGE, prefLanguage)
                    .apply()
                setLocale(Locale(prefLanguage))
                Timber.d("Language changed to '${prefLanguage.toUpperCase(Locale.getDefault())}'")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == PermissionUtils.PICK_IMAGE_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK &&
            intent != null && intent.data != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, intent.data)
            val compressedBitmap = bitmap.compress(10)
            avatarView.setImageBitmap(compressedBitmap)
            prefs.edit()
                .putString(Const.Prefs.USER_AVATAR, compressedBitmap.encodeTobase64())
                .apply()
            Timber.d("Avatar changed.")
        }
    }

    private fun setLocale(locale: Locale) {
        val config: Configuration = resources.configuration
        config.locale = locale
        resources.updateConfiguration(config, null)
        Locale.setDefault(locale)
        activity?.recreate()
    }

    private fun setChangeAvatarClickListener() {
        val preference = preferenceScreen.findPreference(Const.Prefs.CHANGE_AVATAR) as Preference?
        preference?.setOnPreferenceClickListener {
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
    }

    private fun setResetAvatarClickListener() {
        val preference = preferenceScreen.findPreference(Const.Prefs.RESET_AVATAR) as Preference?
        preference?.setOnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.alert_reset_avatar))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.action_proceed_with_reset)) { _, _ ->
                    prefs.edit()
                        .putString(Const.Prefs.USER_AVATAR, null)
                        .apply()
                    avatarView.setImageDrawable(getDrawable(requireContext(), R.mipmap.ic_launcher))
                    Timber.d("Avatar reset")
                }
                .setNegativeButton(getString(R.string.action_cancel)) { dialog, _ ->
                    dialog.cancel()
                }
            dialogBuilder.create().show()
            true
        }
    }

    private fun setUsernameClickListener() {
        val preference = preferenceScreen.findPreference(Const.Prefs.USER_NAME) as Preference?
        preference?.setOnPreferenceClickListener {
            val usernameValue = prefs.getString(Const.Prefs.USER_NAME, getString(R.string.app_name))
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
    }

    private fun setThemeClickListener() {
        val preference = preferenceScreen.findPreference(Const.Prefs.THEME) as Preference?
        preference?.setOnPreferenceClickListener {
            prefTheme = prefs.getString(Const.Prefs.THEME, Const.Defaults.THEME) ?: Const.Defaults.THEME
            val dialogView = (context as Activity).layoutInflater.inflate(R.layout.select_theme_dialog, null)
            val radioGroup = dialogView.findViewById(R.id.radio_group) as RadioGroupPlus
            preselectTheme(dialogView)
            val builder = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.heading_select_theme))
                .setView(dialogView)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.action_set)) { _, _ ->
                    prefs.edit()
                        .putString(Const.Prefs.THEME, prefTheme)
                        .apply()
                    activity?.recreate()
                    Timber.d("Theme changed to '${prefTheme}'")
                }
                .setNegativeButton(getString(R.string.action_cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            val alertDialog = builder.create()
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                prefTheme = when (checkedId) {
                    R.id.button_default -> getString(R.string.theme_default)
                    R.id.button_sailor -> getString(R.string.theme_sailor)
                    R.id.button_royal -> getString(R.string.theme_royal)
                    R.id.button_mercury -> getString(R.string.theme_mercury)
                    R.id.button_mocca -> getString(R.string.theme_mocca)
                    R.id.button_creeper -> getString(R.string.theme_creeper)
                    R.id.button_flamingo -> getString(R.string.theme_flamingo)
                    R.id.button_pilot -> getString(R.string.theme_pilot)
                    R.id.button_coral -> getString(R.string.theme_coral)
                    R.id.button_blossom -> getString(R.string.theme_blossom)
                    else -> getString(R.string.theme_default)
                }
            }
            alertDialog.show()
            true
        }
    }

    private fun preselectTheme(dialogView: View) {
        val radioButton: RadioButton = when (prefTheme) {
            getString(R.string.theme_default) -> {
                dialogView.findViewById(R.id.button_default) as RadioButton
            }
            getString(R.string.theme_sailor) -> {
                dialogView.findViewById(R.id.button_sailor) as RadioButton
            }
            getString(R.string.theme_royal) -> {
                dialogView.findViewById(R.id.button_royal) as RadioButton
            }
            getString(R.string.theme_mercury) -> {
                dialogView.findViewById(R.id.button_mercury) as RadioButton
            }
            getString(R.string.theme_mocca) -> {
                dialogView.findViewById(R.id.button_mocca) as RadioButton
            }
            getString(R.string.theme_creeper) -> {
                dialogView.findViewById(R.id.button_creeper) as RadioButton
            }
            getString(R.string.theme_flamingo) -> {
                dialogView.findViewById(R.id.button_flamingo) as RadioButton
            }
            getString(R.string.theme_pilot) -> {
                dialogView.findViewById(R.id.button_pilot) as RadioButton
            }
            getString(R.string.theme_coral) -> {
                dialogView.findViewById(R.id.button_coral) as RadioButton
            }
            getString(R.string.theme_blossom) -> {
                dialogView.findViewById(R.id.button_blossom) as RadioButton
            }
            else -> dialogView.findViewById(R.id.button_default) as RadioButton
        }
        radioButton.isChecked = true
    }
}