package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.view.LayoutInflater
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
import org.vmaier.tidfl.util.getHumanReadableValue


/**
 * Created by Vladas Maier
 * on 16/02/2020.
 * at 19:25
 */
class TaskViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
    inflater.inflate(
        R.layout.card_item, parent,
        false
    )
) {

    private var id: Long = 0
    private var goalView: TextView? = itemView.findViewById(R.id.task_goal)
    private var detailsView: TextView? = itemView.findViewById(R.id.task_details)
    private var iconView: ImageView? = itemView.findViewById(R.id.task_icon)
    private var xpView: TextView? = itemView.findViewById(R.id.task_xp_gain)
    private var durationView: TextView? = itemView.findViewById(R.id.task_duration)

    fun bind(context: Context, task: Task) {
        id = task.id
        goalView?.text = task.goal
        detailsView?.text = task.details
        val drawable = App.iconPack.getIcon(task.iconId)?.drawable!!
        DrawableCompat.setTint(
            drawable, ContextCompat.getColor(
                context, R.color.colorSecondary
            )
        )
        iconView?.background = App.iconPack.getIcon(task.iconId)?.drawable
        xpView?.text = "${task.xpGain}XP"
        durationView?.text = "${task.getHumanReadableValue()}"

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