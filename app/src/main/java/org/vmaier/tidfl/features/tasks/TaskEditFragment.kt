package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import com.maltaisn.icondialog.pack.IconDrawableLoader
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.databinding.FragmentEditTaskBinding
import org.vmaier.tidfl.util.*
import java.util.*


/**
 * Created by Vladas Maier
 * on 08/02/2020.
 * at 11:26
 */
class TaskEditFragment : TaskFragment() {

    var itemPosition: Int = 0

    companion object {
        lateinit var binding: FragmentEditTaskBinding
        lateinit var task: Task
        lateinit var skillNames: List<String>
        lateinit var difficulty: String
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?)
            : View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_task, container,
            false
        )

        val args = TaskEditFragmentArgs.fromBundle(this.arguments!!)
        task = args.task
        itemPosition = args.itemPosition
        binding.goal.setText(saved?.getString(TaskFragment.KEY_GOAL) ?: task.goal)
        binding.details.setText(saved?.getString(KEY_DETAILS) ?: task.details)

        setTaskIcon(saved, binding.iconButton)

        // default 3 (=15 min)
        binding.durationBar.progress = saved?.getInt(KEY_DURATION) ?: task.getSeekBarValue()
        binding.durationValue.text = binding.durationBar.getHumanReadableValue()
        binding.durationBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                binding.durationValue.text = seek.getHumanReadableValue()
                // set minimum to 1 (=5 min)
                if (progress <= 1) {
                    binding.durationBar.progress = 1
                }
            }
            override fun onStartTrackingTouch(seek: SeekBar) = Unit
            override fun onStopTrackingTouch(seek: SeekBar) = Unit
        })

        val dbHandler = DatabaseHandler(cntxt)
        skillNames = dbHandler.findAllSkillNames()

        val adapter = ArrayAdapter(
            cntxt,
            R.layout.support_simple_spinner_dropdown_item, skillNames
        )
        binding.skills.setAdapter(adapter)
        binding.skills.onFocusChangeListener = OnFocusChangeListener { view, b ->
            val allChips = binding.skills.allChips
            val chipList: MutableList<ChipInfo> = arrayListOf()
            for (chip in allChips) {
                if (skillNames.contains(chip.text) &&
                    chipList.find { it.text == chip.text } == null
                ) {
                    chipList.add(ChipInfo(chip.text, chip.data))
                }
            }
            binding.skills.setTextWithChips(chipList)
        }

        binding.skills.chipTokenizer = SpanChipTokenizer(cntxt, object : ChipSpanChipCreator() {
            override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
                val findAllSkills = dbHandler.findAllSkills()
                val skill = findAllSkills.find { it.name == text }!!
                val skillIcon = App.iconPack.getIconDrawable(
                    skill.iconId, IconDrawableLoader(cntxt)
                )!!
                DrawableCompat.setTint(
                    skillIcon, ContextCompat.getColor(
                        cntxt, R.color.colorWhite
                    )
                )
                return ChipSpan(context, text, skillIcon, data)
            }

            override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
                super.configureChip(chip, chipConfiguration)
                chip.setShowIconOnLeft(true)
            }
        }, ChipSpan::class.java)

        binding.skills.setText(saved?.getStringArrayList(KEY_SKILLS) ?: task.skillNames)

        binding.difficulty.setOnCheckedChangeListener { chipGroup, i ->
            val chip: Chip = chipGroup.findViewById(i)
            difficulty = chip.text.toString().toUpperCase(Locale.getDefault())
        }
        val selectedDifficulty = Difficulty.valueOf(
            saved?.getString(KEY_DIFFICULTY) ?: task.difficulty.name
        )
        binding.difficulty.trivial.isChecked = selectedDifficulty == Difficulty.TRIVIAL
        binding.difficulty.regular.isChecked = selectedDifficulty == Difficulty.REGULAR
        binding.difficulty.hard.isChecked = selectedDifficulty == Difficulty.HARD
        binding.difficulty.insane.isChecked = selectedDifficulty == Difficulty.INSANE

        binding.goal.onFocusChangeListener = KeyBoardHider()
        binding.details.onFocusChangeListener = KeyBoardHider()

        binding.header.isFocusable = true

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        saveChangesOnTask()
        binding.goal.hideKeyboard()
        binding.details.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)

        out.putString(TaskFragment.KEY_GOAL, binding.goal.text.toString())
        out.putString(KEY_DETAILS, binding.goal.text.toString())
        out.putString(KEY_DIFFICULTY, difficulty)
        out.putInt(KEY_DURATION, binding.durationBar.progress)
        out.putStringArray(KEY_SKILLS, binding.skills.chipValues.toTypedArray())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))

        saveChangesOnTask()
    }

    private fun saveChangesOnTask() {

        val dbHandler = DatabaseHandler(cntxt)
        val goal = binding.goal.text.toString()
        val details = binding.details.text.toString()
        val duration = binding.durationBar.getDurationInMinutes()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val skills = binding.skills.chipAndTokenValues.toTypedArray()
        if (dbHandler.checkForChangesWithinTask(
                task.id, goal, details, duration,
                Difficulty.valueOf(difficulty), iconId, skills
            )
        ) {
            val updatedTask = dbHandler.updateTask(
                task.id,
                goal,
                details,
                duration,
                Difficulty.valueOf(difficulty),
                iconId,
                skills
            )
            TaskListFragment.taskAdapter.items[itemPosition] = updatedTask!!
            TaskListFragment.taskAdapter.notifyItemChanged(itemPosition)
            Toast.makeText(
                context, "Task updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}