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
import com.vmaier.taski.MainActivity
import com.vmaier.taski.MainActivity.Companion.avatarView
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.compress
import com.vmaier.taski.encodeToBase64
import com.vmaier.taski.services.PreferenceService
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.views.EditTextDialog
import timber.log.Timber
import worker8.com.github.radiogroupplus.RadioGroupPlus
import java.util.*


/**
 * Created by Vladas Maier
 * on 20.04.2020
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefService: PreferenceService

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
        prefService = PreferenceService(requireContext())

        setChangeAvatarClickListener()
        setResetAvatarClickListener()
        setUsernameClickListener()
        setThemeClickListener()

        // preselect dark mode icon
        val isDarkModeOn = prefService.isDarkModeEnabled()
        val darkMode =
            preferenceScreen.findPreference(PreferenceService.Keys.DARK_MODE) as SwitchPreference?
        darkMode?.setIcon(if (isDarkModeOn) R.drawable.ic_light_mode_24 else R.drawable.ic_dark_mode_24)
        darkMode?.title =
            getString(if (isDarkModeOn) R.string.heading_light_mode else R.string.heading_dark_mode)

        // preselect language value
        val appLanguage =
            preferenceScreen.findPreference(PreferenceService.Keys.LANGUAGE) as ListPreference?
        val prefLanguage = prefService.getLanguage()
        val languageValues = resources.getStringArray(R.array.language_values_array)
        appLanguage?.setValueIndex(languageValues.indexOf(prefLanguage))

        // preselect start of the week value
        val startOfTheWeek =
            preferenceScreen.findPreference(PreferenceService.Keys.START_OF_THE_WEEK) as ListPreference?
        val prefStartOfTheWeek = prefService.getStartOfTheWeek()
        val startOfTheWeekValues = resources.getStringArray(R.array.start_of_the_week_values_array)
        startOfTheWeek?.setValueIndex(startOfTheWeekValues.indexOf(prefStartOfTheWeek))

        val calendarTasksPref: CheckBoxPreference? =
            findPreference(PreferenceService.Keys.DELETE_COMPLETED_TASKS)
        calendarTasksPref?.isEnabled = prefService.isCalendarSyncEnabled()
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            PreferenceService.Keys.CALENDAR_SYNC -> {
                calendarSyncPref = findPreference(key)!!
                val isCalendarSyncOn = calendarSyncPref.isChecked
                if (isCalendarSyncOn) PermissionUtils.setupCalendarPermissions(requireContext())
                prefService.setCalendarSyncEnabled(isCalendarSyncOn)
                Timber.d("Calendar synchronization is ${if (isCalendarSyncOn) "enabled" else "disabled"}.")
                val calendarTasksPref: CheckBoxPreference? =
                    findPreference(PreferenceService.Keys.DELETE_COMPLETED_TASKS)
                calendarTasksPref?.isEnabled = isCalendarSyncOn
                // deselect "Delete completed tasks" if "Calendar sync" gets disabled
                if (!isCalendarSyncOn) {
                    calendarTasksPref?.isChecked = isCalendarSyncOn
                    prefService.setDeleteCompletedTasksEnabled(false)
                }
            }
            PreferenceService.Keys.DELETE_COMPLETED_TASKS -> {
                val pref: CheckBoxPreference? = findPreference(key)
                val calendarSyncPref: CheckBoxPreference? =
                    findPreference(PreferenceService.Keys.CALENDAR_SYNC)
                val isCalendarSyncOn = calendarSyncPref?.isChecked ?: false
                if (isCalendarSyncOn) {
                    val value = pref?.isChecked ?: PreferenceService.Defaults.DELETE_COMPLETED_TASKS
                    prefService.setDeleteCompletedTasksEnabled(value)
                }
            }
            PreferenceService.Keys.DARK_MODE -> {
                val pref: SwitchPreference? = findPreference(key)
                val isDarkModeOn = pref?.isChecked ?: false
                setDefaultNightMode(if (isDarkModeOn) MODE_NIGHT_YES else MODE_NIGHT_NO)
                prefService.setDarkModeEnabled(isDarkModeOn)
                Timber.d("Dark mode is ${if (isDarkModeOn) "enabled" else "disabled"}.")
            }
            PreferenceService.Keys.LANGUAGE -> {
                val pref: ListPreference? = findPreference(key)
                val prefLanguage = pref?.value ?: PreferenceService.Keys.LANGUAGE
                prefService.setLanguage(prefLanguage)
                setLocale(Locale(prefLanguage))
                Timber.d("Language changed to '${prefLanguage.toUpperCase(Locale.getDefault())}'")
            }
            PreferenceService.Keys.START_OF_THE_WEEK -> {
                val pref: ListPreference? = findPreference(key)
                val prefStartOfTheWeek = pref?.value ?: PreferenceService.Keys.START_OF_THE_WEEK
                prefService.setStartOfTheWeek(prefStartOfTheWeek)
                Timber.d("Start of the week changed to '$prefStartOfTheWeek'")
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
            intent != null && intent.data != null
        ) {
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, intent.data)
            val compressedBitmap = bitmap.compress(10)
            avatarView.setImageBitmap(compressedBitmap)
            prefService.setUserAvatar(compressedBitmap.encodeToBase64())
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
        val preference =
            preferenceScreen.findPreference(PreferenceService.Keys.CHANGE_AVATAR) as Preference?
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
        val preference =
            preferenceScreen.findPreference(PreferenceService.Keys.RESET_AVATAR) as Preference?
        preference?.setOnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.alert_reset_avatar))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.action_proceed_with_reset)) { _, _ ->
                    prefService.resetUserAvatar()
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
        val preference =
            preferenceScreen.findPreference(PreferenceService.Keys.USER_NAME) as Preference?
        preference?.setOnPreferenceClickListener {
            val usernameValue = prefService.getUserName()
            val dialog = EditTextDialog.newInstance(
                title = getString(R.string.heading_user_name),
                hint = getString(R.string.hint_user_name),
                text = usernameValue,
                positiveButton = R.string.action_set
            )
            dialog.onPositiveButtonClicked = {
                val textValue = dialog.editText.text.toString().trim()
                prefService.setUserName(textValue)
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
        val preference =
            preferenceScreen.findPreference(PreferenceService.Keys.THEME) as Preference?
        preference?.setOnPreferenceClickListener {
            prefTheme = prefService.getTheme()
            val dialogView =
                (context as Activity).layoutInflater.inflate(R.layout.select_theme_dialog, null)
            val radioGroup = dialogView.findViewById(R.id.radio_group_themes) as RadioGroupPlus
            preselectTheme(dialogView)
            val builder = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.heading_select_theme))
                .setView(dialogView)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.action_set)) { _, _ ->
                    prefService.setTheme(prefTheme)
                    activity?.recreate()
                    Timber.d("Theme changed to '${prefTheme}'")
                }
                .setNegativeButton(getString(R.string.action_cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            val alertDialog = builder.create()
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                prefTheme = when (checkedId) {
                    R.id.button_default -> requireActivity().getString(R.string.theme_default)
                    R.id.button_sailor -> requireActivity().getString(R.string.theme_sailor)
                    R.id.button_royal -> requireActivity().getString(R.string.theme_royal)
                    R.id.button_mercury -> requireActivity().getString(R.string.theme_mercury)
                    R.id.button_mocca -> requireActivity().getString(R.string.theme_mocca)
                    R.id.button_creeper -> requireActivity().getString(R.string.theme_creeper)
                    R.id.button_flamingo -> requireActivity().getString(R.string.theme_flamingo)
                    R.id.button_pilot -> requireActivity().getString(R.string.theme_pilot)
                    R.id.button_coral -> requireActivity().getString(R.string.theme_coral)
                    R.id.button_blossom -> requireActivity().getString(R.string.theme_blossom)
                    R.id.button_mint -> requireActivity().getString(R.string.theme_mint)
                    else -> requireActivity().getString(R.string.theme_default)
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
            getString(R.string.theme_mint) -> {
                dialogView.findViewById(R.id.button_mint) as RadioButton
            }
            else -> dialogView.findViewById(R.id.button_default) as RadioButton
        }
        radioButton.isChecked = true
    }
}