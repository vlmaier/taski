package com.vmaier.taski


/**
 * Created by Vladas Maier
 * on 13.05.2020
 * at 19:57
 */
class Constants {

    class Prefs {
        companion object {
            const val CALENDAR_SYNC = "calendar_sync"
            const val USER_NAME = "user_name"
            const val CHANGE_AVATAR = "change_avatar"
            const val RESET_AVATAR = "reset_avatar"
            const val USER_AVATAR = "user_avatar"
            const val THEME = "app_theme"
            const val DARK_MODE = "dark_mode"
            const val LANGUAGE = "app_language"
            const val REQUEST_CODE_COUNTER = "request_code_counter"
        }
    }

    class Tag {
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
        }
    }
}