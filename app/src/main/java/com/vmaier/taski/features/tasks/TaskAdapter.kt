package com.vmaier.taski.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.utils.getDateInAppFormat
import com.vmaier.taski.utils.getHumanReadableDurationValue
import com.vmaier.taski.utils.getSeekBarValue
import com.vmaier.taski.utils.setIcon
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Vladas Maier
 * on 16/02/2020
 * at 19:24
 */
class TaskAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var sections: MutableMap<Int, String> = mutableMapOf()
    var tasks: MutableMap<Int, Task> = mutableMapOf()

    enum class ItemViewType(val value: Int) {
        SECTION(0),
        TASK(1)
    }

    inner class TaskSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sectionView: TextView = itemView.findViewById(R.id.header)
    }

    inner class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goalView: TextView = itemView.findViewById(R.id.task_goal)
        var detailsView: TextView = itemView.findViewById(R.id.task_details)
        var durationView: TextView = itemView.findViewById(R.id.task_duration)
        var xpView: TextView = itemView.findViewById(R.id.task_xp)
        var skillsView: TextView = itemView.findViewById(R.id.skill_amount)
        var taskIconView: ImageView = itemView.findViewById(R.id.task_icon)
        var skillIconView: ImageView = itemView.findViewById(R.id.skill_icon)
    }

    override fun getItemViewType(position: Int): Int {
        return if (sections[position] != null) {
            ItemViewType.SECTION.value
        } else {
            ItemViewType.TASK.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemViewType.SECTION.value -> {
                val itemView = inflater.inflate(R.layout.item_task_section, parent, false)
                TaskSectionViewHolder(itemView)
            }
            else -> {
                val itemView = inflater.inflate(R.layout.item_task, parent, false)
                TaskItemViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            ItemViewType.SECTION.value -> {
                val holder = viewHolder as TaskSectionViewHolder

                // --- Section header
                holder.sectionView.text = sections[position]
            }
            ItemViewType.TASK.value -> {
                val holder = viewHolder as TaskItemViewHolder
                val db = AppDatabase(context)
                val task: Task? = tasks[position]
                if (task != null) {
                    // --- Goal settings
                    holder.goalView.text = task.goal
                    holder.goalView.isSelected = true

                    // --- Details settings
                    holder.detailsView.text = task.details
                    holder.detailsView.isSelected = true

                    // --- Duration settings
                    holder.durationView.text = task.getHumanReadableDurationValue(context)

                    // --- XP value settings
                    holder.xpView.text = context.getString(R.string.term_xp_value, task.xpValue)

                    // --- Skills settings
                    val skillsCount = db.skillDao().countAssignedSkills(task.id)
                    if (skillsCount > 0) {
                        holder.skillsView.text = context.resources.getQuantityString(
                            R.plurals.term_skill, skillsCount, skillsCount
                        )
                        holder.skillIconView.visibility = View.VISIBLE
                        holder.skillsView.visibility = View.VISIBLE
                    } else {
                        holder.skillIconView.visibility = View.INVISIBLE
                        holder.skillsView.visibility = View.INVISIBLE
                    }

                    // --- Icon settings
                    holder.taskIconView.setIcon(task.iconId)

                    holder.itemView.setOnClickListener {
                        it.findNavController().navigate(
                            TaskListFragmentDirections
                                .actionTaskListFragmentToEditTaskFragment(task, position)
                        )
                    }

                    holder.itemView.setOnLongClickListener { it ->
                        val menu = PopupMenu(it.context, it)
                        menu.inflate(R.menu.task_context_menu)
                        menu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.copy -> {
                                    val taskSkills = db.skillDao().findAssignedSkills(task.id)
                                    val bundle = Bundle()
                                    bundle.putString(TaskFragment.KEY_GOAL, task.goal)
                                    bundle.putString(TaskFragment.KEY_DETAILS, task.details)
                                    bundle.putString(
                                        TaskFragment.KEY_DIFFICULTY,
                                        task.difficulty.value.toUpperCase(Locale.getDefault())
                                    )
                                    bundle.putInt(TaskFragment.KEY_DURATION, task.getSeekBarValue())
                                    bundle.putStringArray(
                                        TaskFragment.KEY_SKILLS,
                                        taskSkills.map { skill -> skill.name }.toTypedArray()
                                    )
                                    bundle.putInt(TaskFragment.KEY_ICON_ID, task.iconId)
                                    if (task.dueAt != null) {
                                        val dueAtParts = task.dueAt.split(" ")
                                        bundle.putString(
                                            TaskFragment.KEY_DEADLINE_DATE,
                                            dueAtParts[0]
                                        )
                                        bundle.putString(
                                            TaskFragment.KEY_DEADLINE_TIME,
                                            dueAtParts[1]
                                        )
                                    }
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
            }
        }
    }

    internal fun fill(all: List<Task>) {
        sections.clear()
        tasks.clear()
        if (all.isNotEmpty()) buildView(all)
        notifyDataSetChanged()
    }

    internal fun update() {
        val allTasks = tasks.map { it.value }
        sections.clear()
        tasks.clear()
        if (allTasks.isNotEmpty()) buildView(allTasks)
        notifyDataSetChanged()
    }

    private fun buildView(all: List<Task>) {
        val overdue: MutableList<Task> = mutableListOf()
        val dueToday: MutableList<Task> = mutableListOf()
        val dueTomorrow: MutableList<Task> = mutableListOf()
        val dueSomeday: MutableList<Task> = mutableListOf()
        all.forEach { task: Task ->
            if (task.dueAt != null) {
                val calendar = Calendar.getInstance()
                val today = calendar.time.getDateInAppFormat()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val tomorrow = calendar.time.getDateInAppFormat()
                when (val dueAt = task.dueAt.split(" ")[0]) {
                    today -> dueToday.add(task)
                    tomorrow -> dueTomorrow.add(task)
                    else -> {
                        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        val dueAtDate = sdf.parse(dueAt)
                        if (dueAtDate != null) {
                            if (dueAtDate < sdf.parse(today)) overdue.add(task)
                            else dueSomeday.add(task)
                        } else dueSomeday.add(task)
                    }
                }
            } else dueSomeday.add(task)
        }
        var index = 0
        if (overdue.isNotEmpty()) {
            sections[index++] = context.getString(R.string.term_overdue)
            for (task in overdue) tasks[index++] = task
        }
        if (dueToday.isNotEmpty()) {
            sections[index++] = context.getString(R.string.term_due_today)
            for (task in dueToday) tasks[index++] = task
        }
        if (dueTomorrow.isNotEmpty()) {
            sections[index++] = context.getString(R.string.term_due_tomorrow)
            for (task in dueTomorrow) tasks[index++] = task
        }
        if (dueSomeday.isNotEmpty()) {
            sections[index++] = context.getString(R.string.term_due_someday)
            for (task in dueSomeday) tasks[index++] = task
        }
        Timber.d("[ ${overdue.size}, ${dueToday.size}, ${dueTomorrow.size}, ${dueSomeday.size} ]")
    }

    override fun getItemCount(): Int = sections.size + tasks.size

    fun removeItem(position: Int, status: Status): Task {
        val task = tasks.getValue(position)
        tasks.remove(position)
        update()
        Timber.d("Task removed.")
        return updateTaskStatus(task, status)
    }

    fun restoreItem(task: Task) {
        val tasks = tasks.map { it.value } as MutableList
        tasks.add(task)
        fill(tasks)
        Timber.d("Task restored.")
        updateTaskStatus(task, Status.OPEN)
    }

    private fun updateTaskStatus(task: Task, status: Status): Task {

        val db = AppDatabase(context)
        Timber.d("Status for task with ID ${task.id} updated: ${task.status} ---> $status")
        if (status != Status.OPEN) {
            db.taskDao().close(task.id, status)
        } else {
            db.taskDao().reopen(task.id)
        }
        if (status != Status.FAILED) {
            val xpValue = db.taskDao().countOverallXpValue()
            val levelValue = xpValue.div(10000) + 1
            MainActivity.xpCounterView.text = context.getString(R.string.term_xp_value, xpValue)
            MainActivity.levelCounterView.text =
                context.getString(R.string.term_level_value, levelValue)
        }
        return db.taskDao().findById(task.id)
    }
}