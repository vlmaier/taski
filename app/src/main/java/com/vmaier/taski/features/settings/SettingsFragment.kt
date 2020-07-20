package com.vmaier.taski.features.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.vmaier.taski.*
import com.vmaier.taski.R
import com.vmaier.taski.utils.RequestCode
import com.vmaier.taski.views.EditTextDialog
import timber.log.Timber
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by Vladas Maier
 * on 20/04/2020
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs: SharedPreferences

    companion object {
        var PICK_IMAGE_REQUEST_CODE by Delegates.notNull<Int>()
        var ACCESS_CALENDAR_REQUEST_CODE by Delegates.notNull<Int>()

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
        PICK_IMAGE_REQUEST_CODE = RequestCode.get(requireContext())
        ACCESS_CALENDAR_REQUEST_CODE = RequestCode.get(requireContext())
        val changeAvatar = preferenceScreen.findPreference(Constants.Prefs.CHANGE_AVATAR) as Preference?
        changeAvatar?.setOnPreferenceClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(
                    Intent.createChooser(
                        galleryIntent,
                        getString(R.string.heading_select_image)
                    ), PICK_IMAGE_REQUEST_CODE
                )
            }
            true
        }
        val resetAvatar = preferenceScreen.findPreference(Constants.Prefs.RESET_AVATAR) as Preference?
        resetAvatar?.setOnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.alert_reset_avatar))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.action_proceed_with_reset)) { _, _ ->
                    prefs.edit()
                        .putString(Constants.Prefs.USER_AVATAR, null)
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
        val username = preferenceScreen.findPreference(Constants.Prefs.USER_NAME) as Preference?
        username?.setOnPreferenceClickListener {
            val usernameValue = prefs
                .getString(Constants.Prefs.USER_NAME, getString(R.string.app_name))
            val dialog = EditTextDialog.newInstance(
                title = getString(R.string.heading_user_name),
                hint = getString(R.string.hint_user_name),
                text = usernameValue,
                positiveButton = R.string.action_set
            )
            dialog.onPositiveButtonClicked = {
                val textValue = dialog.editText.text.toString().trim()
                prefs.edit()
                    .putString(Constants.Prefs.USER_NAME, textValue)
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
        val darkMode = preferenceScreen.findPreference(Constants.Prefs.DARK_MODE) as SwitchPreference?

        // preselect dark mode icon
        val isDarkModeOn = prefs.getBoolean(Constants.Prefs.DARK_MODE, Constants.Defaults.DARK_MODE)
        darkMode?.setIcon(
            if (isDarkModeOn) R.drawable.ic_light_mode_24 else R.drawable.ic_dark_mode_24)
        darkMode?.title = getString(
            if (isDarkModeOn) R.string.heading_light_mode else R.string.heading_dark_mode)

        // preselect theme value
        val appTheme = preferenceScreen.findPreference(Constants.Prefs.THEME) as ListPreference?
        val prefTheme = prefs.getString(Constants.Prefs.THEME, Constants.Defaults.THEME)
        val themeValues = resources.getStringArray(R.array.theme_values_array)
        appTheme?.setValueIndex(themeValues.indexOf(prefTheme))
        
        // preselect language value
        val appLanguage = preferenceScreen.findPreference(Constants.Prefs.LANGUAGE) as ListPreference?
        val prefLanguage = prefs.getString(Constants.Prefs.LANGUAGE, Constants.Defaults.LANGUAGE)
        val languageValues = resources.getStringArray(R.array.language_values_array)
        appLanguage?.setValueIndex(languageValues.indexOf(prefLanguage))
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            Constants.Prefs.CALENDAR_SYNC -> {
                calendarSyncPref = findPreference(key)!!
                val isCalendarSyncOn = calendarSyncPref.isChecked
                if (isCalendarSyncOn) {
                    setupCalendarPermissions()
                }
                prefs.edit()
                    .putBoolean(Constants.Prefs.CALENDAR_SYNC, isCalendarSyncOn)
                    .apply()
                Timber.d(
                    "Calendar synchronization is %s.",
                    if (isCalendarSyncOn) "enabled" else "disabled"
                )
            }
            Constants.Prefs.DARK_MODE -> {
                val pref: SwitchPreference? = findPreference(key)
                val isDarkModeOn = pref?.isChecked ?: false
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                prefs.edit()
                    .putBoolean(Constants.Prefs.DARK_MODE, isDarkModeOn)
                    .apply()
                Timber.d("Dark mode is %s.", if (isDarkModeOn) "enabled" else "disabled")
            }
            Constants.Prefs.THEME -> {
                val pref: ListPreference? = findPreference(key)
                val prefTheme = pref?.value
                prefs.edit()
                    .putString(Constants.Prefs.THEME, prefTheme)
                    .apply()
                activity?.recreate()
                Timber.d("Theme changed to '%s'", prefTheme)
            }
            Constants.Prefs.LANGUAGE -> {
                val pref: ListPreference? = findPreference(key)
                val prefLanguage = pref?.value
                prefs.edit()
                    .putString(Constants.Prefs.LANGUAGE, prefLanguage)
                    .apply()
                setLocale(Locale(prefLanguage))
                Timber.d("Language changed to '%s'", prefLanguage?.toUpperCase(Locale.getDefault()))
            }
        }
    }

    private fun setupCalendarPermissions() {
        val context = requireContext()
        val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
        if (read == PackageManager.PERMISSION_DENIED || write == PackageManager.PERMISSION_DENIED) {
            Timber.d("Permission to access calendar is denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.WRITE_CALENDAR)
            ) {
                val builder = AlertDialog.Builder(requireContext())
                builder
                    .setMessage(getString(R.string.alert_calendar_access_required))
                    .setTitle(getString(R.string.alert_permission_required))
                    .setPositiveButton(getString(R.string.action_ok)) { _, _ ->
                        requestCalendarPermissions()
                    }
                val dialog = builder.create()
                dialog.show()
            } else {
                requestCalendarPermissions()
            }
        }
        if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED) {
            Timber.d("Permissions to access calendar are granted")
        }
    }

    private fun requestCalendarPermissions() {
        requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            ),
            ACCESS_CALENDAR_REQUEST_CODE
        )
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
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent != null && intent.data != null) {
                val filePath = intent.data
                val contentResolver = requireActivity().contentResolver
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val compressedBitmap = bitmap.compress(10)
                MainActivity.avatarView.setImageBitmap(compressedBitmap)
                prefs.edit()
                    .putString(Constants.Prefs.USER_AVATAR, compressedBitmap.encodeTobase64())
                    .apply()
                Timber.d("Avatar changed.")
            }
        }
    }
}