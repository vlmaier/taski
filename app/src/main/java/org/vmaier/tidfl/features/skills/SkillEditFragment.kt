package org.vmaier.tidfl.features.skills

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.databinding.FragmentEditSkillBinding
import org.vmaier.tidfl.util.KeyBoardHider
import org.vmaier.tidfl.util.hideKeyboard


/**
 * Created by Vladas Maier
 * on 03/03/2020.
 * at 17:38
 */
class SkillEditFragment : SkillFragment() {

    var itemPosition: Int = 0

    companion object {

        lateinit var binding: FragmentEditSkillBinding
        lateinit var skill: Skill

        fun setIcon(context: Context, icon: Icon) {

            val drawable = IconDrawableLoader(context).loadDrawable(icon)!!
            drawable.clearColorFilter()
            DrawableCompat.setTint(
                    drawable, ContextCompat.getColor(
                    context, R.color.colorSecondary
            )
            )
            binding.editIconButton.background = drawable
            binding.editIconButton.tag = icon.id
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?)
            : View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_skill, container, false
        )

        val args = SkillEditFragmentArgs.fromBundle(this.arguments!!)
        skill = args.skill
        itemPosition = args.itemPosition
        binding.name.setText(if (saved != null) saved.getString(KEY_NAME) else skill.name)
        binding.category.setText(if (saved != null) saved.getString(KEY_CATEGORY) else skill.category)
        val db = AppDatabase(requireContext())
        val openTasksAmount = db.skillDao().findAmountOfTasks(skill.name, Status.OPEN)
        val doneTasksAmount = db.skillDao().findAmountOfTasks(skill.name, Status.DONE)
        binding.skillLevelValue.text = skill.level.toString()
        binding.skillXpValue.text = "${skill.xp} XP"
        binding.skillOpenTasksValue.text = "$openTasksAmount"
        binding.skillDoneTasksValue.text = "$doneTasksAmount"
        val iconId = if (saved != null) saved.getInt(KEY_ICON_ID) else skill.iconId
        binding.editIconButton.background = App.iconPack.getIconDrawable(
                iconId, IconDrawableLoader(this.context!!)
        )
        binding.editIconButton.tag = iconId

        binding.name.onFocusChangeListener = KeyBoardHider()
        binding.category.onFocusChangeListener = KeyBoardHider()

        binding.header.isFocusable = true

        binding.deleteButton.setOnClickListener {
            val removedSkill = SkillListFragment.skillAdapter.removeItem(itemPosition)
            Snackbar.make(
                    it,
                    "Skill deleted",
                    Snackbar.LENGTH_LONG
            ).setAction("UNDO") {
                // undo is selected, restore the deleted item
                SkillListFragment.skillAdapter.restoreItem(removedSkill, itemPosition)
            }.setActionTextColor(Color.YELLOW).show()
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        saveChangesOnSkill()
        binding.name.hideKeyboard()
        binding.category.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)

        out.putString(KEY_NAME, binding.name.text.toString())
        out.putString(KEY_CATEGORY, binding.category.text.toString())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.editIconButton.tag.toString()))

        saveChangesOnSkill()
    }

    private fun saveChangesOnSkill() {

        val name = binding.name.text.toString()
        val category = binding.category.text.toString()
        val iconId: Int = Integer.parseInt(binding.editIconButton.tag.toString())
        val isUpdateRequired = !(
                skill.name == name &&
                skill.category == category &&
                skill.iconId == iconId)
        if (isUpdateRequired) {
            val db = AppDatabase(requireContext())
            GlobalScope.launch {
                val toUpdate = Skill(
                    name = name,
                    category = category,
                    iconId = iconId
                )
                val updatedSkill = db.skillDao().updateSkill(skill.name, toUpdate)
                SkillListFragment.skillAdapter.skills.set(itemPosition, updatedSkill)
                SkillListFragment.skillAdapter.notifyItemChanged(itemPosition)
            }
            Toast.makeText(
                context, "Skill updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}