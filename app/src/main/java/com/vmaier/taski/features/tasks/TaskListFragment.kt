package com.vmaier.taski.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Status
import com.vmaier.taski.databinding.FragmentTaskListBinding
import kotlinx.android.synthetic.main.fragment_task_list.*
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class TaskListFragment : Fragment() {

    companion object {
        lateinit var taskAdapter: TaskAdapter
        lateinit var binding: FragmentTaskListBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_tasks)
        MainActivity.fab.show()
        MainActivity.bottomNav.visibility = View.VISIBLE
        MainActivity.bottomBar.visibility = View.VISIBLE
        val foundItem = MainActivity.bottomNav.menu.findItem(R.id.nav_tasks)
        if (foundItem != null) {
            foundItem.isChecked = true
        }
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_task_list, container, false
        )
        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskAdapter = TaskAdapter(requireContext())
        taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIfRecyclerViewIsEmpty()
                updateBadge()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkIfRecyclerViewIsEmpty()
                updateBadge()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkIfRecyclerViewIsEmpty()
                updateBadge()
            }

            fun checkIfRecyclerViewIsEmpty() {
                val visibility = if (taskAdapter.tasks.isEmpty()) View.VISIBLE else View.GONE
                binding.emptyRv.visibility = visibility
            }

            fun updateBadge() {
                val size = taskAdapter.tasks.size
                if (size > 0) {
                    MainActivity.bottomNav.getOrCreateBadge(R.id.nav_tasks).number = size
                }
                MainActivity.bottomNav.getOrCreateBadge(R.id.nav_tasks).isVisible = size > 0
            }
        })
        val db = AppDatabase(requireContext())
        val tasks = db.taskDao().findByStatus(Status.OPEN)
        Timber.d("${tasks.size} task(s) found.")
        taskAdapter.fill(tasks)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }
        val simpleItemTouchCallback = SwipeCallbackHandler()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }
}