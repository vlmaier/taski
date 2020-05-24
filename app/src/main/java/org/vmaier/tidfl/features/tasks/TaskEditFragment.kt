package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.databinding.FragmentEditTaskBinding
import org.vmaier.tidfl.utils.*
import timber.log.Timber
import java.util.*


/**
 * Created by Vladas Maier
 * on 08/02/2020
 * at 11:26
 */
class TaskEditFragment : TaskFragment() {

    private var itemPosition: Int = 0

    companion object {
        lateinit var binding: FragmentEditTaskBinding
        lateinit var difficultyChip: Chip
        lateinit var task: Task
        lateinit var assignedSkills: List<Skill>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_task, container, false
        )

        // Focus header, so it's not one of the edit texts
        binding.header.isFocusable = true

        // Get arguments from bundle
        val args = TaskEditFragmentArgs.fromBundle(this.requireArguments())
        task = args.task
        itemPosition = args.itemPosition

        // --- Goal settings
        binding.goal.editText?.setText(saved?.getString(KEY_GOAL) ?: task.goal)
        binding.goal.onFocusChangeListener = KeyBoardHider()

        // --- Details settings
        binding.details.editText?.setText(saved?.getString(KEY_DETAILS) ?: task.details)
        binding.details.onFocusChangeListener = KeyBoardHider()

        // --- Icon settings
        setTaskIcon(saved, binding.iconButton, task.iconId)

        // --- Duration settings
        binding.durationBar.progress = saved?.getInt(KEY_DURATION) ?: task.getSeekBarValue()
        binding.durationValue.text = binding.durationBar.getHumanReadableValue()
        binding.durationBar.setOnSeekBarChangeListener(
            getDurationBarListener(binding.durationValue, binding.xpGainValue, binding.durationBar)
        )

        // --- Difficulty settings
        binding.difficulty.setOnCheckedChangeListener { chipGroup, chipId ->
            if (chipId == View.NO_ID) {
                // do not allow to unselect chip
                difficultyChip.isChecked = true
                return@setOnCheckedChangeListener
            }
            difficultyChip = chipGroup.findViewById(chipId)
            difficulty = difficultyChip.tag.toString().toUpperCase(Locale.getDefault())
            updateXpGain(binding.xpGainValue, binding.durationBar)
        }
        val selectedDifficulty = Difficulty.valueOf(
            saved?.getString(KEY_DIFFICULTY) ?: task.difficulty.name
        )
        binding.difficulty.trivial.isChecked = selectedDifficulty == Difficulty.TRIVIAL
        binding.difficulty.regular.isChecked = selectedDifficulty == Difficulty.REGULAR
        binding.difficulty.hard.isChecked = selectedDifficulty == Difficulty.HARD
        binding.difficulty.insane.isChecked = selectedDifficulty == Difficulty.INSANE

        // --- Skills settings
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item, skillNames
        )
        assignedSkills = db.skillDao().findAssignedSkills(task.id)
        binding.skills.setAdapter(adapter)
        binding.skills.hint = if (skillNames.isEmpty()) getString(R.string.hint_no_skills) else ""
        binding.skills.onFocusChangeListener = getSkillsRestrictor(binding.skills)
        binding.skills.chipTokenizer = getSkillsTokenizer()
        binding.skills.setText(
            saved?.getStringArrayList(KEY_SKILLS) ?: assignedSkills.map { it.name })

        // --- Deadline settings
        val dueAt = task.dueAt
        if (dueAt != null && dueAt.isNotBlank()) {
            val dueAtParts = dueAt.split(" ")
            binding.deadlineDate.editText?.setText(
                saved?.getString(KEY_DEADLINE_DATE)
                    ?: if (dueAtParts.isNotEmpty()) dueAtParts[0] else ""
            )
            binding.deadlineTime.editText?.setText(
                saved?.getString(KEY_DEADLINE_TIME)
                    ?: if (dueAtParts.isNotEmpty()) dueAtParts[1] else ""
            )
        }
        setDeadlineDateOnClickListener(binding.deadlineDate.editText)
        setDeadlineTimeOnClickListener(binding.deadlineTime.editText)

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

        out.putString(KEY_GOAL, binding.goal.editText?.text.toString())
        out.putString(KEY_DETAILS, binding.goal.editText?.text.toString())
        out.putString(KEY_DIFFICULTY, difficulty)
        out.putInt(KEY_DURATION, binding.durationBar.progress)
        out.putStringArray(KEY_SKILLS, binding.skills.chipValues.toTypedArray())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
        out.putString(KEY_DEADLINE_DATE, binding.deadlineDate.editText?.text.toString())
        out.putString(KEY_DEADLINE_TIME, binding.deadlineTime.editText?.text.toString())

        saveChangesOnTask()
    }

    private fun saveChangesOnTask() {

        val goal = binding.goal.editText?.text.toString()
        val detailsValue = binding.details.editText?.text.toString()
        val details = if (detailsValue.isNotBlank()) detailsValue else null
        val duration = binding.durationBar.getDurationInMinutes()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val skillNames = binding.skills.chipAndTokenValues.toList()
        val skillsToAssign = db.skillDao().findByName(skillNames)
        var dueAt: String? = null
        val deadlineDate = binding.deadlineDate.editText?.text.toString()
        val deadlineTime = binding.deadlineTime.editText?.text.toString()
        if (deadlineDate.isNotBlank()) {
            dueAt = deadlineDate
            dueAt += if (deadlineTime.isNotBlank()) {
                " $deadlineTime"
            } else {
                " 08:00"
            }
        }
        val toUpdate = Task(
            id = task.id, goal = goal, details = details, duration = duration, iconId = iconId,
            createdAt = task.createdAt, dueAt = dueAt, difficulty = Difficulty.valueOf(difficulty),
            eventId = task.eventId
        )
        if (task != toUpdate || assignedSkills != skillsToAssign) {
            db.taskDao().updateTask(toUpdate, skillsToAssign)
            Timber.d("Updated task with ID: ${task.id}.")
            TaskListFragment.taskAdapter.tasks[itemPosition] = toUpdate
            TaskListFragment.taskAdapter.update()
            updateInCalendar(task, toUpdate)
            getString(R.string.event_task_updated).toast(requireContext())
        }
    }
}