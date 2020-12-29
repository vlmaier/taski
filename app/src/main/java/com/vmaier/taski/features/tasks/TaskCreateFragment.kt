package com.vmaier.taski.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.vmaier.taski.*
import com.vmaier.taski.MainActivity.Companion.iconDialog
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.databinding.FragmentCreateTaskBinding
import com.vmaier.taski.features.tasks.TaskListFragment.Companion.taskAdapter
import com.vmaier.taski.utils.KeyBoardHider
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.utils.RequestCode
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import timber.log.Timber
import java.text.ParseException
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
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
        binding.durationBar.progress = args?.getInt(KEY_DURATION) ?: 3
        binding.durationValue.text = binding.durationBar.getHumanReadableValue()
        binding.durationBar.setOnSeekBarChangeListener(
            getDurationBarListener(binding.durationValue, binding.xpGain, binding.durationBar)
        )

        // Difficulty settings
        binding.difficulty.setOnCheckedChangeListener { chipGroup, chipId ->
            if (chipId == NO_ID) {
                // do not allow to unselect a chip
                difficultyChip.isChecked = true
                return@setOnCheckedChangeListener
            }
            difficultyChip = chipGroup.findViewById(chipId)
            difficulty = difficultyChip.tag.toString().toUpperCase(Locale.getDefault())
            updateXpGain(binding.xpGain, binding.durationBar)
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
            }
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
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
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.goal.hideKeyboard()
        binding.details.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putString(KEY_GOAL, binding.goal.editText?.text.toString())
        out.putString(KEY_DETAILS, binding.details.editText?.text.toString())
        out.putString(KEY_DIFFICULTY,
            if (isDifficultyInitialized()) difficulty else Difficulty.REGULAR.value)
        out.putInt(KEY_DURATION, binding.durationBar.progress)
        out.putStringArray(KEY_SKILLS, binding.skills.chipValues.toTypedArray())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
        out.putString(KEY_DEADLINE_DATE, binding.deadlineDate.editText?.text.toString())
        out.putString(KEY_DEADLINE_TIME, binding.deadlineTime.editText?.text.toString())
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
            binding.goal.error = getString(R.string.error_too_short)
            return false
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
        val task = Task(
            goal = goal, details = details, duration = duration, iconId = iconId,
            dueAt = dueAt, difficulty = Difficulty.valueOf(difficulty)
        )
        val id = db.taskDao().createTask(task, skillsToAssign)
        Timber.d("Task ($id) created.")
        task.id = id
        taskAdapter.notifyDataSetChanged()
        calendarService.addToCalendar(binding.calendarSync.isChecked, task)
        if (dueAt != null) {
            // remind 15 minutes before the task is due (incl. duration)
            val durationInMs: Long = duration.toLong() * 60 * 1000
            val notifyAtInMs: Long = try {
                App.dateTimeFormat.parse(dueAt)?.time
                    ?.minus(durationInMs)
                    ?.minus(900000)
                    ?: 0
            } catch (e: ParseException) {
                App.dateFormat.parse(dueAt)?.time
                    ?.minus(durationInMs)
                    ?.minus(900000)
                    ?: 0
            }
            val taskReminderRequestCode = RequestCode.get(requireContext())
            notificationService.setReminder(
                notifyAtInMs,
                task.goal,
                "Due at ${dueAt.split(" ")[1]}",
                requireActivity(),
                taskReminderRequestCode
            )
            db.taskDao().updateAlarmRequestCode(id, taskReminderRequestCode)
        }
        return true
    }
}