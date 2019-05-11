package org.vmaier.tidfl.features.list

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.vmaier.tidfl.R
import org.vmaier.tidfl.databinding.FragmentCreateTaskBinding


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:16
 */
class CreateTaskFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? {
        val binding = DataBindingUtil.inflate<FragmentCreateTaskBinding>(
            inflater, R.layout.fragment_create_task, container, false)
        return binding.root;
    }
}