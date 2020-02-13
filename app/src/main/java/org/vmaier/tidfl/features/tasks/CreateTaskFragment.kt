package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.MainActivity
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

    private val KEY_GOAL = "goal"
    private val KEY_DETAILS = "details"
    private val KEY_DIFFICULTY = "difficulty"
    private val KEY_DURATION_UNIT = "duration_unit"
    private val KEY_DURATION_VALUE = "duration_value"
    private val KEY_ICON_ID = "icon_id"

    companion object {

        lateinit var mContext: Context
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_task, container, false)

        MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val randomIconId = if (savedInstanceState != null)
            savedInstanceState.getInt(KEY_ICON_ID) else Random.nextInt(App.iconPack.allIcons.size)
        val randomIconDrawable = App.iconPack.getIconDrawable(
            randomIconId, IconDrawableLoader(mContext)
        )!!

        DrawableCompat.setTint(randomIconDrawable, ContextCompat.getColor(
            mContext, R.color.colorSecondary))

        binding.selectIconButton.background = randomIconDrawable
        binding.selectIconButton.tag = randomIconId

        binding.createTaskButton.setOnClickListener {
            createTaskButtonClicked(it)
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        binding.durationUnit.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent : AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (pos < 0) return
                var scrollTo = if (savedInstanceState != null)
                    savedInstanceState.getInt(KEY_DURATION_VALUE) else 0
                val resourceArrayId = when (binding.durationUnit.selectedItem.toString()) {
                    "minutes" -> {
                        scrollTo = 2
                        R.array.duration_minutes
                    }
                    "hours" -> R.array.duration_hours
                    "days" -> R.array.duration_days
                    else -> R.array.duration_minutes
                }
                val values = resources.getStringArray(resourceArrayId)
                val adapter : ArrayAdapter<String> = ArrayAdapter(mContext,
                    android.R.layout.simple_spinner_dropdown_item, values)
                binding.durationValue.adapter = adapter
                binding.durationValue.setSelection(scrollTo)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.difficulty.setSelection(1)

        binding.goal.onFocusChangeListener = KeyBoardHider()
        binding.details.onFocusChangeListener = KeyBoardHider()

        return binding.root
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)

        out.putString(KEY_GOAL, binding.goal.text.toString())
        out.putString(KEY_DETAILS, binding.goal.text.toString())
        out.putInt(KEY_DIFFICULTY, binding.difficulty.selectedItemPosition)
        out.putInt(KEY_DURATION_UNIT, binding.durationUnit.selectedItemPosition)
        out.putInt(KEY_DURATION_VALUE, binding.durationValue.selectedItemPosition)
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.selectIconButton.tag.toString()))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            binding.goal.setText(savedInstanceState.getString(KEY_GOAL, ""))
            binding.details.setText(savedInstanceState.getString(KEY_DETAILS, ""))
            binding.difficulty.setSelection(
                savedInstanceState.getInt(KEY_DIFFICULTY, 0))
            val unitPos = savedInstanceState.getInt(KEY_DURATION_UNIT, 0)
            binding.durationUnit.setSelection(unitPos)
            val resourceArrayId = when (unitPos) {
                0 -> R.array.duration_minutes
                1 -> R.array.duration_hours
                2 -> R.array.duration_days
                else -> R.array.duration_minutes
            }
            val values = resources.getStringArray(resourceArrayId)
            binding.durationValue.adapter = ArrayAdapter(mContext,
                android.R.layout.simple_spinner_dropdown_item, values)
        }
    }

    private fun createTaskButtonClicked(view : View) {

        val dbHandler = DatabaseHandler(mContext)
        val goal = binding.goal.text.toString()
        val details = binding.details.text.toString()
        val duration = binding.durationValue.selectedItem.toString().toInt()
        val finalDuration = when (binding.durationUnit.selectedItem.toString()) {
            "minutes" -> duration
            "hours" -> duration * 60
            "days" -> duration * 60 * 24
            else -> {
                15
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