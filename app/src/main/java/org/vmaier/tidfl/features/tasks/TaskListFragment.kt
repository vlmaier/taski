package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.databinding.FragmentTaskListBinding


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class TaskListFragment : Fragment() {

    companion object {
        lateinit var taskAdapter: TaskAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
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
        taskAdapter = TaskAdapter(requireContext())
        val db = AppDatabase(requireContext())
        val tasks = db.taskDao().findTasksWithStatus(Status.OPEN)
        taskAdapter.setTasks(tasks)
        rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }
        val simpleItemTouchCallback = SwipeCallbackHandler()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }
}