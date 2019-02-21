package org.vmaier.tidfl.features.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Task


class TaskViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {

    private var goalView: TextView? = itemView.findViewById(R.id.goal_list_item)
    private var detailsView: TextView? = itemView.findViewById(R.id.details_list_item)

    fun bind(task: Task) {
        goalView?.text = task.goal
        detailsView?.text = task.details
    }
}