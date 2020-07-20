package com.vmaier.taski.utils

import android.view.View
import android.view.View.OnFocusChangeListener
import com.vmaier.taski.hideKeyboard


/**
 * Created by Vladas Maier
 * on 08/02/2020
 * at 16:53
 */
class KeyBoardHider : OnFocusChangeListener {

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (!hasFocus) v.hideKeyboard()
    }
}