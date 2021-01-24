package com.vmaier.taski


/**
 * Created by Vladas Maier
 * on 13.05.2020
 * at 19:57
 */
class Const {

    class Prefs {
        companion object {
            const val CALENDAR_SYNC = "calendar_sync"
            const val USER_NAME = "user_name"
            const val CHANGE_AVATAR = "change_avatar"
            const val RESET_AVATAR = "reset_avatar"
            const val USER_AVATAR = "user_avatar"
            const val REQUEST_CODE_COUNTER = "request_code_counter"
            const val THEME = "app_theme"
            const val DARK_MODE = "dark_mode"
            const val LANGUAGE = "app_language"
            const val SORT_TASKS = "sort_tasks"
            const val SORT_TASKS_ORDER = "sort_tasks_order"
            const val SORT_SKILLS = "sort_skills"
            const val SORT_SKILLS_ORDER = "sort_skills_order"
            const val SORT_CATEGORIES = "sort_categories"
            const val SORT_CATEGORIES_ORDER = "sort_categories_order"
            const val ONBOARDING = "onboarding"
            const val DELETE_COMPLETED_TASKS = "delete_completed_tasks"
            const val APP_LAUNCH_COUNTER = "app_starts_counter"
        }
    }

    class Tags {
        companion object {
            const val ICON_DIALOG_TAG = "icon_dialog"
            const val RECURRENCE_LIST_DIALOG = "recurrence-list-dialog"
            const val RECURRENCE_PICKER_DIALOG = "recurrence_picker_dialog"
        }
    }

    class Defaults {
        companion object {
            const val CALENDAR_SYNC = false
            const val USER_NAME = "Taski"
            const val THEME = "Theme.Default"
            const val DARK_MODE = false
            const val LANGUAGE = "en"
            const val SORT_TASKS = "created_at"
            const val SORT_TASKS_ORDER = "asc"
            const val SORT_SKILLS = "name"
            const val SORT_SKILLS_ORDER = "asc"
            const val SORT_CATEGORIES = "name"
            const val SORT_CATEGORIES_ORDER = "asc"
            const val ONBOARDING = true
            const val DELETE_COMPLETED_TASKS = false
            const val APP_LAUNCH_COUNTER = 0
            const val APP_LAUNCH_COUNTER_FOR_REVIEW = 10
            const val MINIMAL_INPUT_LENGTH = 2
        }
    }
}