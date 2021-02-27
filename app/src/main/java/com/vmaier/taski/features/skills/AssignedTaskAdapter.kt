package com.vmaier.taski.features.skills

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.R
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.setIcon


/**
 * Created by Vladas Maier
 * on 28.07.2020
 * at 18:48
 */
class AssignedTaskAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<AssignedTaskAdapter.AssignedTaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var tasks: MutableList<Task> = mutableListOf()

    inner class AssignedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goalView: TextView = itemView.findViewById(R.id.task_goal)
        var iconView: ImageView = itemView.findViewById(R.id.task_icon)
    }

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignedTaskViewHolder {
        val itemView = inflater.inflate(R.layout.item_assigned_task, parent, false)
        return AssignedTaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssignedTaskViewHolder, position: Int) {
        val task: Task = tasks[position]

        // setup "Goal" view
        holder.goalView.text = task.goal
        holder.goalView.isSelected = true

        // setup "Icon" view
        holder.iconView.setIcon(task.iconId)
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(
                SkillEditFragmentDirections.actionSkillEditFragmentToEditTaskFragment(task)
            )
        }
    }

    override fun getItemCount(): Int = tasks.size
}