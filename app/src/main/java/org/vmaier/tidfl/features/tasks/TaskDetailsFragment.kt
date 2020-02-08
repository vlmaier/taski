package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.databinding.FragmentTaskDetailsBinding


/**
 * Created by Vladas Maier
 * on 11.05.2019
 * at 12:44
 */
class TaskDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding =
            DataBindingUtil.inflate<FragmentTaskDetailsBinding>(inflater,
                R.layout.fragment_task_details, container, false)

        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val args = TaskDetailsFragmentArgs.fromBundle(this.arguments!!)
        binding.goalTextView.text = args.task.goal
        binding.detailsTextView.text = args.task.details
        return binding.root;
    }
}