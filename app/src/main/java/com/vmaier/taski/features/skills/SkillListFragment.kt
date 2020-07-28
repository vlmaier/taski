package com.vmaier.taski.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.databinding.FragmentSkillListBinding
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 25/02/2020
 * at 18:01
 */
class SkillListFragment : Fragment() {

    companion object {
        lateinit var skillAdapter: SkillAdapter
        lateinit var binding: FragmentSkillListBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_skills)
        MainActivity.fab.show()
        MainActivity.bottomNav.visibility = View.VISIBLE
        MainActivity.bottomBar.visibility = View.VISIBLE
        val foundItem = MainActivity.bottomNav.menu.findItem(R.id.nav_skills)
        if (foundItem != null) {
            foundItem.isChecked = true
        }
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_skill_list, container, false
        )
        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        Timber.d("${skills.size} skill(s) found.")
        skills.sortBy { it.name }
        skillAdapter.setSkills(skills)
        binding.rv.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter =
                skillAdapter
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
    }
}