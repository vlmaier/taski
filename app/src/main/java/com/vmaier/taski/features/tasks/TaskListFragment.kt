package com.vmaier.taski.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maltaisn.recurpicker.RecurrenceFinder
import com.maltaisn.recurpicker.format.RRuleFormatter
import com.vmaier.taski.MainActivity
import com.vmaier.taski.MainActivity.Companion.bottomNav
import com.vmaier.taski.MainActivity.Companion.drawerLayout
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.data.SortOrder
import com.vmaier.taski.data.SortTasks
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.data.repository.TaskRepository
import com.vmaier.taski.databinding.FragmentTaskListBinding
import com.vmaier.taski.services.PreferenceService
import com.vmaier.taski.utils.Utils


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
            val prefService = PreferenceService(context)
            val sort = prefService.getSort(PreferenceService.SortType.TASKS)
            val order = prefService.getSortOrder(PreferenceService.SortType.TASKS)
            when (sort) {
                SortTasks.CREATED_AT.value -> tasks.apply {
                    if (order == SortOrder.ASC.value) sortByDescending { it.createdAt }
                    else sortBy { it.createdAt }
                }
                SortTasks.GOAL.value -> tasks.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.goal }
                    else sortByDescending { it.goal }
                }
                SortTasks.DURATION.value -> tasks.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.duration }
                    else sortByDescending { it.duration }
                }
                SortTasks.DIFFICULTY.value -> tasks.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.difficulty }
                    else sortByDescending { it.difficulty }
                }
                SortTasks.XP_GAIN.value -> tasks.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.xp }
                    else sortByDescending { it.xp }
                }
                SortTasks.DUE_ON.value -> tasks.apply {
                    if (order == SortOrder.ASC.value) {
                        sortBy { it.dueAt ?: it.createdAt }
                    } else {
                        sortByDescending { it.dueAt ?: it.createdAt }
                    }
                }
            }
            updateSortedByHeader(context, tasks)
        }

        fun updateSortedByHeader(context: Context, tasks: MutableList<Task>) {
            val prefService = PreferenceService(context)
            val sort = prefService.getSort(PreferenceService.SortType.TASKS)
            val order = prefService.getSortOrder(PreferenceService.SortType.TASKS)
            val sortString = when (sort) {
                SortTasks.CREATED_AT.value -> context.getString(R.string.term_sort_created_at)
                SortTasks.GOAL.value -> context.getString(R.string.term_sort_goal)
                SortTasks.DURATION.value -> context.getString(R.string.term_sort_duration)
                SortTasks.DIFFICULTY.value -> context.getString(R.string.term_sort_difficulty)
                SortTasks.XP_GAIN.value -> context.getString(R.string.term_sort_xp_gain)
                SortTasks.DUE_ON.value -> context.getString(R.string.term_sort_due_on)
                else -> context.getString(R.string.term_sort_created_at)
            }
            val orderString =
                if (order == SortOrder.ASC.value) context.getString(R.string.term_sort_asc)
                else context.getString(R.string.term_sort_desc)
            if (tasks.isNotEmpty()) {
                binding.header.visibility = View.VISIBLE
                binding.header.text =
                    context.getString(R.string.term_sort_by, sortString, orderString)
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_tasks)
        toggleBottomMenu(true, View.VISIBLE)
        val foundItem = bottomNav.menu.findItem(R.id.nav_tasks)
        if (foundItem != null) foundItem.isChecked = true
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_list, container, false)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
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
                    bottomNav.getOrCreateBadge(R.id.nav_tasks).number = size
                } else {
                    bottomNav.removeBadge(R.id.nav_tasks)
                }
            }
        })
        val taskRepository = TaskRepository(requireContext())
        val tasks = taskRepository.getByStatus(Status.OPEN)
        tasks.removeAll {
            if (it.rrule != null) {
                val found = RecurrenceFinder().findBasedOn(
                    RRuleFormatter().parse(it.rrule),
                    it.createdAt,
                    it.closedAt ?: it.createdAt,
                    it.countDone,
                    1,
                    it.closedAt ?: it.createdAt,
                    false
                )
                found.size == 0 || (it.closedAt != null && found[0] > Utils.getEndOfDay())
            } else {
                false
            }
        }
        sortTasks(requireContext(), tasks)
        taskAdapter.setTasks(tasks)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val fab = MainActivity.fab
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !fab.isShown) {
                    toggleBottomMenu(true, View.VISIBLE)
                } else if (dy > 0 && fab.isShown) {
                    toggleBottomMenu(false, View.GONE)
                }
            }
        })
        val simpleItemTouchCallback = TaskItemSwipeHandler()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_sort_menu, menu)
        val prefService = PreferenceService(requireContext())
        val menuItemId = when (prefService.getSort(PreferenceService.SortType.TASKS)) {
            SortTasks.CREATED_AT.value -> R.id.sort_created_at
            SortTasks.GOAL.value -> R.id.sort_goal
            SortTasks.DURATION.value -> R.id.sort_duration
            SortTasks.DIFFICULTY.value -> R.id.sort_difficulty
            SortTasks.XP_GAIN.value -> R.id.sort_xp_gain
            SortTasks.DUE_ON.value -> R.id.sort_due_on
            else -> R.id.sort_created_at
        }
        val sortItem = menu.findItem(menuItemId)
        sortItem.isChecked = true
        val sortOrderItem = menu.findItem(R.id.sort_tasks_order)
        val order = prefService.getSortOrder(PreferenceService.SortType.TASKS)
        if (order == SortOrder.ASC.value) {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_asc_24)
        } else {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_desc_24)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefService = PreferenceService(requireContext())
        if (item.itemId == R.id.sort_tasks_order) {
            val savedOrder = prefService.getSortOrder(PreferenceService.SortType.TASKS)
            val newOrder = if (savedOrder == SortOrder.ASC.value) {
                item.setIcon(R.drawable.ic_sort_order_desc_24)
                SortOrder.DESC.value
            } else {
                item.setIcon(R.drawable.ic_sort_order_asc_24)
                SortOrder.ASC.value
            }
            prefService.setSortOrder(newOrder, PreferenceService.SortType.TASKS)
            sortTasks(requireContext(), taskAdapter.tasks)
            taskAdapter.notifyDataSetChanged()
        } else {
            item.isChecked = true
            var sort = prefService.getSort(PreferenceService.SortType.TASKS)
            when (item.itemId) {
                R.id.sort_created_at -> sort = SortTasks.CREATED_AT.value
                R.id.sort_goal -> sort = SortTasks.GOAL.value
                R.id.sort_duration -> sort = SortTasks.DURATION.value
                R.id.sort_difficulty -> sort = SortTasks.DIFFICULTY.value
                R.id.sort_xp_gain -> sort = SortTasks.XP_GAIN.value
                R.id.sort_due_on -> sort = SortTasks.DUE_ON.value
                else -> super.onOptionsItemSelected(item)
            }
            prefService.setSort(sort, PreferenceService.SortType.TASKS)
            sortTasks(requireContext(), taskAdapter.tasks)
            taskAdapter.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }
}