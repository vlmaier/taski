package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.vmaier.tidfl.App
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.util.getHumanReadableDurationValue


/**
 * Created by Vladas Maier
 * on 16/02/2020.
 * at 19:24
 */
class TaskAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var tasks: MutableList<Task> = mutableListOf()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // text views
        var goalView: TextView = itemView.findViewById(R.id.task_goal)
        var detailsView: TextView = itemView.findViewById(R.id.task_details)
        var durationView: TextView = itemView.findViewById(R.id.task_duration)
        var xpView: TextView = itemView.findViewById(R.id.task_xp)
        var skillsView: TextView = itemView.findViewById(R.id.skill_amount)
        var tagsView: TextView = itemView.findViewById(R.id.skill_tags)
        // icon views
        var taskIconView: ImageView = itemView.findViewById(R.id.task_icon)
        var skillIconView: ImageView = itemView.findViewById(R.id.skill_icon)
        var tagIconView: ImageView = itemView.findViewById(R.id.skill_tag_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task: Task = tasks[position]
        holder.goalView.text = task.goal
        holder.detailsView.text = task.details
        holder.durationView.text = task.getHumanReadableDurationValue()
        holder.xpView.text = "${task.xp} XP"

        // skills
        val amountOfSkills = task.skills.size
        if (amountOfSkills > 0) {
            holder.skillsView.text = "$amountOfSkills ${if (amountOfSkills == 1) "skill" else "skills"}"
            holder.skillIconView.visibility = View.VISIBLE
            holder.skillsView.visibility = View.VISIBLE
        } else {
            holder.skillIconView.visibility = View.INVISIBLE
            holder.skillsView.visibility = View.INVISIBLE
        }

        // tags
        holder.tagIconView.visibility = View.INVISIBLE
        holder.tagsView.visibility = View.INVISIBLE

        // task icon
        val drawable: Drawable? = App.iconPack.getIcon(task.iconId)?.drawable
        if (drawable != null) {
            DrawableCompat.setTint(
                drawable, ContextCompat.getColor(
                    holder.taskIconView.context, R.color.colorSecondary
                )
            )
            holder.taskIconView.background = drawable
        }

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

    fun removeItem(position: Int, status: Status) : Task {
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

        val db = AppDatabase(this.inflater.context)

        GlobalScope.launch {
            db.taskDao().updateTaskStatus(task.id, status)
            if (status != Status.FAILED) {
                val xp = db.taskDao().calculateOverallXp(Status.DONE)
                MainActivity.xpCounter.text = "${xp} XP"
                MainActivity.levelCounter.text = "Level ${xp.div(10000) + 1}"
            }
        }

        return db.taskDao().findTask(task.id)
    }
}