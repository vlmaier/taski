package com.vmaier.taski.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.maltaisn.recurpicker.RecurrenceFinder
import com.maltaisn.recurpicker.format.RRuleFormatter
import com.vmaier.taski.*
import com.vmaier.taski.data.Quadruple
import com.vmaier.taski.data.SortTasks
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.data.repository.SkillRepository
import com.vmaier.taski.data.repository.TaskRepository
import com.vmaier.taski.features.tasks.TaskListFragment.Companion.taskAdapter
import com.vmaier.taski.features.tasks.TaskListFragment.Companion.updateSortedByHeader
import com.vmaier.taski.services.CalendarService
import com.vmaier.taski.services.LevelService
import com.vmaier.taski.services.PreferenceService
import java.util.*


/**
 * Created by Vladas Maier
 * on 16.02.2020
 * at 19:24
 */
class TaskAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val taskRepository = TaskRepository(context)
    private val skillRepository = SkillRepository(context)
    private val prefService = PreferenceService(context)
    private val levelService = LevelService(context)
    private val calendarService = CalendarService(context)
    var tasks: MutableList<Task> = mutableListOf()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goalView: TextView = itemView.findViewById(R.id.task_goal)
        var detailsView: TextView = itemView.findViewById(R.id.task_details)
        var durationView: TextView = itemView.findViewById(R.id.task_duration)
        var xpView: TextView = itemView.findViewById(R.id.task_xp)
        var taskIconView: ImageView = itemView.findViewById(R.id.task_icon)
        var recurrenceIconView: ImageView = itemView.findViewById(R.id.recurrence_icon)
        var sortIndicatorView: TextView = itemView.findViewById(R.id.task_sort_indicator)
        var skillIcon1View: ImageView = itemView.findViewById(R.id.skill_icon_1)
        var skillIcon2View: ImageView = itemView.findViewById(R.id.skill_icon_2)
        var skillIcon3View: ImageView = itemView.findViewById(R.id.skill_icon_3)
        var skillIcon4View: ImageView = itemView.findViewById(R.id.skill_icon_4)
        var skillIcon5View: ImageView = itemView.findViewById(R.id.skill_icon_5)
        var skillIcon6View: ImageView = itemView.findViewById(R.id.skill_icon_6)
        var skillIcon7View: ImageView = itemView.findViewById(R.id.skill_icon_7)
        var tooMuchSkillsView: TextView = itemView.findViewById(R.id.too_much_skills)
    }

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task: Task = tasks[position]

        // Goal settings
        holder.goalView.text = task.goal
        holder.goalView.isSelected = true

        // Details settings
        holder.detailsView.text = task.details
        holder.detailsView.isSelected = true

        // Duration settings
        holder.durationView.text = task.getHumanReadableDurationValue(context)

        // XP settings
        holder.xpView.text = context.getString(R.string.term_xp_value, task.xp)

        // Skills settings
        setupSkillIcons(holder, task)

        // Icon settings
        holder.taskIconView.setIcon(task.iconId)

        // Sort indicator settings
        val sort = prefService.getSort(PreferenceService.SortType.TASKS)
        holder.sortIndicatorView.text = when (sort) {
            SortTasks.DIFFICULTY.value -> task.difficulty.getName(context)
            SortTasks.CREATED_AT.value -> task.getHumanReadableCreationDate()
            SortTasks.DUE_ON.value -> task.getHumanReadableDueDate()
            else -> ""
        }

        // Recurrence icon settings
        holder.recurrenceIconView.visibility = if (task.rrule == null) View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(task)
            )
        }

        // Copy button
        holder.itemView.setOnLongClickListener {
            val menu = PopupMenu(it.context, it)
            menu.inflate(R.menu.task_context_menu)
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.copy -> {
                        val taskSkills = skillRepository.getAssignedSkills(task.id)
                        val bundle = Bundle()
                        bundle.putString(TaskFragment.KEY_GOAL, task.goal)
                        bundle.putString(TaskFragment.KEY_DETAILS, task.details)
                        val durationUnit = task.getDurationUnit()
                        bundle.putInt(TaskFragment.KEY_DURATION_VALUE, task.convertDurationToMinutes(durationUnit))
                        bundle.putString(TaskFragment.KEY_DURATION_UNIT, durationUnit.name)
                        bundle.putString(
                            TaskFragment.KEY_DIFFICULTY,
                            task.difficulty.value.toUpperCase(Locale.getDefault())
                        )
                        bundle.putStringArray(
                            TaskFragment.KEY_SKILLS,
                            taskSkills.map { skill -> skill.name }.toTypedArray()
                        )
                        bundle.putInt(TaskFragment.KEY_ICON_ID, task.iconId)
                        if (task.dueAt != null) {
                            val dateTime = Date(task.dueAt)
                            bundle.putString(
                                TaskFragment.KEY_DEADLINE_DATE,
                                dateTime.getDateInAppFormat()
                            )
                            bundle.putString(
                                TaskFragment.KEY_DEADLINE_TIME,
                                dateTime.getTimeInAppFormat()
                            )
                        }
                        bundle.putString(TaskFragment.KEY_RECURRENCE, task.rrule)
                        it.findNavController().navigate(
                            R.id.action_taskListFragment_to_createTaskFragment, bundle
                        )
                    }
                }
                true
            }
            menu.show()
            true
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun removeItem(position: Int, status: Status): Quadruple<Task?, LiveData<Long>, Boolean, Long?> {
        val task = tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
        return updateTaskStatus(task, status)
    }

    fun restoreItem(id: Long, position: Int, decrementCounter: Boolean, closedAt: Long?) {
        val task = taskRepository.get(id)
        if (task != null) {
            tasks.add(position, task)
            notifyItemInserted(position)
            updateTaskStatus(task, Status.OPEN, decrementCounter, closedAt)
        }
    }

    private fun setupSkillIcons(holder: TaskViewHolder, task: Task) {
        val skills = skillRepository.getAssignedSkills(task.id).sortedBy { it.name }
        val size = skills.size
        val icons = App.iconPack
        if (size > 0) {
            holder.skillIcon1View.background =
                if (size >= 1) icons.getIconDrawable(
                    skills[0].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon1View.visibility = if (size >= 1) View.VISIBLE else View.INVISIBLE
            holder.skillIcon2View.background =
                if (size >= 2) icons.getIconDrawable(
                    skills[1].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon2View.visibility = if (size >= 2) View.VISIBLE else View.INVISIBLE
            holder.skillIcon3View.background =
                if (size >= 3) icons.getIconDrawable(
                    skills[2].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon3View.visibility = if (size >= 3) View.VISIBLE else View.INVISIBLE
            holder.skillIcon4View.background =
                if (size >= 4) icons.getIconDrawable(
                    skills[3].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon4View.visibility = if (size >= 4) View.VISIBLE else View.INVISIBLE
            holder.skillIcon5View.background =
                if (size >= 5) icons.getIconDrawable(
                    skills[4].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon5View.visibility = if (size >= 5) View.VISIBLE else View.INVISIBLE
            holder.skillIcon6View.background =
                if (size >= 6) icons.getIconDrawable(
                    skills[5].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon6View.visibility = if (size >= 6) View.VISIBLE else View.INVISIBLE
            holder.skillIcon7View.background =
                if (size >= 7) icons.getIconDrawable(
                    skills[6].iconId,
                    IconDrawableLoader(context)
                ) else null
            holder.skillIcon7View.visibility = if (size >= 7) View.VISIBLE else View.INVISIBLE
            holder.tooMuchSkillsView.visibility = if (size > 7) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateTaskStatus(
        task: Task,
        status: Status,
        decrementCounter: Boolean = false,
        closedAt: Long? = null
    ): Quadruple<Task?, LiveData<Long>, Boolean, Long?> {
        val assignedSkills = skillRepository.getAssignedSkills(task.id)
        val xpPerSkill = if (assignedSkills.size >= 2) task.xp.div(assignedSkills.size) else task.xp
        var closedTaskId: LiveData<Long> = MutableLiveData<Long>().apply { value = 0L }
        var isCounterIncremented = false
        if (status != Status.OPEN) {
            if (status == Status.DONE) {
                for (skill in assignedSkills) {
                    skillRepository.updateXp(skill.id, xpPerSkill)
                    levelService.checkSkillLevelUp(skill, xpPerSkill)
                }
                levelService.checkOverallLevelUp(task.xp)
                taskRepository.incrementCountDone(task.id)
                isCounterIncremented = true
            }
            // handle recurrence
            if (task.rrule != null) {
                val isRecurrenceDone = RecurrenceFinder().findBasedOn(
                    RRuleFormatter().parse(task.rrule),
                    task.createdAt,
                    task.closedAt ?: task.createdAt,
                    task.countDone + 1,
                    1,
                    System.currentTimeMillis(),
                    false
                ).size == 0
                if (isRecurrenceDone) {
                    // recurrence is finished
                    closedTaskId = taskRepository.close(task.id, status)
                    removeTaskFromCalendar(task)
                } else {
                    closedTaskId = taskRepository.close(task.id, status, isRecurring = true)
                }
            } else {
                closedTaskId = taskRepository.close(task.id, status)
                removeTaskFromCalendar(task)
            }
        } else {
            if (task.status == Status.DONE) {
                for (skill in assignedSkills) {
                    skillRepository.updateXp(skill.id, -xpPerSkill)
                }
            }
            taskRepository.reopen(task.id)
            if (decrementCounter) {
                taskRepository.decrementCountDone(task.id)
            }
            if (task.rrule == null) {
                taskRepository.updateClosedAt(task.id, null)
            } else {
                taskRepository.updateClosedAt(task.id, closedAt)
            }
        }
        taskAdapter.notifyDataSetChanged()
        updateSortedByHeader(context, tasks)
        return Quadruple(
            taskRepository.get(task.id),
            closedTaskId,
            isCounterIncremented,
            task.closedAt
        )
    }

    private fun removeTaskFromCalendar(task: Task) {
        if (task.eventId != null && prefService.isDeleteCompletedTasksEnabled()) {
            calendarService.deleteFromCalendar(task)
        }
    }
}