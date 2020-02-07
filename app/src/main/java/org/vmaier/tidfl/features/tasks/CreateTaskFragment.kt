package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.databinding.FragmentCreateTaskBinding
import org.vmaier.tidfl.hideKeyboard
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:16
 */
class CreateTaskFragment : Fragment() {

    companion object {

        lateinit var fragmentContext: Context
        lateinit var binding: FragmentCreateTaskBinding

        fun setIcon(context: Context, icon: Icon) {

            val drawable = IconDrawableLoader(context).loadDrawable(icon)!!
            drawable.clearColorFilter()
            DrawableCompat.setTint(drawable, ContextCompat.getColor(
                context, R.color.colorSecondary))
            binding.selectIconButton.background = drawable
            binding.selectIconButton.tag = icon.id
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {

        fragmentContext = this.context!!
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_task, container, false)

        val randomIconId = Random.nextInt(App.iconPack.allIcons.size)
        val randomIconDrawable = App.iconPack.getIconDrawable(
            randomIconId, IconDrawableLoader(fragmentContext)
        )!!

        DrawableCompat.setTint(randomIconDrawable, ContextCompat.getColor(
            fragmentContext, R.color.colorSecondary))

        binding.selectIconButton.background = randomIconDrawable
        binding.selectIconButton.tag = randomIconId

        binding.createTaskButton.setOnClickListener {
            createTaskButtonClicked(it)
            it.findNavController().navigate(
                CreateTaskFragmentDirections.actionCreateTaskFragmentToTaskListFragment())
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }
        return binding.root
    }

    private fun createTaskButtonClicked(view : View) {

        val dbHandler = DatabaseHandler(fragmentContext)
        val goal = binding.goal.text.toString()
        val details = binding.details.text.toString()
        val duration = binding.durationValue.selectedItem.toString().toInt()
        val finalDuration = when (binding.durationUnit.selectedItem.toString()) {
            "minutes" -> duration
            "hours" -> duration * 60
            "days" -> duration * 60 * 24
            else -> {
                30
            }
        }
        val difficulty = when (binding.difficulty.selectedItem.toString()) {
            "trivial" -> Difficulty.TRIVIAL
            "regular" -> Difficulty.REGULAR
            "hard" -> Difficulty.HARD
            "insane" -> Difficulty.INSANE
            else -> {
                Difficulty.REGULAR
            }
        }
        val iconId : Int = Integer.parseInt(binding.selectIconButton.tag.toString())
        dbHandler.addTask(goal, details, Status.OPEN, finalDuration, difficulty, iconId)
    }
}