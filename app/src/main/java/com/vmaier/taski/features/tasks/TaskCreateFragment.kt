package com.vmaier.taski.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.maltaisn.recurpicker.Recurrence
import com.maltaisn.recurpicker.format.RRuleFormatter
import com.maltaisn.recurpicker.format.RecurrenceFormatter
import com.vmaier.taski.*
import com.vmaier.taski.MainActivity.Companion.iconDialog
import com.vmaier.taski.MainActivity.Companion.recurrenceListDialog
import com.vmaier.taski.MainActivity.Companion.selectedRecurrence
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.databinding.FragmentCreateTaskBinding
import com.vmaier.taski.features.tasks.TaskListFragment.Companion.taskAdapter
import com.vmaier.taski.utils.KeyBoardHider
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.utils.RequestCode
import kotlinx.android.synthetic.main.fragment_create_task.view.*
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
        lateinit var recurrenceButton: Button
        fun isBindingInitialized() = Companion::binding.isInitialized
        fun isRecurrenceButtonInitialized() = Companion::recurrenceButton.isInitialized
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_task, container, false)

        // Get arguments from bundle
        val args = saved ?: this.arguments

        // Goal settings
        binding.goal.editText?.setText(args?.getString(KEY_GOAL) ?: "")
        binding.goal.onFocusChangeListener = KeyBoardHider()
        binding.goal.requestFocus()

        // Details settings
        binding.details.editText?.setText(args?.getString(KEY_DETAILS) ?: "")
        binding.details.onFocusChangeListener = KeyBoardHider()

        // Icon settings
        setTaskIcon(args, binding.iconButton)

        // Duration settings
        binding.durationButton.text = getHumanReadableDuration()
        binding.durationButton.setOnClickListener {
            showDurationPickerDialog(binding.durationButton, binding.xpGain)
        }

        // Difficulty settings
        binding.difficulty.setOnCheckedChangeListener { chipGroup, chipId ->
            if (chipId == NO_ID) {
                // do not allow to deselect a chip
                difficultyChip.isChecked = true
                return@setOnCheckedChangeListener
            }
            difficultyChip = chipGroup.findViewById(chipId)
            difficulty = difficultyChip.tag.toString().toUpperCase(Locale.getDefault())
            updateXpGain(binding.xpGain)
        }
        val selectedDifficulty = Difficulty.valueOf(
            args?.getString(KEY_DIFFICULTY) ?: Difficulty.REGULAR.name
        )
        binding.difficulty.trivial.isChecked = selectedDifficulty == Difficulty.TRIVIAL
        binding.difficulty.regular.isChecked = selectedDifficulty == Difficulty.REGULAR
        binding.difficulty.hard.isChecked = selectedDifficulty == Difficulty.HARD
        binding.difficulty.insane.isChecked = selectedDifficulty == Difficulty.INSANE

        // Skills settings
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item, skillNames
        )
        binding.skills.setAdapter(adapter)
        binding.skills.hint = if (skillNames.isEmpty()) getString(R.string.hint_no_skills) else ""
        binding.skills.onFocusChangeListener = getSkillsRestrictor(binding.skills)
        binding.skills.chipTokenizer = getSkillsTokenizer()
        binding.skills.setText(args?.getStringArray(KEY_SKILLS)?.toList())

        // Action buttons settings
        binding.createTaskButton.setOnClickListener {
            if (createTaskButtonClicked()) {
                it.findNavController().popBackStack()
                it.hideKeyboard()
                selectedRecurrence = Recurrence(Recurrence.Period.NONE)
            }
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
            selectedRecurrence = Recurrence(Recurrence.Period.NONE)
        }
        binding.iconButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            iconDialog.show(fragmentManager, Const.Tags.ICON_DIALOG_TAG)
        }

        // Deadline settings
        binding.deadlineDate.editText?.setText(args?.getString(KEY_DEADLINE_DATE) ?: "")
        binding.deadlineTime.editText?.setText(args?.getString(KEY_DEADLINE_TIME) ?: "")
        setDeadlineDateOnClickListener(binding.deadlineDate.editText)
        setDeadlineTimeOnClickListener(binding.deadlineTime.editText)
        setDeadlineDateOnTextChangedListener(binding.calendarSync, binding.deadlineDate.editText)
        binding.calendarSync.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) PermissionUtils.setupCalendarPermissions(requireContext())
        }

        // Recurrence settings
        recurrenceButton = binding.recurrenceButton
        binding.recurrenceButton.text =
            RecurrenceFormatter(App.dateTimeFormat).format(requireContext(), selectedRecurrence)
        binding.recurrenceButton.setOnClickListener {
            recurrenceListDialog.selectedRecurrence = selectedRecurrence
            recurrenceListDialog.startDate = System.currentTimeMillis()
            recurrenceListDialog.show(
                requireActivity().supportFragmentManager,
                Const.Tags.RECURRENCE_LIST_DIALOG
            )
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.goal.hideKeyboard()
        binding.details.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        if (isBindingInitialized()) {
            out.putString(KEY_GOAL, binding.goal.editText?.text.toString())
            out.putString(KEY_DETAILS, binding.details.editText?.text.toString())
            out.putString(
                KEY_DIFFICULTY,
                if (isDifficultyInitialized()) difficulty else Difficulty.REGULAR.value
            )
            out.putStringArray(KEY_SKILLS, binding.skills.chipValues.toTypedArray())
            out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
            out.putString(KEY_DEADLINE_DATE, binding.deadlineDate.editText?.text.toString())
            out.putString(KEY_DEADLINE_TIME, binding.deadlineTime.editText?.text.toString())
        }
    }

    private fun createTaskButtonClicked(): Boolean {
        val goal = binding.goal.editText?.text.toString().trim()
        if (goal.isBlank()) {
            binding.goal.requestFocus()
            binding.goal.error = getString(R.string.error_cannot_be_empty)
            return false
        }
        if (goal.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
            binding.goal.requestFocus()
            binding.goal.error = getString(R.string.error_too_short, Const.Defaults.MINIMAL_INPUT_LENGTH)
            return false
        }
        val detailsValue = binding.details.editText?.text.toString().trim()
        val details = if (detailsValue.isNotBlank()) detailsValue else null
        val duration = getDurationInMinutes()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val skillNames = binding.skills.chipAndTokenValues.toList()
        val skillsToAssign = skillRepository.getByNames(skillNames)
        val deadlineDate = binding.deadlineDate.editText?.text.toString()
        val deadlineTime = binding.deadlineTime.editText?.text.toString()
        var dueAt: Date? = null
        if (deadlineDate.isNotBlank()) {
            var deadline = deadlineDate
            deadline += if (deadlineTime.isNotBlank()) {
                " $deadlineTime"
            } else {
                " 08:00"
            }
            dueAt = deadline.parseToDate()
        }
        var rrule: String? = null
        if (selectedRecurrence != Recurrence.DOES_NOT_REPEAT) {
            rrule = RRuleFormatter().format(selectedRecurrence)
        }
        val task = Task(
            goal = goal, details = details, duration = duration, iconId = iconId,
            dueAt = dueAt?.time, difficulty = Difficulty.valueOf(difficulty), rrule = rrule
        )
        val id = db.taskDao().createTask(task, skillsToAssign)
        task.id = id
        taskAdapter.notifyDataSetChanged()
        calendarService.addToCalendar(binding.calendarSync.isChecked, task)
        if (dueAt != null) {
            // remind 15 minutes before the task is due (incl. duration)
            val durationInMs: Long = duration.toLong() * 60 * 1000
            val notifyAtInMs: Long = dueAt.time
                .minus(durationInMs)
                .minus(900000)
            val taskReminderRequestCode = RequestCode.get(requireContext())
            val message = if (deadlineTime.isNotBlank()) {
                getString(R.string.term_due_at, deadlineTime)
            } else {
                getString(R.string.term_due_today)
            }
            notificationService.setReminder(
                notifyAtInMs,
                task.goal,
                message,
                requireActivity(),
                taskReminderRequestCode
            )
            db.taskDao().updateAlarmRequestCode(id, taskReminderRequestCode)
        }
        return true
    }
}