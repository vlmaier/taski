package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.DurationUnit
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.databinding.FragmentEditTaskBinding
import org.vmaier.tidfl.util.*
import java.util.*


/**
 * Created by Vladas Maier
 * on 08/02/2020.
 * at 11:26
 */
class EditTaskFragment : TaskFragment() {

    var itemPosition: Int = 0

    companion object {

        lateinit var binding: FragmentEditTaskBinding
        lateinit var task: Task

        fun setIcon(context: Context, icon: Icon) {

            val drawable = IconDrawableLoader(context).loadDrawable(icon)!!
            drawable.clearColorFilter()
            DrawableCompat.setTint(
                    drawable, ContextCompat.getColor(
                    context, R.color.colorSecondary
            )
            )
            binding.editIconButton.background = drawable
            binding.editIconButton.tag = icon.id
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?)
            : View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_task, container,
                false
        )

        val args = EditTaskFragmentArgs.fromBundle(this.arguments!!)
        task = args.task
        itemPosition = args.itemPosition
        binding.goal.setText(if (saved != null) saved.getString(KEY_GOAL) else task.goal)
        binding.details.setText(if (saved != null) saved.getString(KEY_DETAILS) else task.details)
        val iconId = if (saved != null) saved.getInt(KEY_ICON_ID) else task.iconId
        binding.editIconButton.background = App.iconPack.getIconDrawable(
                iconId, IconDrawableLoader(this.context!!)
        )
        binding.editIconButton.tag = iconId

        val unitPos = saved?.getInt(KEY_DURATION_UNIT) ?: task.getPosForUnitSpinner()
        val valuePos = saved?.getInt(KEY_DURATION_VALUE) ?: task.getPosForValueSpinner()
        val difficultyPos = saved?.getInt(KEY_DIFFICULTY) ?: task.getPosForDifficultySpinner()

        binding.durationUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var firstTimeCalled = true
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
                if (firstTimeCalled) {
                    binding.durationValue.setSelection(valuePos)
                    firstTimeCalled = false
                } else if (saved != null) {
                    binding.durationValue.setSelection(saved.getInt(KEY_DURATION_VALUE))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }
        }
        binding.durationUnit.setSelection(unitPos)
        binding.difficulty.setSelection(difficultyPos)

        binding.goal.onFocusChangeListener =
                KeyBoardHider()
        binding.details.onFocusChangeListener =
                KeyBoardHider()

        binding.header.isFocusable = true

        return binding.root;
    }

    override fun onPause() {
        super.onPause()
        saveChangesOnTask()
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
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.editIconButton.tag.toString()))

        saveChangesOnTask()
    }

    private fun saveChangesOnTask() {

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
        val iconId: Int = Integer.parseInt(binding.editIconButton.tag.toString())
        if (dbHandler.checkForChangesWithinTask(task.id, goal, details, finalDuration, difficulty, iconId)) {
            val updatedTask = dbHandler.updateTask(
                    task.id, goal, details, finalDuration, difficulty, iconId
            )
            TaskListFragment.taskListAdapter.items.set(itemPosition, updatedTask!!)
            TaskListFragment.taskListAdapter.notifyItemChanged(itemPosition)
            Toast.makeText(
                    context, "Task updated",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }
}