package org.vmaier.tidfl.features.skills

import android.content.Context
import androidx.fragment.app.Fragment


/**
 * Created by Vladas Maier
 * on 02/03/2020.
 * at 20:48
 */
open class SkillFragment : Fragment() {

    val KEY_NAME = "name"
    val KEY_CATEGORY = "category"
    val KEY_ICON_ID = "icon_id"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        lateinit var mContext: Context
    }
}