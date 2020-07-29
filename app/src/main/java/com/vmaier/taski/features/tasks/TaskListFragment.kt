package com.vmaier.taski.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.Const
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Sort
import com.vmaier.taski.data.SortOrder
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.databinding.FragmentTaskListBinding
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

        fun sortTasks(context: Context, tasks: MutableList<Task>) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val sort = prefs.getString(Const.Prefs.SORT_TASKS, Const.Defaults.SORT_TASKS)
            val order = prefs.getString(Const.Prefs.SORT_TASKS_ORDER, Const.Defaults.SORT_TASKS_ORDER)
            if (order == SortOrder.ASC.value) {
                when (sort) {
                    Sort.CREATED_AT.value -> tasks.sortBy { it.createdAt }
                    Sort.GOAL.value -> tasks.sortBy { it.goal }
                    Sort.DURATION.value -> tasks.sortBy { it.duration }
                    Sort.XP_GAIN.value -> tasks.sortBy { it.xp }
                    Sort.DUE_ON.value -> tasks.sortBy { it.dueAt }
                }
            } else {
                when (sort) {
                    Sort.CREATED_AT.value -> tasks.sortByDescending { it.createdAt }
                    Sort.GOAL.value -> tasks.sortByDescending { it.goal }
                    Sort.DURATION.value -> tasks.sortByDescending { it.duration }
                    Sort.XP_GAIN.value -> tasks.sortByDescending { it.xp }
                    Sort.DUE_ON.value -> tasks.sortByDescending { it.dueAt }
                }
            }
            updateSortedByHeader(context, tasks)
        }

        fun updateSortedByHeader(context: Context, tasks: MutableList<Task>) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val sort = prefs.getString(Const.Prefs.SORT_TASKS, Const.Defaults.SORT_TASKS)
            val order = prefs.getString(Const.Prefs.SORT_TASKS_ORDER, Const.Defaults.SORT_TASKS_ORDER)
            val sortString = when (sort) {
                Sort.CREATED_AT.value -> context.getString(R.string.term_sort_created_at)
                Sort.GOAL.value -> context.getString(R.string.term_sort_goal)
                Sort.DURATION.value -> context.getString(R.string.term_sort_duration)
                Sort.XP_GAIN.value -> context.getString(R.string.term_sort_xp_gain)
                Sort.DUE_ON.value -> context.getString(R.string.term_sort_due_on)
                else -> context.getString(R.string.term_sort_created_at)
            }
            val orderString =
                if (order == SortOrder.ASC.value) context.getString(R.string.term_sort_asc)
                else context.getString(R.string.term_sort_desc)
            if (tasks.isNotEmpty()) {
                binding.header.visibility = View.VISIBLE
                binding.header.text = context.getString(R.string.term_sort_by, sortString, orderString)
            } else {
                binding.header.visibility = View.GONE
                binding.header.text = ""
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
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
                } else {
                    MainActivity.bottomNav.removeBadge(R.id.nav_tasks)
                }
            }
        })
        val db = AppDatabase(requireContext())
        val tasks = db.taskDao().findByStatus(Status.OPEN)
        sortTasks(requireContext(), tasks)
        Timber.d("${tasks.size} task(s) found.")
        taskAdapter.setTasks(tasks)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val fab = MainActivity.fab
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !fab.isShown) {
                    fab.show()
                    MainActivity.bottomNav.visibility = View.VISIBLE
                    MainActivity.bottomBar.visibility = View.VISIBLE
                } else if (dy > 0 && fab.isShown) {
                    fab.hide()
                    MainActivity.bottomNav.visibility = View.GONE
                    MainActivity.bottomBar.visibility = View.GONE
                }
            }
        })
        val simpleItemTouchCallback = TaskItemSwipeHandler()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu, menu)
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val menuItemId = when (prefs.getString(Const.Prefs.SORT_TASKS, Const.Defaults.SORT_TASKS)) {
            Sort.CREATED_AT.value -> R.id.sort_created_at
            Sort.GOAL.value -> R.id.sort_goal
            Sort.DURATION.value -> R.id.sort_duration
            Sort.XP_GAIN.value -> R.id.sort_xp_gain
            Sort.DUE_ON.value -> R.id.sort_due_on
            else -> R.id.sort_created_at
        }
        val sortItem = menu.findItem(menuItemId)
        sortItem.isChecked = true
        val sortOrderItem = menu.findItem(R.id.sort_tasks_order)
        val order = prefs.getString(Const.Prefs.SORT_TASKS_ORDER, Const.Defaults.SORT_TASKS_ORDER)
        if (order == SortOrder.ASC.value) {

            sortOrderItem.setIcon(R.drawable.ic_sort_order_asc_24)
        } else {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_desc_24)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (item.itemId == R.id.sort_tasks_order) {
            val savedOrder = prefs.getString(Const.Prefs.SORT_TASKS_ORDER, Const.Defaults.SORT_TASKS_ORDER)
            val newOrder = if (savedOrder == SortOrder.ASC.value) {
                item.setIcon(R.drawable.ic_sort_order_desc_24)
                SortOrder.DESC.value
            } else {
                item.setIcon(R.drawable.ic_sort_order_asc_24)
                SortOrder.ASC.value
            }
            prefs.edit()
                .putString(Const.Prefs.SORT_TASKS_ORDER, newOrder)
                .apply()
            sortTasks(requireContext(), taskAdapter.tasks)
            taskAdapter.notifyDataSetChanged()
        } else {
            item.isChecked = true
            var sort = prefs.getString(Const.Prefs.SORT_TASKS, Const.Defaults.SORT_TASKS)
                ?: Sort.CREATED_AT.value
            when (item.itemId) {
                R.id.sort_created_at -> sort = Sort.CREATED_AT.value
                R.id.sort_goal -> sort = Sort.GOAL.value
                R.id.sort_duration -> sort = Sort.DURATION.value
                R.id.sort_xp_gain -> sort = Sort.XP_GAIN.value
                R.id.sort_due_on -> sort = Sort.DUE_ON.value
                else -> super.onOptionsItemSelected(item)
            }
            prefs.edit()
                .putString(Const.Prefs.SORT_TASKS, sort)
                .apply()
            sortTasks(requireContext(), taskAdapter.tasks)
            taskAdapter.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }
}