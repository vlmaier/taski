package com.vmaier.taski.features.categories

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
import com.vmaier.taski.databinding.FragmentCategoryListBinding
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 23/07/2020
 * at 17:11
 */
class CategoryListFragment : Fragment() {

    companion object {
        lateinit var categoryAdapter: CategoryAdapter
        lateinit var binding: FragmentCategoryListBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_categories)
        MainActivity.fab.show()
        MainActivity.bottomNav.visibility = View.INVISIBLE
        MainActivity.bottomBar.visibility = View.INVISIBLE
        val foundItem = MainActivity.bottomNav.menu.findItem(R.id.nav_categories)
        if (foundItem != null) {
            foundItem.isChecked = true
        }
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_category_list, container, false
        )
        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val db = AppDatabase(requireContext())
        val categories = db.categoryDao().findAll()
        Timber.d("${categories.size} categor${if (categories.size > 1) "ies" else "y"} found.")
        categories.sortBy { it.name }
        categoryAdapter.setCategories(categories)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = categoryAdapter
        }
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val fab = MainActivity.fab
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (categoryAdapter.isMenuShown()) categoryAdapter.closeMenu()
                if (dy < 0 && !fab.isShown) fab.show()
                else if (dy > 0 && fab.isShown) fab.hide()
            }
        })
        val simpleItemTouchCallback = CategoryItemSwipeHandler()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
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
    }
}