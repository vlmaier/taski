package org.vmaier.tidfl.features.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Task


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class TaskListFragment : Fragment() {

    private val tasks = listOf(
        Task(goal = "Do Dishes", duration = 15),
        Task(goal = "Make an Android App", details = "Things I Do For Loot", duration = 3600)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_task_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TaskListAdapter(tasks)
        }
    }

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

    class TaskViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {

        private var goalView: TextView? = itemView.findViewById(R.id.goal_list_item)
        private var detailsView: TextView? = itemView.findViewById(R.id.details_list_item)

        fun bind(task: Task) {
            goalView?.text = task.goal
            detailsView?.text = task.details
        }
    }
}