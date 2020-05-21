package org.vmaier.tidfl.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.databinding.FragmentSkillListBinding


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
        MainActivity.bottomNav.visibility = View.VISIBLE
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_skill_list, container, false
        )
        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        binding.fab.setOnClickListener {
            it.findNavController().navigate(
                SkillListFragmentDirections.actionSkillListFragmentToCreateSkillFragment()
            )
        }
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
        skillAdapter.setSkills(skills)
        binding.rv.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = skillAdapter
        }
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !binding.fab.isShown) {
                    binding.fab.show()
                } else if (dy > 0 && binding.fab.isShown) {
                    binding.fab.hide()
                }
            }
        })
    }
}