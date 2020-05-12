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
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Category
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

        val db = AppDatabase(requireContext())
        val args = SkillEditFragmentArgs.fromBundle(this.arguments!!)

        skill = args.skill
        itemPosition = args.itemPosition
        binding.name.setText(if (saved != null) saved.getString(KEY_NAME) else skill.name)
        val categoryId = skill.categoryId
        val categoryName = if (categoryId != null) {
            db.categoryDao().findNameById(categoryId)
        } else {
            null
        }
        binding.category.setText(if (saved != null) saved.getString(KEY_CATEGORY) else categoryName)
        val openTasksAmount = db.skillDao().countTasksWithSkillByStatus(skill.id, Status.OPEN)
        val doneTasksAmount = db.skillDao().countTasksWithSkillByStatus(skill.id, Status.DONE)
        val xp = db.skillDao().countSkillXpValue(skill.id)
        val level = xp.div(1000) + 1
        binding.skillLevelValue.text = level.toString()
        binding.skillXpValue.text = "$xp XP"
        binding.skillOpenTasksValue.text = "$openTasksAmount"
        binding.skillDoneTasksValue.text = "$doneTasksAmount"
        val iconId = saved?.getInt(KEY_ICON_ID) ?: skill.iconId
        binding.editIconButton.background = App.iconPack.getIconDrawable(
                iconId, IconDrawableLoader(this.context!!)
        )
        binding.editIconButton.tag = iconId

        binding.name.onFocusChangeListener = KeyBoardHider()
        binding.category.onFocusChangeListener = KeyBoardHider()

        binding.header.isFocusable = true

        binding.deleteSkillButton.setOnClickListener {
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
        val categoryName = binding.category.text.toString()
        val iconId: Int = Integer.parseInt(binding.editIconButton.tag.toString())
        val db = AppDatabase(requireContext())
        var categoryId: Long? = skill.categoryId
        if (categoryName.isNotBlank()) {
            val foundCategory = db.categoryDao().findByName(categoryName)
            if (foundCategory == null) {
                categoryId = db.categoryDao().create(Category(name = categoryName))
            }
        } else {
            categoryId = null
        }
        val toUpdate = Skill(
            id = skill.id, name = name, categoryId = categoryId, iconId = iconId
        )
        if (skill != toUpdate) {
            db.skillDao().update(toUpdate)
            SkillListFragment.skillAdapter.skills[itemPosition] = toUpdate
            SkillListFragment.skillAdapter.notifyItemChanged(itemPosition)
            Toast.makeText(
                context, "Skill updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}