package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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

        val binding = DataBindingUtil.inflate<FragmentTaskDetailsBinding>(
            inflater, R.layout.fragment_task_details, container, false)

        val args = TaskDetailsFragmentArgs.fromBundle(this.arguments!!)
        binding.goalTextView.text = args.goal
        binding.detailsTextView.text = args.details
        return binding.root;
    }
}