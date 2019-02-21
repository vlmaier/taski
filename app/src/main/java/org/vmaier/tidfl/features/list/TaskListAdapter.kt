package org.vmaier.tidfl.features.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.vmaier.tidfl.data.Task

class TaskListAdapter(private val list: List<Task>)
    : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task: Task = list[position]
        holder.bind(task)
    }

    override fun getItemCount() : Int = list.size
}