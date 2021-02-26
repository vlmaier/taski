package com.vmaier.taski.services

import android.content.Context
import androidx.preference.PreferenceManager


/**
 * Created by Vladas Maier
 * on 30.01.2021
 * at 11:48
 */
class PreferenceService(val context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun isCalendarSyncEnabled(): Boolean {
        return preferences.getBoolean(Keys.CALENDAR_SYNC, Defaults.CALENDAR_SYNC)
    }

    fun setCalendarSyncEnabled(value: Boolean) {
        preferences.edit().putBoolean(Keys.CALENDAR_SYNC, value).apply()
    }

    fun isDeleteCompletedTasksEnabled(): Boolean {
        return preferences.getBoolean(Keys.DELETE_COMPLETED_TASKS, Defaults.DELETE_COMPLETED_TASKS)
    }

    fun setDeleteCompletedTasksEnabled(value: Boolean) {
        preferences.edit().putBoolean(Keys.DELETE_COMPLETED_TASKS, value).apply()
    }

    fun getUserName(): String {
        return preferences.getString(Keys.USER_NAME, Defaults.USER_NAME) ?: Defaults.USER_NAME
    }

    fun setUserName(value: String) {
        preferences.edit().putString(Keys.USER_NAME, value).apply()
    }

    fun getUserAvatar(): String? {
        return preferences.getString(Keys.USER_AVATAR, null)
    }

    fun setUserAvatar(value: String?) {
        preferences.edit().putString(Keys.USER_AVATAR, value).apply()
    }

    fun resetUserAvatar() {
        preferences.edit().putString(Keys.USER_AVATAR, null).apply()
    }

    fun getRequestCodeCounter(): Int {
        return preferences.getInt(Keys.REQUEST_CODE_COUNTER, Defaults.REQUEST_CODE_COUNTER)
    }

    fun setRequestCodeCounter(value: Int) {
        preferences.edit().putInt(Keys.REQUEST_CODE_COUNTER, value).apply()
    }

    fun incrementAppLaunchCounter(): Int {
        var counter = preferences.getInt(Keys.APP_LAUNCH_COUNTER, Defaults.APP_LAUNCH_COUNTER)
        counter += 1
        preferences.edit().putInt(Keys.APP_LAUNCH_COUNTER, counter).apply()
        return counter
    }

    fun getTheme(): String {
        return preferences.getString(Keys.THEME, Defaults.THEME) ?: Defaults.THEME
    }

    fun setTheme(value: String) {
        preferences.edit().putString(Keys.THEME, value).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return preferences.getBoolean(Keys.DARK_MODE, Defaults.DARK_MODE)
    }

    fun setDarkModeEnabled(value: Boolean) {
        preferences.edit().putBoolean(Keys.DARK_MODE, value).apply()
    }

    fun getLanguage(): String {
        return preferences.getString(Keys.LANGUAGE, Defaults.LANGUAGE) ?: Defaults.LANGUAGE
    }

    fun setLanguage(value: String) {
        preferences.edit().putString(Keys.LANGUAGE, value).apply()
    }

    fun isOnboardingEnabled(): Boolean {
        return preferences.getBoolean(Keys.ONBOARDING, Defaults.ONBOARDING)
    }

    fun setOnboardingEnabled(value: Boolean) {
        preferences.edit().putBoolean(Keys.ONBOARDING, value).apply()
    }

    fun getStartOfTheWeek(): String {
        return preferences.getString(Keys.START_OF_THE_WEEK, Defaults.START_OF_THE_WEEK) ?: Defaults.START_OF_THE_WEEK
    }

    fun setStartOfTheWeek(value: String) {
        preferences.edit().putString(Keys.START_OF_THE_WEEK, value).apply()
    }

    fun getSort(type: SortType): String {
        return when (type) {
            SortType.TASKS ->
                preferences.getString(Keys.SORT_TASKS, Defaults.SORT_TASKS)
                    ?: Defaults.SORT_TASKS
            SortType.SKILLS ->
                preferences.getString(Keys.SORT_SKILLS, Defaults.SORT_SKILLS)
                    ?: Defaults.SORT_SKILLS
            SortType.CATEGORIES ->
                preferences.getString(Keys.SORT_CATEGORIES, Defaults.SORT_CATEGORIES)
                    ?: Defaults.SORT_CATEGORIES
        }
    }

    fun setSort(value: String, type: SortType) {
        when (type) {
            SortType.TASKS ->
                preferences.edit().putString(Keys.SORT_TASKS, value).apply()
            SortType.SKILLS ->
                preferences.edit().putString(Keys.SORT_SKILLS, value).apply()
            SortType.CATEGORIES ->
                preferences.edit().putString(Keys.SORT_CATEGORIES, value).apply()
        }
    }

    fun getSortOrder(type: SortType): String {
        return when (type) {
            SortType.TASKS ->
                preferences.getString(Keys.SORT_TASKS_ORDER, Defaults.SORT_TASKS_ORDER)
                    ?: Defaults.SORT_TASKS_ORDER
            SortType.SKILLS ->
                preferences.getString(Keys.SORT_SKILLS_ORDER, Defaults.SORT_SKILLS_ORDER)
                    ?: Defaults.SORT_SKILLS_ORDER
            SortType.CATEGORIES ->
                preferences.getString(Keys.SORT_CATEGORIES_ORDER, Defaults.SORT_CATEGORIES_ORDER)
                    ?: Defaults.SORT_CATEGORIES_ORDER
        }
    }

    fun setSortOrder(value: String, type: SortType) {
        when (type) {
            SortType.TASKS ->
                preferences.edit().putString(Keys.SORT_TASKS_ORDER, value).apply()
            SortType.SKILLS ->
                preferences.edit().putString(Keys.SORT_SKILLS_ORDER, value).apply()
            SortType.CATEGORIES ->
                preferences.edit().putString(Keys.SORT_CATEGORIES_ORDER, value).apply()
        }
    }

    enum class SortType {
        TASKS,
        SKILLS,
        CATEGORIES
    }

    class Keys {
        companion object {
            const val CALENDAR_SYNC = "calendar_sync"
            const val DELETE_COMPLETED_TASKS = "delete_completed_tasks"
            const val USER_NAME = "user_name"
            const val USER_AVATAR = "user_avatar"
            const val CHANGE_AVATAR = "change_avatar"
            const val RESET_AVATAR = "reset_avatar"
            const val THEME = "app_theme"
            const val DARK_MODE = "dark_mode"
            const val LANGUAGE = "app_language"
            const val SORT_TASKS = "sort_tasks"
            const val SORT_SKILLS = "sort_skills"
            const val SORT_CATEGORIES = "sort_categories"
            const val SORT_TASKS_ORDER = "sort_tasks_order"
            const val SORT_SKILLS_ORDER = "sort_skills_order"
            const val SORT_CATEGORIES_ORDER = "sort_categories_order"
            const val ONBOARDING = "onboarding"
            const val REQUEST_CODE_COUNTER = "request_code_counter"
            const val APP_LAUNCH_COUNTER = "app_starts_counter"
            const val START_OF_THE_WEEK = "start_of_the_week"
        }
    }

    class Defaults {
        companion object {
            const val CALENDAR_SYNC = false
            const val DELETE_COMPLETED_TASKS = false
            const val USER_NAME = "Taski"
            const val THEME = "Theme.Default"
            const val DARK_MODE = false
            const val LANGUAGE = "en"
            const val SORT_TASKS = "created_at"
            const val SORT_SKILLS = "name"
            const val SORT_CATEGORIES = "name"
            const val SORT_TASKS_ORDER = "asc"
            const val SORT_SKILLS_ORDER = "asc"
            const val SORT_CATEGORIES_ORDER = "asc"
            const val ONBOARDING = true
            const val REQUEST_CODE_COUNTER = 1
            const val APP_LAUNCH_COUNTER = 0
            const val START_OF_THE_WEEK = "monday"
        }
    }
}