package org.vmaier.tidfl.features.tasks

import android.content.Context
import androidx.fragment.app.Fragment


/**
 * Created by Vladas Maier
 * on 15/02/2020.
 * at 15:22
 */
open class TaskFragment : Fragment() {

    val KEY_GOAL = "goal"
    val KEY_DETAILS = "details"
    val KEY_DIFFICULTY = "difficulty"
    val KEY_DURATION_UNIT = "duration_unit"
    val KEY_DURATION_VALUE = "duration_value"
    val KEY_ICON_ID = "icon_id"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        lateinit var mContext: Context
    }
}