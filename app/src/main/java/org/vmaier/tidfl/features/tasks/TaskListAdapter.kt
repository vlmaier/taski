package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.Task


/**
 * Created by Vladas Maier
 * on 16/02/2020.
 * at 19:24
 */
class TaskListAdapter(list: MutableList<Task>, private val context: Context) :
    RecyclerView.Adapter<TaskViewHolder>() {

    private val dbHandler = DatabaseHandler(context)

    var items: MutableList<Task> = list.toMutableList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task: Task = items[position]
        holder.bind(context, task)
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(position: Int, status: Status): Task? {
        val task = items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
        return updateTaskStatus(context, task, status)
    }

    fun restoreItem(task: Task, position: Int) {
        items.add(position, task)
        notifyItemInserted(position)
        updateTaskStatus(context, task, Status.OPEN)
    }

    private fun updateTaskStatus(context: Context, task: Task, status: Status): Task? {

        val updateTask = dbHandler.updateTaskStatus(task, status)
        if (status != Status.FAILED) {
            MainActivity.xpCounter.text = "${DatabaseHandler(context).calculateOverallXp()}XP"
        }
        return updateTask
    }
}