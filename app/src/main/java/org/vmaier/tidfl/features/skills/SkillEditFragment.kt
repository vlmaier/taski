package org.vmaier.tidfl.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Category
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.databinding.FragmentEditSkillBinding
import org.vmaier.tidfl.util.KeyBoardHider
import org.vmaier.tidfl.util.hideKeyboard
import org.vmaier.tidfl.util.toast


/**
 * Created by Vladas Maier
 * on 03/03/2020.
 * at 17:38
 */
class SkillEditFragment : SkillFragment() {

    private var itemPosition: Int = 0

    companion object {
        lateinit var binding: FragmentEditSkillBinding
        lateinit var skill: Skill
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_skill, container, false
        )

        // Focus header, so it's not one of the edit texts
        binding.header.isFocusable = true

        // Get arguments from bundle
        val args = SkillEditFragmentArgs.fromBundle(this.requireArguments())
        skill = args.skill
        itemPosition = args.itemPosition

        // --- Name settings
        binding.name.setText(saved?.getString(KEY_NAME) ?: skill.name)
        binding.name.onFocusChangeListener = KeyBoardHider()

        // --- Category settings
        val categoryId = skill.categoryId
        val categoryName =
            if (categoryId != null) db.categoryDao().findNameById(categoryId) else null
        binding.category.setText(saved?.getString(KEY_CATEGORY) ?: categoryName)
        binding.category.onFocusChangeListener = KeyBoardHider()

        // --- Open tasks settings
        val openTasksAmount = db.skillDao().countTasksWithSkillByStatus(skill.id, Status.OPEN)
        binding.skillOpenTasksValue.text = "$openTasksAmount"

        // --- Done tasks settings
        val doneTasksAmount = db.skillDao().countTasksWithSkillByStatus(skill.id, Status.DONE)
        binding.skillDoneTasksValue.text = "$doneTasksAmount"

        // --- XP settings
        val xpValue = db.skillDao().countSkillXpValue(skill.id)
        binding.skillXpValue.text = getString(R.string.term_xp_value, xpValue)

        // --- Level settings
        val level = xpValue.div(1000) + 1
        binding.skillLevelValue.text = level.toString()

        // --- Icon settings
        setSkillIcon(saved, binding.iconButton, skill.iconId)

        // --- Action buttons settings
        setDeleteButtonOnClickListener(binding.deleteSkillButton, itemPosition)

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        // check if the skill was not deleted
        if (db.skillDao().findById(skill.id) != null) {
            saveChangesOnSkill()
        }
        binding.name.hideKeyboard()
        binding.category.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putString(KEY_NAME, binding.name.text.toString())
        out.putString(KEY_CATEGORY, binding.category.text.toString())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
        saveChangesOnSkill()
    }

    private fun saveChangesOnSkill() {
        val name = binding.name.text.toString()
        val categoryName = binding.category.text.toString()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val db = AppDatabase(requireContext())
        val categoryId: Long? = if (categoryName.isNotBlank()) {
            val foundCategory = db.categoryDao().findByName(categoryName)
            foundCategory?.id ?: db.categoryDao().create(Category(name = categoryName))
        } else {
            null
        }
        val toUpdate = Skill(
            id = skill.id, name = name, categoryId = categoryId, iconId = iconId
        )
        if (skill != toUpdate) {
            db.skillDao().update(toUpdate)
            SkillListFragment.skillAdapter.skills[itemPosition] = toUpdate
            SkillListFragment.skillAdapter.notifyItemChanged(itemPosition)
            getString(R.string.event_skill_updated).toast(requireContext())
        }
    }
}