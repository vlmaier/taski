package com.vmaier.taski.views

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.vmaier.taski.R


/**
 * Created by Vladas Maier
 * on 21.05.2020
 * at 17:12
 */
class EditTextDialog : DialogFragment() {

    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_HINT = "hint"
        private const val EXTRA_TEXT = "text"
        private const val EXTRA_MESSAGE = "message"
        private const val EXTRA_POSITIVE_BUTTON = "positive_button"
        private const val EXTRA_NEGATIVE_BUTTON = "negative_button"

        fun newInstance(
            title: String? = null, text: String? = null,
            message: String? = null, hint: String? = null,
            positiveButton: Int = android.R.string.ok,
            negativeButton: Int = android.R.string.cancel
        ): EditTextDialog {
            val dialog = EditTextDialog()
            val args = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putString(EXTRA_TEXT, text)
                putString(EXTRA_MESSAGE, message)
                putString(EXTRA_HINT, hint)
                putInt(EXTRA_POSITIVE_BUTTON, positiveButton)
                putInt(EXTRA_NEGATIVE_BUTTON, negativeButton)
            }
            dialog.arguments = args
            return dialog
        }
    }

    lateinit var editText: TextInputEditText

    var onPositiveButtonClicked: (() -> Unit)? = null
    var onNegativeButtonClicked: (() -> Unit)? = null
    var onPositiveButtonListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(EXTRA_TITLE)
        val text = arguments?.getString(EXTRA_TEXT)
        val message = arguments?.getString(EXTRA_MESSAGE)
        val hint = arguments?.getString(EXTRA_HINT)
        val positiveButton: Int = arguments?.getInt(EXTRA_POSITIVE_BUTTON) ?: android.R.string.ok
        val negativeButton: Int =
            arguments?.getInt(EXTRA_NEGATIVE_BUTTON) ?: android.R.string.cancel

        val view = requireActivity().layoutInflater.inflate(R.layout.edit_text_dialog, null)

        editText = view.findViewById(R.id.text)
        editText.hint = hint
        editText.setText(text)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(positiveButton) { _, _ -> onPositiveButtonClicked?.invoke() }
            .setNegativeButton(negativeButton) { _, _ -> onNegativeButtonClicked?.invoke() }
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            if (TextUtils.isEmpty(text)) button.isEnabled = false
            if (onPositiveButtonListener != null) {
                button.setOnClickListener {
                    onPositiveButtonListener?.invoke()
                }
            }
        }
        editText.doOnTextChanged { value, _, _, _ ->
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .isEnabled = !TextUtils.isEmpty(value)
        }
        alertDialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        )
        return alertDialog
    }
}