package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.util.getHumanReadableDurationValue


/**
 * Created by Vladas Maier
 * on 16/02/2020.
 * at 19:25
 */
class TaskViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
    inflater.inflate(
        R.layout.item_task, parent,
        false
    )
) {

    private var id: Long = 0
    private var goal: TextView? = itemView.findViewById(R.id.task_goal)
    private var details: TextView? = itemView.findViewById(R.id.task_details)
    private var taskIcon: ImageView? = itemView.findViewById(R.id.task_icon)
    private var xp: TextView? = itemView.findViewById(R.id.task_xp)
    private var duration: TextView? = itemView.findViewById(R.id.task_duration)
    private var skillIcon: ImageView? = itemView.findViewById(R.id.skill_icon)
    private var skillText: TextView? = itemView.findViewById(R.id.skill_amount)

    fun bind(context: Context, task: Task) {

        id = task.id
        goal?.text = task.goal
        details?.text = task.details
        val drawable = App.iconPack.getIcon(task.iconId)?.drawable!!
        DrawableCompat.setTint(
            drawable, ContextCompat.getColor(
                context, R.color.colorSecondary
            )
        )
        taskIcon?.background = App.iconPack.getIcon(task.iconId)?.drawable
        xp?.text = "${task.xp} XP"
        duration?.text = "${task.getHumanReadableDurationValue()}"
        val skillsAmount = task.skills.size
        if (task.skills.isNotEmpty()) {
            skillIcon?.visibility = View.VISIBLE
            skillText?.text = "$skillsAmount " + if (skillsAmount == 1) "skill" else "skills"
            skillText?.visibility = View.VISIBLE
        } else {
            skillIcon?.visibility = View.INVISIBLE
            skillText?.visibility = View.INVISIBLE
        }

        itemView.setOnClickListener {
            it.findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(
                    task,
                    this.adapterPosition
                )
            )
        }
    }
}