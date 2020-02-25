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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.data.entity.Category
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.databinding.FragmentSkillListBinding
import org.vmaier.tidfl.databinding.FragmentTaskListBinding
import org.vmaier.tidfl.features.tasks.SwipeCallbackHandler
import org.vmaier.tidfl.features.tasks.TaskAdapter
import org.vmaier.tidfl.features.tasks.TaskListFragment
import org.vmaier.tidfl.features.tasks.TaskListFragmentDirections


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

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val skills = mutableListOf(
            Skill(
                1, "Reading", Category(1, "Intellect"), 116
            ),
            Skill(
                2, "Programming", Category(1, "Intellect"), 896
            )
        )

        skillAdapter = SkillAdapter(skills, mContext)
        rv.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = skillAdapter
        }
    }
}