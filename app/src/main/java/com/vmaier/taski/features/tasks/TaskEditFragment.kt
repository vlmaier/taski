package com.vmaier.taski.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.vmaier.taski.*
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.databinding.FragmentEditTaskBinding
import com.vmaier.taski.features.skills.SkillEditFragment
import com.vmaier.taski.utils.*
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import timber.log.Timber
import java.text.ParseException
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
        cameFromTaskList = args.cameFromTaskList

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
        val amountOfSkills = assignedSkills.size
        val linesNeeded = if (amountOfSkills <= 3) 1 else assignedSkills.size.div(3) + 1
        binding.skills.setLines(linesNeeded)

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
        setDeadlineDateOnTextChangedListener(binding.calendarSync, binding.deadlineDate.editText)
        val isCalendarSyncEnabled = dueAt != null && dueAt.isNotBlank() && task.eventId != null
        binding.calendarSync.isEnabled = isCalendarSyncEnabled
        binding.calendarSync.isChecked = isCalendarSyncEnabled
        binding.calendarSync.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                PermissionUtils.setupCalendarPermissions(requireContext())
            }
        }

        binding.iconButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            MainActivity.iconDialog.show(fragmentManager, Const.Tags.ICON_DIALOG_TAG)
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
            isCanceled = true
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if (!isCanceled) {
            saveChangesOnTask()
        }
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

        val goal = binding.goal.editText?.text.toString().trim()
        if (goal.isBlank()) {
            binding.goal.requestFocus()
            binding.goal.error = getString(R.string.error_cannot_be_empty)
            return
        }
        if (goal.length < 4) {
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
            eventId = task.eventId, reminderRequestCode = task.reminderRequestCode
        )
        if (task != toUpdate || assignedSkills != skillsToAssign) {
            val reminderUpdateRequired = task.dueAt != toUpdate.dueAt
            db.taskDao().updateTask(toUpdate, skillsToAssign)
            Timber.d("Updated task with ID: ${task.id}.")
            for (i in 0..TaskListFragment.taskAdapter.tasks.size) {
                if (TaskListFragment.taskAdapter.tasks[i].id == task.id) {
                    TaskListFragment.taskAdapter.tasks[i] = toUpdate
                    break
                }
            }
            if (!cameFromTaskList && SkillEditFragment.isTaskAdapterInitialized()) {
                for (i in 0..SkillEditFragment.taskAdapter.tasks.size) {
                    if (SkillEditFragment.taskAdapter.tasks[i].id == task.id) {
                        if (skillsToAssign.contains(SkillEditFragment.skill)) {
                            SkillEditFragment.taskAdapter.tasks[i] = toUpdate
                        } else {
                            SkillEditFragment.taskAdapter.tasks.remove(task)
                        }
                        break
                    }
                }
            }
            TaskListFragment.sortTasks(requireContext(), TaskListFragment.taskAdapter.tasks)
            TaskListFragment.taskAdapter.notifyDataSetChanged()
            updateInCalendar(binding.calendarSync.isChecked, task, toUpdate)
            getString(R.string.event_task_updated).toast(requireContext())
            if (reminderUpdateRequired && toUpdate.dueAt != null) {
                notificationService.cancelReminder(requireActivity(), task.reminderRequestCode)
                val notifyAtInMs: Long = try {
                    // remind 15 minutes before the task is due (incl. duration)
                    val durationInMs: Long = duration.toLong() * 60 * 1000
                    App.dateFormat.parse(toUpdate.dueAt)?.time
                        ?.minus(durationInMs)
                        ?.minus(900000)
                        ?: 0
                } catch (e: ParseException) {
                    0
                }
                val taskReminderRequestCode = RequestCode.get(requireContext())
                notificationService.setReminder(
                    notifyAtInMs,
                    toUpdate.goal,
                    "Due at ${toUpdate.dueAt.split(" ")[1]}",
                    requireActivity(),
                    taskReminderRequestCode
                )
                db.taskDao().updateAlarmRequestCode(task.id, taskReminderRequestCode)
            }
        }
    }
}