package org.vmaier.tidfl.features.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_create_task.*
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Status
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

        binding.createTaskButton.setOnClickListener {
            createTaskButtonClicked(it)
            it.findNavController().navigate(
                CreateTaskFragmentDirections.actionCreateTaskFragmentToTaskListFragment())
            // TODO: disable keyboard
        }

        return binding.root;
    }

    fun createTaskButtonClicked(view : View) {

        val dbHandler = DatabaseHandler(this.context!!)
        val goal = goal.text.toString()
        val details = details.text.toString()
        val duration = duration_value.selectedItem.toString().toInt()
        val finalDuration = when (duration_unit.selectedItem.toString()) {
            "minutes" -> duration
            "hours" -> duration * 60
            "days" -> duration * 60 * 24
            else -> {
                30
            }
        }
        val difficulty = when (difficulty.selectedItem.toString()) {
            "trivial" -> Difficulty.TRIVIAL
            "regular" -> Difficulty.REGULAR
            "hard" -> Difficulty.HARD
            "insane" -> Difficulty.INSANE
            else -> {
                Difficulty.REGULAR
            }
        }
        val iconId : Int = if (select_icon_button.tag == null) 115 else Integer.parseInt(select_icon_button.tag.toString())
        dbHandler.addTask(goal, details, Status.OPEN, finalDuration, difficulty, iconId)
    }
}