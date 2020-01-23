package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_task.*
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Task
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