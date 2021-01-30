package com.vmaier.taski


/**
 * Created by Vladas Maier
 * on 13.05.2020
 * at 19:57
 */
class Const {

    class Tags {
        companion object {
            const val ICON_DIALOG_TAG = "icon_dialog"
            const val RECURRENCE_LIST_DIALOG = "recurrence-list-dialog"
            const val RECURRENCE_PICKER_DIALOG = "recurrence_picker_dialog"
        }
    }

    class Defaults {
        companion object {
            const val APP_LAUNCH_COUNTER_FOR_REVIEW = 10
            const val MINIMAL_INPUT_LENGTH = 2
        }
    }
}