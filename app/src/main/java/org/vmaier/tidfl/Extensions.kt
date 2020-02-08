package org.vmaier.tidfl

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by Vladas Maier
 * on 07/02/2020.
 * at 21:03
 */

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}