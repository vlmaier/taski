package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.databinding.FragmentCreateTaskBinding
import org.vmaier.tidfl.features.skills.SkillCreateFragment
import org.vmaier.tidfl.util.KeyBoardHider
import org.vmaier.tidfl.util.getDurationInMinutes
import org.vmaier.tidfl.util.getHumanReadableValue
import org.vmaier.tidfl.util.hideKeyboard
import java.util.*


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:16
 */
class TaskCreateFragment : TaskFragment() {

    companion object {
        lateinit var binding: FragmentCreateTaskBinding
        lateinit var difficultyChip: Chip
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_task, container, false
        )

        // --- Goal settings
        binding.goal.setText(saved?.getString(KEY_GOAL) ?: "")
        binding.goal.onFocusChangeListener = KeyBoardHider()
        binding.goal.requestFocus()

        // --- Details settings
        binding.details.setText(saved?.getString(KEY_DETAILS) ?: "")
        binding.details.onFocusChangeListener = KeyBoardHider()

        // --- Icon settings
        setTaskIcon(saved, binding.iconButton)

        // --- Duration settings
        binding.durationBar.progress = saved?.getInt(KEY_DURATION) ?: 3
        binding.durationValue.text = binding.durationBar.getHumanReadableValue()
        binding.durationBar.setOnSeekBarChangeListener(
            getDurationBarListener(binding.durationValue, binding.xpGainValue, binding.durationBar)
        )

        // --- Difficulty settings
        binding.difficulty.setOnCheckedChangeListener { chipGroup, chipId ->
            if (chipId == NO_ID) {
                // do not allow to unselect a chip
                difficultyChip.isChecked = true
                return@setOnCheckedChangeListener
            }
            difficultyChip = chipGroup.findViewById(chipId)
            difficulty = difficultyChip.tag.toString().toUpperCase(Locale.getDefault())
            updateXpGain(binding.xpGainValue, binding.durationBar)
        }
        val selectedDifficulty = Difficulty.valueOf(
            saved?.getString(KEY_DIFFICULTY) ?: Difficulty.REGULAR.name
        )
        binding.difficulty.trivial.isChecked = selectedDifficulty == Difficulty.TRIVIAL
        binding.difficulty.regular.isChecked = selectedDifficulty == Difficulty.REGULAR
        binding.difficulty.hard.isChecked = selectedDifficulty == Difficulty.HARD
        binding.difficulty.insane.isChecked = selectedDifficulty == Difficulty.INSANE

        // --- Skills settings
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item, skillNames
        )
        binding.skills.setAdapter(adapter)
        binding.skills.hint = if (skillNames.isEmpty()) getString(R.string.hint_no_skills) else ""
        binding.skills.onFocusChangeListener = getSkillsRestrictor(binding.skills)
        binding.skills.chipTokenizer = getSkillsTokenizer()
        binding.skills.setText(saved?.getStringArrayList(KEY_SKILLS))

        // --- Action buttons settings
        binding.createTaskButton.setOnClickListener {
            if (createTaskButtonClicked()) {
                it.findNavController().popBackStack()
                it.hideKeyboard()
            }
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        // --- Deadline settings
        binding.deadlineDate.setText(saved?.getString(KEY_DEADLINE_DATE) ?: "")
        binding.deadlineTime.setText(saved?.getString(KEY_DEADLINE_TIME) ?: "")
        setDeadlineDateOnClickListener(binding.deadlineDate)
        setDeadlineTimeOnClickListener(binding.deadlineTime)

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
        out.putString(KEY_DIFFICULTY, difficulty)
        out.putInt(KEY_DURATION, binding.durationBar.progress)
        out.putStringArray(KEY_SKILLS, binding.skills.chipValues.toTypedArray())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
        out.putString(KEY_DEADLINE_DATE, binding.deadlineDate.text.toString())
        out.putString(KEY_DEADLINE_TIME, binding.deadlineTime.text.toString())
    }

    private fun createTaskButtonClicked(): Boolean {

        val goal = binding.goal.text.toString()
        if (goal.isBlank()) {
            binding.goal.requestFocus()
            binding.goal.error = getString(R.string.error_goal_cannot_be_empty)
            return false
        }
        val detailsValue = binding.details.text.toString()
        val details = if (detailsValue.isNotBlank()) detailsValue else null
        val duration = binding.durationBar.getDurationInMinutes()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val skillNames = binding.skills.chipAndTokenValues.toList()
        val skillsToAssign = db.skillDao().findByName(skillNames)
        var dueAt: String? = null
        if (binding.deadlineDate.text.isNotBlank()) {
            dueAt = binding.deadlineDate.text.toString()
            dueAt += if (binding.deadlineTime.text.isNotBlank()) {
                " ${binding.deadlineTime.text}"
            } else {
                " 08:00"
            }
        }
        val task = Task(
            goal = goal, details = details, duration = duration, iconId = iconId,
            dueAt = dueAt, difficulty = Difficulty.valueOf(difficulty)
        )
        db.taskDao().createTask(task, skillsToAssign)
        TaskListFragment.taskAdapter.notifyDataSetChanged()
        addToCalendar(task)
        return true
    }
}