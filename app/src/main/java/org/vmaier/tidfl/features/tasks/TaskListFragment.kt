package org.vmaier.tidfl.features.tasks

import android.content.Context
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
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.databinding.FragmentTaskListBinding


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class TaskListFragment : Fragment() {

    companion object {
        lateinit var mContext: Context
        lateinit var taskAdapter: TaskAdapter
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

        taskAdapter = TaskAdapter(tasks, mContext)
        rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }

        val simpleItemTouchCallback = SwipeCallbackHandler()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }
}