package org.vmaier.tidfl.features.list

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.vmaier.tidfl.R
import org.vmaier.tidfl.databinding.FragmentTaskDetailsBinding


/**
 * Created by Vladas Maier
 * on 11.05.2019
 * at 12:44
 */
class TaskDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? {
        val binding = DataBindingUtil.inflate<FragmentTaskDetailsBinding>(
            inflater, R.layout.fragment_task_details, container, false)
        val args = TaskDetailsFragmentArgs.fromBundle(this.arguments!!)
        val displayString = "${args.goal} ${args.details}"
        binding.textViewTaskDetails.text = displayString
        return binding.root;
    }
}