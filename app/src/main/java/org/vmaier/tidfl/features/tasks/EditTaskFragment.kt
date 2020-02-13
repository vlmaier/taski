package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Task
import org.vmaier.tidfl.databinding.FragmentEditTaskBinding
import org.vmaier.tidfl.hideKeyboard


/**
 * Created by Vladas Maier
 * on 08/02/2020.
 * at 11:26
 */
class EditTaskFragment : Fragment() {

    companion object {

        lateinit var mContext: Context
        lateinit var binding: FragmentEditTaskBinding
        lateinit var task: Task

        fun setIcon(context: Context, icon: Icon) {

            val drawable = IconDrawableLoader(context).loadDrawable(icon)!!
            drawable.clearColorFilter()
            DrawableCompat.setTint(drawable, ContextCompat.getColor(
                context, R.color.colorSecondary))
            binding.editIconButton.background = drawable
            binding.editIconButton.tag = icon.id
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_task, container,
            false)

        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val args = TaskDetailsFragmentArgs.fromBundle(this.arguments!!)
        task = args.task
        binding.goal.setText(task.goal)
        binding.details.setText(task.details)
        binding.editIconButton.background = App.iconPack.getIconDrawable(
            task.iconId, IconDrawableLoader(this.context!!)
        )
        binding.editIconButton.tag = task.iconId

        var unitPos = 0
        var finalDuration = task.duration
        when (task.duration) {
            // minutes
            in 5..45 -> {}
            // hours
            in 60..1200 -> {
                unitPos = 1
                finalDuration = task.duration.div(60)
            }
            // days
            else -> {
                unitPos = 2
                finalDuration = task.duration.div(60 * 24)
            }
        }
        val valuePos = when (unitPos) {
            0 -> when (finalDuration) {
                5 -> 0
                10 -> 1
                15 -> 2
                30 -> 3
                45 -> 4
                else -> 0
            }
            1 -> when (finalDuration) {
                1 -> 0
                2 -> 1
                3 -> 2
                4 -> 3
                8 -> 4
                12 -> 5
                16 -> 6
                20 -> 7
                else -> 0
            }
            2 -> when (finalDuration) {
                1 -> 0
                2 -> 1
                3 -> 2
                4 -> 3
                5 -> 4
                6 -> 5
                7 -> 6
                else -> 0
            }
            else -> 0
        }
        binding.durationUnit.setSelection(unitPos)
        binding.difficulty.setSelection(
            when (task.difficulty) {
                Difficulty.TRIVIAL -> 0
                Difficulty.REGULAR -> 1
                Difficulty.HARD -> 2
                Difficulty.INSANE -> 3
            }
        )

        binding.editTaskButton.setOnClickListener {
            editTaskButtonClicked(it)
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        binding.durationUnit.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            var firstTimeCalled = true
            override fun onItemSelected(parent : AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (pos < 0) return
                val resourceArrayId : Int
                if (binding.durationUnit.selectedItem.toString() == "minutes") {
                    resourceArrayId = R.array.duration_minutes
                } else if (binding.durationUnit.selectedItem.toString() == "hours") {
                    resourceArrayId = R.array.duration_hours
                } else {
                    resourceArrayId = R.array.duration_days
                }
                val values = resources.getStringArray(resourceArrayId)
                val adapter : ArrayAdapter<String> = ArrayAdapter(mContext,
                    android.R.layout.simple_spinner_dropdown_item, values)
                binding.durationValue.adapter = adapter
                if (firstTimeCalled) {
                    binding.durationValue.setSelection(valuePos)
                    firstTimeCalled = false
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.goal.onFocusChangeListener = KeyBoardHider()
        binding.details.onFocusChangeListener = KeyBoardHider()

        return binding.root;
    }

    private fun editTaskButtonClicked(view : View) {

        val dbHandler = DatabaseHandler(mContext)
        val goal = binding.goal.text.toString()
        val details = binding.details.text.toString()
        val duration = binding.durationValue.selectedItem.toString().toInt()
        val finalDuration = when (binding.durationUnit.selectedItem.toString()) {
            "minutes" -> duration
            "hours" -> duration * 60
            "days" -> duration * 60 * 24
            else -> {
                15
            }
        }
        val difficulty = when (binding.difficulty.selectedItem.toString()) {
            "trivial" -> Difficulty.TRIVIAL
            "regular" -> Difficulty.REGULAR
            "hard" -> Difficulty.HARD
            "insane" -> Difficulty.INSANE
            else -> {
                Difficulty.REGULAR
            }
        }
        val iconId : Int = Integer.parseInt(binding.editIconButton.tag.toString())
        dbHandler.updateTask(task.id, goal, details, finalDuration, difficulty, iconId)
    }
}