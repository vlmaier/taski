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
            const val ONBOARDING = "onboarding"
            const val DELETE_COMPLETED_TASKS = "delete_completed_tasks"
        }
    }

    class Tags {
        companion object {
            const val ICON_DIALOG_TAG = "icon_dialog"
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
            const val ONBOARDING = true
            const val DELETE_COMPLETED_TASKS = false
        }
    }
}