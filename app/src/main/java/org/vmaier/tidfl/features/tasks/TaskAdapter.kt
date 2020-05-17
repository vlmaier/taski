package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.util.getHumanReadableDurationValue
import org.vmaier.tidfl.util.setIcon


/**
 * Created by Vladas Maier
 * on 16/02/2020.
 * at 19:24
 */
class TaskAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var tasks: MutableList<Task> = mutableListOf()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goalView: TextView = itemView.findViewById(R.id.task_goal)
        var detailsView: TextView = itemView.findViewById(R.id.task_details)
        var durationView: TextView = itemView.findViewById(R.id.task_duration)
        var xpView: TextView = itemView.findViewById(R.id.task_xp)
        var skillsView: TextView = itemView.findViewById(R.id.skill_amount)
        var taskIconView: ImageView = itemView.findViewById(R.id.task_icon)
        var skillIconView: ImageView = itemView.findViewById(R.id.skill_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val db = AppDatabase(context)
        val task: Task = tasks[position]

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
        db.taskDao().changeTaskStatus(task.id, status)
        if (status != Status.FAILED) {
            val xpValue = db.taskDao().countOverallXpValue()
            val levelValue = xpValue.div(10000) + 1
            MainActivity.xpCounter.text = context.getString(R.string.term_xp_value, xpValue)
            MainActivity.levelCounter.text =
                context.getString(R.string.term_level_value, levelValue)
        }
        return db.taskDao().findTaskById(task.id)
    }
}