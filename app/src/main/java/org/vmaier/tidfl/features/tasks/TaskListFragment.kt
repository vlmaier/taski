package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Task
import org.vmaier.tidfl.databinding.FragmentTaskListBinding


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class TaskListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? {

        val binding = DataBindingUtil.inflate<FragmentTaskListBinding>(
            inflater, R.layout.fragment_task_list, container, false)
        binding.fab.setOnClickListener {
            it.findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToCreateTaskFragment())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHandler = DatabaseHandler(activity!!.applicationContext)
        val tasks = dbHandler.findAllTasks()

        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TaskListAdapter(tasks, dbHandler)
        }

        val swipeHandler = object : SwipeToCompleteCallback(activity!!.applicationContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = list_recycler_view.adapter as TaskListAdapter
                adapter.removeTaskAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(list_recycler_view)
    }

    class TaskListAdapter(private val items: MutableList<Task>,
                          private val dbHandler: DatabaseHandler)
        : RecyclerView.Adapter<TaskViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TaskViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return TaskViewHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task: Task = items[position]
            holder.bind(task)
        }

        override fun getItemCount() : Int = items.size

        fun removeTaskAt(position: Int) {
            val removedAt = items.removeAt(position)
            notifyItemRemoved(position)
            dbHandler.deleteTask(removedAt)
        }
    }

    class TaskViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {

        private var id : Long = 0
        private var goalView: TextView? = itemView.findViewById(R.id.task_goal)
        private var detailsView: TextView? = itemView.findViewById(R.id.task_details)
        private var iconView: ImageView? = itemView.findViewById(R.id.task_icon)

        fun bind(task: Task) {
            id = task.id
            goalView?.text = task.goal
            detailsView?.text = task.details
            // TODO: set correct background color (black)
            iconView?.background = App.iconPack?.getIcon(task.icon)?.drawable
            itemView.setOnClickListener {
                it.findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(task.goal, task.details))
            }
        }
    }
}