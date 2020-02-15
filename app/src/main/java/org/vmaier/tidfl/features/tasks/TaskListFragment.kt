package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.vmaier.tidfl.App
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Task
import org.vmaier.tidfl.databinding.FragmentTaskListBinding


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class TaskListFragment : Fragment() {

    companion object {
        lateinit var mContext: Context
        lateinit var taskListAdapter: TaskListAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentTaskListBinding>(
            inflater, R.layout.fragment_task_list, container, false
        )

        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        binding.fab.setOnClickListener {
            it.findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToCreateTaskFragment()
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHandler = DatabaseHandler(mContext)
        val tasks = dbHandler.findAllTasks()

        taskListAdapter = TaskListAdapter(tasks, mContext)
        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskListAdapter
        }

        val swipeHandler = object : SwipeToCompleteCallback(mContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = list_recycler_view.adapter as TaskListAdapter
                adapter.completeTask(mContext, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(list_recycler_view)
    }

    class TaskListAdapter(list: MutableList<Task>, private val context: Context) :
        RecyclerView.Adapter<TaskViewHolder>() {

        var items: MutableList<Task> = list.toMutableList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        private val dbHandler = DatabaseHandler(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return TaskViewHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task: Task = items[position]
            holder.bind(context, task)
        }

        override fun getItemCount(): Int = items.size

        fun completeTask(context: Context, position: Int) {
            val completedTask = items.removeAt(position)
            notifyItemRemoved(position)
            dbHandler.completeTask(completedTask)
            Toast.makeText(
                context, "Task done (+${completedTask.xpGain}XP)",
                Toast.LENGTH_SHORT
            ).show()
            MainActivity.xpCounter.text = "${DatabaseHandler(context).calculateOverallXp()}XP"
        }
    }

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
}