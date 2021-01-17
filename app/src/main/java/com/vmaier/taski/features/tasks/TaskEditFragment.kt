package com.vmaier.taski.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.databinding.FragmentEditTaskBinding
import com.vmaier.taski.features.skills.SkillEditFragment
import com.vmaier.taski.utils.KeyBoardHider
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.utils.RequestCode
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import timber.log.Timber
import java.util.*


/**
 * Created by Vladas Maier
 * on 08/02/2020
 * at 11:26
 */
class TaskEditFragment : TaskFragment() {

    private var cameFromTaskList: Boolean = true
    private var isCanceled = false

    companion object {
        lateinit var binding: FragmentEditTaskBinding
        lateinit var difficultyChip: Chip
        lateinit var recurrenceButton: Button
        lateinit var task: Task
        lateinit var assignedSkills: List<Skill>
        fun isRecurrenceButtonInitialized() = Companion::recurrenceButton.isInitialized
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        super.onCreateView(inflater, container, saved)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_task, container, false)

        // Focus header, so it's not one of the edit texts
        binding.header.isFocusable = true

        // Get arguments from bundle
        val args = TaskEditFragmentArgs.fromBundle(this.requireArguments())
        task = args.task
        cameFromTaskList = args.cameFromTaskList

        // Creation date
        binding.createdAtValue.text = Date(task.createdAt).getDateTimeInAppFormat()

        // Goal settings
        binding.goal.editText?.setText(saved?.getString(KEY_GOAL) ?: task.goal)
        binding.goal.onFocusChangeListener = KeyBoardHider()

        // Details settings
        binding.details.editText?.setText(saved?.getString(KEY_DETAILS) ?: task.details)
        binding.details.onFocusChangeListener = KeyBoardHider()

        // Icon settings
        setTaskIcon(saved, binding.iconButton, task.iconId)

        // Duration settings
        binding.durationBar.progress = saved?.getInt(KEY_DURATION) ?: task.getSeekBarValue()
        binding.durationValue.text = binding.durationBar.getHumanReadableValue()
        binding.durationBar.setOnSeekBarChangeListener(
            getDurationBarListener(binding.durationValue, binding.xpGain, binding.durationBar)
        )

        // Difficulty settings
        binding.difficulty.setOnCheckedChangeListener { chipGroup, chipId ->
            if (chipId == View.NO_ID) {
                // do not allow to deselect chip
                difficultyChip.isChecked = true
                return@setOnCheckedChangeListener
            }
            difficultyChip = chipGroup.findViewById(chipId)
            difficulty = difficultyChip.tag.toString().toUpperCase(Locale.getDefault())
            updateXpGain(binding.xpGain, binding.durationBar)
        }
        val selectedDifficulty = Difficulty.valueOf(
            saved?.getString(KEY_DIFFICULTY) ?: task.difficulty.name
        )
        binding.difficulty.trivial.isChecked = selectedDifficulty == Difficulty.TRIVIAL
        binding.difficulty.regular.isChecked = selectedDifficulty == Difficulty.REGULAR
        binding.difficulty.hard.isChecked = selectedDifficulty == Difficulty.HARD
        binding.difficulty.insane.isChecked = selectedDifficulty == Difficulty.INSANE

        // Skills settings
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item, skillNames
        )
        assignedSkills = db.skillDao().findAssignedSkills(task.id)
        binding.skills.setAdapter(adapter)
        binding.skills.hint = if (skillNames.isEmpty()) getString(R.string.hint_no_skills) else ""
        binding.skills.onFocusChangeListener = getSkillsRestrictor(binding.skills)
        binding.skills.chipTokenizer = getSkillsTokenizer()
        binding.skills.setText(saved?.getStringArrayList(KEY_SKILLS) ?: assignedSkills.map { it.name })
        val amountOfSkills = assignedSkills.size
        val linesNeeded = if (amountOfSkills <= 3) 1 else assignedSkills.size.div(3) + 1
        binding.skills.setLines(linesNeeded)

        // Deadline settings
        val dueAt = task.dueAt
        if (dueAt != null) {
            val dateTime = Date(dueAt)
            val date = saved?.getString(KEY_DEADLINE_DATE) ?: dateTime.getDateInAppFormat()
            val time = saved?.getString(KEY_DEADLINE_TIME) ?: dateTime.getTimeInAppFormat()
            binding.deadlineDate.editText?.setText(date)
            binding.deadlineTime.editText?.setText(if (time == "00:00") "" else time)
        }
        setDeadlineDateOnClickListener(binding.deadlineDate.editText)
        setDeadlineTimeOnClickListener(binding.deadlineTime.editText)
        setDeadlineDateOnTextChangedListener(binding.calendarSync, binding.deadlineDate.editText)
        binding.calendarSync.isEnabled = dueAt != null
        binding.calendarSync.isChecked = dueAt != null && task.eventId != null
        binding.calendarSync.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) PermissionUtils.setupCalendarPermissions(requireContext())
        }

        // Recurrence settings
        recurrenceButton = binding.recurrenceButton
        val recurrence = RecurrenceFormatter(App.dateTimeFormat).format(requireContext(),
            if (task.rrule != null) {
                val taskRecurrence = RRuleFormatter().parse(task.rrule.toString())
                selectedRecurrence = taskRecurrence
                taskRecurrence
            } else {
                selectedRecurrence
            }
        )
        binding.recurrenceButton.text = recurrence
        binding.recurrenceButton.setOnClickListener {
            recurrenceListDialog.selectedRecurrence = selectedRecurrence
            recurrenceListDialog.startDate = System.currentTimeMillis()
            recurrenceListDialog.show(requireActivity().supportFragmentManager, Const.Tags.RECURRENCE_LIST_DIALOG)
        }

        binding.iconButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            iconDialog.show(fragmentManager, Const.Tags.ICON_DIALOG_TAG)
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
            isCanceled = true
            selectedRecurrence = Recurrence(Recurrence.Period.NONE)
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if (!isCanceled) saveChangesOnTask()
        binding.goal.hideKeyboard()
        binding.details.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putString(KEY_GOAL, binding.goal.editText?.text.toString())
        out.putString(KEY_DETAILS, binding.goal.editText?.text.toString())
        out.putString(KEY_DIFFICULTY,
                if (isDifficultyInitialized()) difficulty else Difficulty.REGULAR.value)
        out.putInt(KEY_DURATION, binding.durationBar.progress)
        out.putStringArray(KEY_SKILLS, binding.skills.chipValues.toTypedArray())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
        out.putString(KEY_DEADLINE_DATE, binding.deadlineDate.editText?.text.toString())
        out.putString(KEY_DEADLINE_TIME, binding.deadlineTime.editText?.text.toString())
        saveChangesOnTask()
    }

    private fun saveChangesOnTask() {
        val goal = binding.goal.editText?.text.toString().trim()
        if (goal.isBlank()) {
            binding.goal.requestFocus()
            binding.goal.error = getString(R.string.error_cannot_be_empty)
            return
        }
        if (goal.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
            binding.goal.requestFocus()
            binding.goal.error = getString(R.string.error_too_short)
            return
        }
        val detailsValue = binding.details.editText?.text.toString().trim()
        val details = if (detailsValue.isNotBlank()) detailsValue else null
        val duration = binding.durationBar.getDurationInMinutes()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val skillNames = binding.skills.chipAndTokenValues.toList()
        val skillsToAssign = db.skillDao().findByName(skillNames)
        val deadlineDate = binding.deadlineDate.editText?.text.toString()
        val deadlineTime = binding.deadlineTime.editText?.text.toString()
        var dueAt: Date? = null
        if (deadlineDate.isNotBlank()) {
            var deadline = deadlineDate
            deadline += " $deadlineTime".trimEnd()
            dueAt = deadline.parseToDate()
        }
        val rrule = if (selectedRecurrence != Recurrence.DOES_NOT_REPEAT) {
            RRuleFormatter().format(selectedRecurrence)
        } else {
            null
        }
        val toUpdate = Task(
            id = task.id, goal = goal, details = details, duration = duration, iconId = iconId,
            createdAt = task.createdAt, dueAt = dueAt?.time, closedAt = task.closedAt,
            difficulty = Difficulty.valueOf(difficulty), eventId = task.eventId,
            reminderRequestCode = task.reminderRequestCode, rrule = rrule, countDone = task.countDone
        )
        if (task != toUpdate || assignedSkills != skillsToAssign) {
            val reminderUpdateRequired = task.dueAt != toUpdate.dueAt
            db.taskDao().updateTask(toUpdate, skillsToAssign)
            Timber.d("Updated task with ID: ${task.id}.")
            updateInAdapter(toUpdate, skillsToAssign)
            TaskListFragment.sortTasks(requireContext(), TaskListFragment.taskAdapter.tasks)
            TaskListFragment.taskAdapter.notifyDataSetChanged()
            calendarService.updateInCalendar(binding.calendarSync.isChecked, task, toUpdate)
            getString(R.string.event_task_updated).toast(requireContext())
            if (reminderUpdateRequired && toUpdate.dueAt != null) {
                notificationService.cancelReminder(requireActivity(), task.reminderRequestCode)
                // remind 15 minutes before the task is due (incl. duration)
                val durationInMs: Long = duration.toLong() * 60 * 1000
                val notifyAtInMs: Long = dueAt?.time
                    ?.minus(durationInMs)
                    ?.minus(900000)
                    ?: 0
                val taskReminderRequestCode = RequestCode.get(requireContext())
                val message = if (deadlineTime.isNotBlank()) {
                    getString(R.string.term_due_at, deadlineTime)
                } else {
                    getString(R.string.term_due_today)
                }
                notificationService.setReminder(
                    notifyAtInMs,
                    toUpdate.goal,
                    message,
                    requireActivity(),
                    taskReminderRequestCode
                )
                db.taskDao().updateAlarmRequestCode(task.id, taskReminderRequestCode)
            }
        // enable sync (only) after disabling before
        } else if (binding.calendarSync.isChecked && task.eventId == null) {
            calendarService.addToCalendar(true, task)
            getString(R.string.event_task_updated).toast(requireContext())
        // disable sync (only) after enabling before
        } else if (!binding.calendarSync.isChecked && task.eventId != null) {
            calendarService.deleteCalendarEvent(task)
            getString(R.string.event_task_updated).toast(requireContext())
        }
        selectedRecurrence = Recurrence(Recurrence.Period.NONE)
    }

    private fun updateInAdapter(task: Task, skills: List<Skill>) {
        for (i in 0..TaskListFragment.taskAdapter.tasks.size) {
            if (TaskListFragment.taskAdapter.tasks[i].id == task.id) {
                TaskListFragment.taskAdapter.tasks[i] = task
                break
            }
        }
        if (!cameFromTaskList && SkillEditFragment.isTaskAdapterInitialized()) {
            for (i in 0..SkillEditFragment.taskAdapter.tasks.size) {
                if (SkillEditFragment.taskAdapter.tasks[i].id == task.id) {
                    if (skills.contains(SkillEditFragment.skill)) {
                        SkillEditFragment.taskAdapter.tasks[i] = task
                    } else {
                        SkillEditFragment.taskAdapter.tasks.remove(task)
                    }
                    break
                }
            }
        }
    }
}