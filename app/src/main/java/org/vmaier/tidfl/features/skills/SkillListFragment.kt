package org.vmaier.tidfl.features.skills

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.databinding.FragmentSkillListBinding


/**
 * Created by Vladas Maier
 * on 25/02/2020.
 * at 18:01
 */
class SkillListFragment : Fragment() {

    companion object {
        lateinit var mContext: Context
        lateinit var skillAdapter: SkillAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentSkillListBinding>(
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

        val dbHandler = DatabaseHandler(mContext)
        val skills = dbHandler.findAllSkills()

        skillAdapter = SkillAdapter(skills, mContext)
        rv.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = skillAdapter
        }
    }
}