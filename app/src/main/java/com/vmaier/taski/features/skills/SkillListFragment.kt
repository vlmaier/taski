package com.vmaier.taski.features.skills

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.MainActivity
import com.vmaier.taski.MainActivity.Companion.drawerLayout
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.SortOrder
import com.vmaier.taski.data.SortSkills
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.data.repository.CategoryRepository
import com.vmaier.taski.databinding.FragmentSkillListBinding
import com.vmaier.taski.services.PreferenceService


/**
 * Created by Vladas Maier
 * on 25.02.2020
 * at 18:01
 */
class SkillListFragment : Fragment() {

    companion object {
        lateinit var skillAdapter: SkillAdapter
        lateinit var binding: FragmentSkillListBinding
        lateinit var categoryRepository: CategoryRepository

        fun sortSkills(context: Context, skills: MutableList<Skill>) {
            val prefService = PreferenceService(context)
            val sort = prefService.getSort(PreferenceService.SortType.SKILLS)
            val order = prefService.getSortOrder(PreferenceService.SortType.SKILLS)
            when (sort) {
                SortSkills.NAME.value -> skills.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.name }
                    else sortByDescending { it.name }
                }
                SortSkills.XP.value -> skills.apply {
                    if (order == SortOrder.ASC.value) sortBy { it.xp }
                    else sortByDescending { it.xp }
                }
                SortSkills.CATEGORY.value -> skills.apply {
                    if (order == SortOrder.ASC.value) {
                        sortBy {
                            if (it.categoryId == null) ""
                            else categoryRepository.get(it.categoryId)?.name
                        }
                    } else {
                        sortByDescending {
                            if (it.categoryId == null) ""
                            else categoryRepository.get(it.categoryId)?.name
                        }
                    }
                }
            }
            updateSortedByHeader(context, skills)
        }

        fun updateSortedByHeader(context: Context, skills: MutableList<Skill>) {
            val prefService = PreferenceService(context)
            val sort = prefService.getSort(PreferenceService.SortType.SKILLS)
            val order = prefService.getSortOrder(PreferenceService.SortType.SKILLS)
            val sortString = when (sort) {
                SortSkills.NAME.value -> context.getString(R.string.term_sort_name)
                SortSkills.XP.value -> context.getString(R.string.term_sort_xp)
                SortSkills.CATEGORY.value -> context.getString(R.string.term_sort_category)
                else -> context.getString(R.string.term_sort_name)
            }
            val orderString =
                if (order == SortOrder.ASC.value) context.getString(R.string.term_sort_asc)
                else context.getString(R.string.term_sort_desc)
            if (skills.isNotEmpty()) {
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
        toolbar.title = getString(R.string.heading_skills)
        toggleBottomMenu(true, View.VISIBLE)
        val foundItem = MainActivity.bottomNav.menu.findItem(R.id.nav_skills)
        if (foundItem != null) foundItem.isChecked = true
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_skill_list, container, false)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryRepository = CategoryRepository(requireContext())
        skillAdapter = SkillAdapter(requireContext())
        skillAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
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
                val visibility = if (skillAdapter.itemCount == 0) View.VISIBLE else View.GONE
                binding.emptyRv.visibility = visibility
            }
        })
        val db = AppDatabase(requireContext())
        val skills = db.skillDao().findAll()
        sortSkills(requireContext(), skills)
        skillAdapter.setSkills(skills)
        binding.rv.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = skillAdapter
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.skill_sort_menu, menu)
        val prefService = PreferenceService(requireContext())
        val menuItemId =
            when (prefService.getSort(PreferenceService.SortType.SKILLS)) {
                SortSkills.NAME.value -> R.id.sort_name
                SortSkills.XP.value -> R.id.sort_xp
                SortSkills.CATEGORY.value -> R.id.sort_category
                else -> R.id.sort_name
            }
        val sortItem = menu.findItem(menuItemId)
        sortItem.isChecked = true
        val sortOrderItem = menu.findItem(R.id.sort_skills_order)
        val order = prefService.getSortOrder(PreferenceService.SortType.SKILLS)
        if (order == SortOrder.ASC.value) {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_asc_24)
        } else {
            sortOrderItem.setIcon(R.drawable.ic_sort_order_desc_24)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefService = PreferenceService(requireContext())
        if (item.itemId == R.id.sort_skills_order) {
            val savedOrder = prefService.getSortOrder(PreferenceService.SortType.SKILLS)
            val newOrder = if (savedOrder == SortOrder.ASC.value) {
                item.setIcon(R.drawable.ic_sort_order_desc_24)
                SortOrder.DESC.value
            } else {
                item.setIcon(R.drawable.ic_sort_order_asc_24)
                SortOrder.ASC.value
            }
            prefService.setSortOrder(newOrder, PreferenceService.SortType.SKILLS)
            sortSkills(requireContext(), skillAdapter.skills)
            skillAdapter.notifyDataSetChanged()
        } else {
            item.isChecked = true
            var sort = prefService.getSort(PreferenceService.SortType.SKILLS)
            when (item.itemId) {
                R.id.sort_name -> sort = SortSkills.NAME.value
                R.id.sort_xp -> sort = SortSkills.XP.value
                R.id.sort_category -> sort = SortSkills.CATEGORY.value
                else -> super.onOptionsItemSelected(item)
            }
            prefService.setSort(sort, PreferenceService.SortType.SKILLS)
            sortSkills(requireContext(), skillAdapter.skills)
            skillAdapter.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }
}