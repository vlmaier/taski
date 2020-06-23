package com.vmaier.taski.features.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.utils.Const
import com.vmaier.taski.utils.compress
import com.vmaier.taski.utils.encodeTobase64
import com.vmaier.taski.views.EditTextDialog
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 20/04/2020
 * at 20:34
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        const val ACCESS_CALENDAR_REQUEST = 2
        var isCalendarSyncOn = false
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
        val changeAvatar = preferenceScreen.findPreference(Const.Prefs.CHANGE_AVATAR) as Preference?
        changeAvatar?.setOnPreferenceClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(
                    Intent.createChooser(
                        galleryIntent,
                        getString(R.string.heading_select_image)
                    ), PICK_IMAGE_REQUEST
                )
            }
            true
        }
        val resetAvatar = preferenceScreen.findPreference(Const.Prefs.RESET_AVATAR) as Preference?
        resetAvatar?.setOnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.alert_reset_avatar))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.action_proceed_with_reset)) { _, _ ->
                    preferenceManager.sharedPreferences
                        .edit().putString(Const.Prefs.USER_AVATAR, null)
                        .apply()
                    MainActivity.avatarView.setImageDrawable(
                        getDrawable(requireContext(), R.mipmap.ic_launcher_round)
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
            val usernameValue = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(Const.Prefs.USER_NAME, getString(R.string.app_name))
            val dialog = EditTextDialog.newInstance(
                title = getString(R.string.heading_user_name),
                hint = getString(R.string.hint_user_name),
                text = usernameValue,
                positiveButton = R.string.action_set
            )
            dialog.onPositiveButtonClicked = {
                val textValue = dialog.editText.text.toString()
                getDefaultSharedPreferences(requireContext())
                    .edit().putString(Const.Prefs.USER_NAME, textValue)
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Const.Prefs.CALENDAR_SYNC -> {
                calendarSyncPref = findPreference(key)!!
                isCalendarSyncOn = calendarSyncPref.isChecked
                if (isCalendarSyncOn) {
                    setupCalendarPermissions()
                }
                sharedPreferences
                    .edit().putBoolean(Const.Prefs.CALENDAR_SYNC, isCalendarSyncOn)
                    .apply()
                Timber.d(
                    "Calendar synchronization is %s.",
                    if (isCalendarSyncOn) "enabled" else "disabled"
                )
            }
            Const.Prefs.APP_THEME -> {
                val pref: ListPreference? = findPreference(key)
                val selectedTheme = pref?.value
                sharedPreferences.edit().putString(Const.Prefs.APP_THEME, selectedTheme).apply()
                if (selectedTheme == getString(R.string.theme_default_name)) {
                    activity?.setTheme(R.style.Theme_Default)
                } else if (selectedTheme == getString(R.string.theme_sailor_name)) {
                    activity?.setTheme(R.style.Theme_Sailor)
                }
                activity?.recreate()
            }
        }
    }

    private fun setupCalendarPermissions() {
        val context = requireContext()
        val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
        if (read == PackageManager.PERMISSION_DENIED || write == PackageManager.PERMISSION_DENIED) {
            Timber.d("Permission to access calendar is denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_CALENDAR
                )
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
            ACCESS_CALENDAR_REQUEST
        )
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (intent != null && intent.data != null) {
                val filePath = intent.data
                val contentResolver = requireActivity().contentResolver
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val compressedBitmap = bitmap.compress(10)
                MainActivity.avatarView.setImageBitmap(compressedBitmap)
                getDefaultSharedPreferences(context)
                    .edit().putString(Const.Prefs.USER_AVATAR, compressedBitmap.encodeTobase64())
                    .apply()
                Timber.d("Avatar changed.")
            }
        }
    }
}