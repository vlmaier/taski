package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.DurationUnit
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.databinding.FragmentCreateTaskBinding
import org.vmaier.tidfl.util.KeyBoardHider
import org.vmaier.tidfl.util.convert
import org.vmaier.tidfl.util.getResourceArrayId
import org.vmaier.tidfl.util.hideKeyboard
import java.util.*
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:16
 */
class CreateTaskFragment : TaskFragment() {

    companion object {

        lateinit var binding: FragmentCreateTaskBinding

        fun setIcon(context: Context, icon: Icon) {

            val drawable = IconDrawableLoader(context).loadDrawable(icon)!!
            drawable.clearColorFilter()
            DrawableCompat.setTint(
                drawable, ContextCompat.getColor(
                    context, R.color.colorSecondary
                )
            )
            binding.selectIconButton.background = drawable
            binding.selectIconButton.tag = icon.id
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_task, container, false
        )

        val iconId = saved?.getInt(KEY_ICON_ID) ?: Random.nextInt(App.iconPack.allIcons.size)
        val iconDrawable = App.iconPack.getIconDrawable(
            iconId, IconDrawableLoader(mContext)
        )!!

        DrawableCompat.setTint(
            iconDrawable, ContextCompat.getColor(
                mContext, R.color.colorSecondary
            )
        )

        binding.selectIconButton.background = iconDrawable
        binding.selectIconButton.tag = iconId

        binding.goal.setText(saved?.getString(KEY_GOAL) ?: "")
        binding.details.setText(saved?.getString(KEY_DETAILS) ?: "")
        binding.difficulty.setSelection(saved?.getInt(KEY_DIFFICULTY) ?: 1)

        binding.createTaskButton.setOnClickListener {
            createTaskButtonClicked(it)
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        binding.durationUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (pos < 0) return
                val unit = DurationUnit.valueOf(
                    binding.durationUnit.selectedItem.toString().toUpperCase(Locale.getDefault())
                )
                val values = resources.getStringArray(unit.getResourceArrayId())
                val adapter: ArrayAdapter<String> = ArrayAdapter(
                    mContext,
                    android.R.layout.simple_spinner_dropdown_item, values
                )
                binding.durationValue.adapter = adapter
                binding.durationValue.setSelection(saved?.getInt(KEY_DURATION_VALUE) ?: 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }
        }
        binding.goal.onFocusChangeListener =
            KeyBoardHider()
        binding.details.onFocusChangeListener =
            KeyBoardHider()

        binding.goal.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.goal.hideKeyboard()
        binding.details.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)

        out.putString(KEY_GOAL, binding.goal.text.toString())
        out.putString(KEY_DETAILS, binding.goal.text.toString())
        out.putInt(KEY_DIFFICULTY, binding.difficulty.selectedItemPosition)
        out.putInt(KEY_DURATION_UNIT, binding.durationUnit.selectedItemPosition)
        out.putInt(KEY_DURATION_VALUE, binding.durationValue.selectedItemPosition)
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.selectIconButton.tag.toString()))
    }

    private fun createTaskButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {

        val dbHandler = DatabaseHandler(mContext)
        val goal = binding.goal.text.toString()
        val details = binding.details.text.toString()
        val duration = binding.durationValue.selectedItem.toString().toInt()
        val durationUnit = DurationUnit.valueOf(
            binding.durationUnit.selectedItem.toString().toUpperCase(Locale.getDefault())
        )
        val finalDuration = duration.convert(durationUnit)
        val difficulty = Difficulty.valueOf(
            binding.difficulty.selectedItem.toString().toUpperCase(Locale.getDefault())
        )
        val iconId: Int = Integer.parseInt(binding.selectIconButton.tag.toString())
        dbHandler.addTask(goal, details, Status.OPEN, finalDuration, difficulty, iconId)
    }
}