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
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.vmaier.taski.*
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.services.LevelService
import java.util.*


/**
 * Created by Vladas Maier
 * on 16/02/2020
 * at 19:24
 */
class TaskAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var tasks: MutableList<Task> = mutableListOf()
    var levelService = LevelService(context)

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goalView: TextView = itemView.findViewById(R.id.task_goal)
        var detailsView: TextView = itemView.findViewById(R.id.task_details)
        var durationView: TextView = itemView.findViewById(R.id.task_duration)
        var xpView: TextView = itemView.findViewById(R.id.task_xp)
        var taskIconView: ImageView = itemView.findViewById(R.id.task_icon)
        var skillIcon1View: ImageView = itemView.findViewById(R.id.skill_icon_1)
        var skillIcon2View: ImageView = itemView.findViewById(R.id.skill_icon_2)
        var skillIcon3View: ImageView = itemView.findViewById(R.id.skill_icon_3)
        var skillIcon4View: ImageView = itemView.findViewById(R.id.skill_icon_4)
        var skillIcon5View: ImageView = itemView.findViewById(R.id.skill_icon_5)
        var skillIcon6View: ImageView = itemView.findViewById(R.id.skill_icon_6)
        var skillIcon7View: ImageView = itemView.findViewById(R.id.skill_icon_7)
        var tooMuchSkillsView: TextView = itemView.findViewById(R.id.too_much_skills)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
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

            // --- XP settings
            holder.xpView.text = context.getString(R.string.term_xp_value, task.xp)

            // --- Skills settings
            val skills = db.skillDao().findAssignedSkills(task.id).sortedBy { it.name }
            val skillsCount = skills.size
            if (skillsCount > 0) {
                for (i in 1..skillsCount) {
                    when (i) {
                        1 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon1View.background = icon
                            holder.skillIcon1View.visibility = View.VISIBLE
                            holder.skillIcon2View.visibility = View.INVISIBLE
                            holder.skillIcon3View.visibility = View.INVISIBLE
                            holder.skillIcon4View.visibility = View.INVISIBLE
                            holder.skillIcon5View.visibility = View.INVISIBLE
                            holder.skillIcon6View.visibility = View.INVISIBLE
                            holder.skillIcon7View.visibility = View.INVISIBLE
                        }
                        2 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon2View.background = icon
                            holder.skillIcon2View.visibility = View.VISIBLE
                            holder.skillIcon3View.visibility = View.INVISIBLE
                            holder.skillIcon4View.visibility = View.INVISIBLE
                            holder.skillIcon5View.visibility = View.INVISIBLE
                            holder.skillIcon6View.visibility = View.INVISIBLE
                            holder.skillIcon7View.visibility = View.INVISIBLE
                        }
                        3 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon3View.background = icon
                            holder.skillIcon3View.visibility = View.VISIBLE
                            holder.skillIcon4View.visibility = View.INVISIBLE
                            holder.skillIcon5View.visibility = View.INVISIBLE
                            holder.skillIcon6View.visibility = View.INVISIBLE
                            holder.skillIcon7View.visibility = View.INVISIBLE
                        }
                        4 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon4View.background = icon
                            holder.skillIcon4View.visibility = View.VISIBLE
                            holder.skillIcon5View.visibility = View.INVISIBLE
                            holder.skillIcon6View.visibility = View.INVISIBLE
                            holder.skillIcon7View.visibility = View.INVISIBLE
                        }
                        5 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon5View.background = icon
                            holder.skillIcon5View.visibility = View.VISIBLE
                            holder.skillIcon6View.visibility = View.INVISIBLE
                            holder.skillIcon7View.visibility = View.INVISIBLE
                        }
                        6 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon6View.background = icon
                            holder.skillIcon6View.visibility = View.VISIBLE
                            holder.skillIcon7View.visibility = View.INVISIBLE
                        }
                        7 -> {
                            val icon = App.iconPack.getIconDrawable(
                                skills[i - 1].iconId, IconDrawableLoader(context)
                            )
                            holder.skillIcon7View.background = icon
                            holder.skillIcon7View.visibility = View.VISIBLE
                        }
                    }
                }
                if (skillsCount > 7) holder.tooMuchSkillsView.visibility = View.VISIBLE
            } else {
                holder.skillIcon1View.visibility = View.INVISIBLE
                holder.skillIcon2View.visibility = View.INVISIBLE
                holder.skillIcon3View.visibility = View.INVISIBLE
                holder.skillIcon4View.visibility = View.INVISIBLE
                holder.skillIcon5View.visibility = View.INVISIBLE
                holder.skillIcon6View.visibility = View.INVISIBLE
                holder.skillIcon7View.visibility = View.INVISIBLE
                holder.tooMuchSkillsView.visibility = View.INVISIBLE
            }

            // --- Icon settings
            holder.taskIconView.setIcon(task.iconId)

            holder.itemView.setOnClickListener {
                it.findNavController().navigate(
                    TaskListFragmentDirections
                        .actionTaskListFragmentToEditTaskFragment(task, cameFromTaskList = true)
                )
            }

            // --- Copy task button
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

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = tasks.size

    fun removeItem(position: Int, status: Status): Task {
        val task = tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
        return updateTaskStatus(task, status)
    }

    fun restoreItem(task: Task, position: Int) {
        tasks.add(position, task)
        notifyItemInserted(position)
        updateTaskStatus(task, Status.OPEN)
    }

    private fun updateTaskStatus(task: Task, status: Status): Task {
        val db = AppDatabase(context)
        val assignedSkills = db.skillDao().findAssignedSkills(task.id)
        val xpPerSkill =
            if (assignedSkills.size > 2) task.xp
            else task.xp.div(assignedSkills.size)
        if (status != Status.OPEN) {
            if (status == Status.DONE) {
                for (skill in assignedSkills) {
                    db.skillDao().updateXp(skill.id, xpPerSkill)
                    levelService.checkForSkillLevelUp(skill, xpPerSkill)
                }
                levelService.checkForOverallLevelUp(task.xp)
            }
            db.taskDao().close(task.id, status)
        } else {
            for (skill in assignedSkills) {
                db.skillDao().updateXp(skill.id, -xpPerSkill)
            }
            db.taskDao().reopen(task.id)
        }
        if (status != Status.FAILED) {
            val overallXp = db.taskDao().countOverallXp()
            val overallLevel = levelService.getOverallLevel(overallXp)
            MainActivity.xpView.text = context.getString(R.string.term_xp_value, overallXp)
            MainActivity.levelView.text = context.getString(R.string.term_level_value, overallLevel)
        }
        TaskListFragment.taskAdapter.notifyDataSetChanged()
        TaskListFragment.updateSortedByHeader(context, tasks)
        return db.taskDao().findById(task.id)
    }
}