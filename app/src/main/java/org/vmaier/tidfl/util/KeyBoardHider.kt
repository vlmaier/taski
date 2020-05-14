package org.vmaier.tidfl.util

import android.view.View
import android.view.View.OnFocusChangeListener


/**
 * Created by Vladas Maier
 * on 08/02/2020.
 * at 16:53
 */
class KeyBoardHider : OnFocusChangeListener {

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (!hasFocus) v.hideKeyboard()
    }
}