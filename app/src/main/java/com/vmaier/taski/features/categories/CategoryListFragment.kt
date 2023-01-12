package com.vmaier.taski.features.categories

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.MainActivity
import com.vmaier.taski.MainActivity.Companion.bottomNav
import com.vmaier.taski.MainActivity.Companion.drawerLayout
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.data.SortCategories
import com.vmaier.taski.data.SortOrder
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.repository.CategoryRepository
import com.vmaier.taski.databinding.FragmentCategoryListBinding
import com.vmaier.taski.services.PreferenceService


/**
 * Created by Vladas Maier
 * on 23.07.2020
 * at 17:11
 */
class CategoryListFragment : Fragment() {

    companion object {
        lateinit var categoryAdapter: CategoryAdapter
        lateinit var binding: FragmentCategoryListBinding

        private lateinit var categoryRepository: CategoryRepository

        fun sortCategories(context: Context, categories: MutableList<Category>) {
            val prefService = PreferenceService(context)
            val sort = prefService.getSort(PreferenceService.SortType.CATEGORIES)
            val order = prefService.getSortOrder(PreferenceService.SortType.CATEGORIES)
            when (sort) {
                SortCategories.NAME.value -> categories.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.name }
                    else sortByDescending { it.name }
                }
                SortCategories.XP.value -> categories.apply {
                    if (order == SortOrder.ASC.value) sortBy {
                        categoryRepository.countXP(it.id)
                    }
                    else sortByDescending {
                        categoryRepository.countXP(it.id)
                    }
                }
            }
            updateSortedByHeader(context, categories)
        }

        fun updateSortedByHeader(context: Context, categories: MutableList<Category>) {
            val prefService = PreferenceService(context)
            val sort = prefService.getSort(PreferenceService.SortType.CATEGORIES)
            val order = prefService.getSortOrder(PreferenceService.SortType.CATEGORIES)
            val sortString = when (sort) {
                SortCategories.NAME.value -> context.getString(R.string.term_sort_name)
                SortCategories.XP.value -> context.getString(R.string.term_sort_xp)
                else -> context.getString(R.string.term_sort_name)
            }
            val orderString =
                if (order == SortOrder.ASC.value) context.getString(R.string.term_sort_asc)
                else context.getString(R.string.term_sort_desc)
            if (categories.isNotEmpty()) {
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
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_categories)
        toggleBottomMenu(true, View.INVISIBLE)
        val foundItem = bottomNav.menu.findItem(R.id.nav_categories)
        if (foundItem != null) {
            foundItem.isChecked = true
        }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryRepository = CategoryRepository(requireContext())
        categoryAdapter = CategoryAdapter(requireContext())
        categoryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIfRecyclerViewIsEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkIfRecyclerViewIsEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkIfRecyclerViewIsEmpty()
            }

            fun checkIfRecyclerViewIsEmpty() {
                val visibility = if (categoryAdapter.itemCount == 0) View.VISIBLE else View.GONE
                binding.emptyRv.visibility = visibility
            }
        })
        val categories = categoryRepository.getAll()
        sortCategories(requireContext(), categories)
        categoryAdapter.setCategories(categories)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = categoryAdapter
        }
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val fab = MainActivity.fab
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (categoryAdapter.isMenuShown()) categoryAdapter.closeMenu()
                if (dy < 0 && !fab.isShown) {
                    toggleBottomMenu(true, View.VISIBLE)
                } else if (dy > 0 && fab.isShown) {
                    toggleBottomMenu(false, View.GONE)
                }
            }
        })
        val simpleItemTouchCallback = CategoryItemSwipeHandler(layoutInflater)
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.category_sort_menu, menu)
        val prefService = PreferenceService(requireContext())
        val menuItemId =
            when (prefService.getSort(PreferenceService.SortType.CATEGORIES)) {
                SortCategories.NAME.value -> R.id.sort_name
                SortCategories.XP.value -> R.id.sort_xp
                else -> R.id.sort_name
            }
        val sortItem = menu.findItem(menuItemId)
        sortItem.isChecked = true
        val sortOrderItem = menu.findItem(R.id.sort_categories_order)
        val order = prefService.getSortOrder(PreferenceService.SortType.CATEGORIES)
        if (order == SortOrder.ASC.value) {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_asc_24)
        } else {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_desc_24)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefService = PreferenceService(requireContext())
        if (item.itemId == R.id.sort_categories_order) {
            val savedOrder = prefService.getSortOrder(PreferenceService.SortType.CATEGORIES)
            val newOrder = if (savedOrder == SortOrder.ASC.value) {
                item.setIcon(R.drawable.ic_sort_order_desc_24)
                SortOrder.DESC.value
            } else {
                item.setIcon(R.drawable.ic_sort_order_asc_24)
                SortOrder.ASC.value
            }
            prefService.setSortOrder(newOrder, PreferenceService.SortType.CATEGORIES)
            sortCategories(requireContext(), categoryAdapter.categories)
            categoryAdapter.notifyDataSetChanged()
        } else {
            item.isChecked = true
            var sort = prefService.getSort(PreferenceService.SortType.CATEGORIES)
            when (item.itemId) {
                R.id.sort_name -> sort = SortCategories.NAME.value
                R.id.sort_xp -> sort = SortCategories.XP.value
                else -> super.onOptionsItemSelected(item)
            }
            prefService.setSort(sort, PreferenceService.SortType.CATEGORIES)
            sortCategories(requireContext(), categoryAdapter.categories)
            categoryAdapter.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }
}