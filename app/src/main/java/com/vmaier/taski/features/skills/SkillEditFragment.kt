package com.vmaier.taski.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.DataBindingUtil
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.databinding.FragmentEditSkillBinding
import com.vmaier.taski.utils.KeyBoardHider
import com.vmaier.taski.hideKeyboard
import com.vmaier.taski.toast
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 03/03/2020
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
        binding.name.editText?.setText(
            saved?.getString(
                KEY_NAME
            ) ?: skill.name
        )
        binding.name.onFocusChangeListener = KeyBoardHider()

        // --- Category settings
        val categoryId = skill.categoryId
        val categoryName =
            if (categoryId != null) db.categoryDao().findNameById(categoryId) else null
        binding.category.editText?.setText(
            saved?.getString(
                KEY_CATEGORY
            ) ?: categoryName
        )
        binding.category.onFocusChangeListener = KeyBoardHider()
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item,
            categoryNames
        )
        val autoCompleteCategory = binding.category.editText as AppCompatAutoCompleteTextView
        autoCompleteCategory.setAdapter(adapter)

        // --- Open tasks settings
        val openTasksAmount = db.skillDao().countTasksWithSkillByStatus(
            skill.id, Status.OPEN
        )
        binding.skillOpenTasksValue.text = "$openTasksAmount"

        // --- Done tasks settings
        val doneTasksAmount = db.skillDao().countTasksWithSkillByStatus(
            skill.id, Status.DONE
        )
        binding.skillDoneTasksValue.text = "$doneTasksAmount"

        // --- XP settings
        val xpValue = db.skillDao().countSkillXpValue(
            skill.id
        )
        binding.skillXpValue.text = getString(R.string.term_xp_value, xpValue)

        // --- Level settings
        val level = xpValue.div(1000) + 1
        binding.skillLevelValue.text = level.toString()

        // --- Icon settings
        setSkillIcon(saved, binding.iconButton, skill.iconId)

        // --- Action buttons settings
        setDeleteButtonOnClickListener(
            binding.deleteSkillButton, itemPosition,
            skill
        )

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        // check if the skill was not deleted
        if (db.skillDao().findById(
                skill.id
            ) != null
        ) {
            saveChangesOnSkill()
        }
        binding.name.hideKeyboard()
        binding.category.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putString(KEY_NAME, binding.name.editText?.text.toString())
        out.putString(KEY_CATEGORY, binding.category.editText?.text.toString())
        out.putInt(
            KEY_ICON_ID, Integer.parseInt(
                binding.iconButton.tag.toString()
            )
        )
        saveChangesOnSkill()
    }

    private fun saveChangesOnSkill() {

        val name = binding.name.editText?.text.toString().trim()
        if (name.isBlank()) {
            binding.name.requestFocus()
            binding.name.error = getString(R.string.error_cannot_be_empty)
            return
        }
        if (name.length < 4) {
            binding.name.requestFocus()
            binding.name.error = getString(R.string.error_too_short)
            return
        }
        val categoryName = binding.category.editText?.text.toString().trim()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val db = AppDatabase(requireContext())
        val categoryId: Long?
        if (categoryName.isNotBlank()) {
            val foundCategory = db.categoryDao().findByName(categoryName)
            if (foundCategory != null) {
                categoryId = foundCategory.id
            } else {
                categoryId = db.categoryDao().create(Category(name = categoryName))
                Timber.d("Created new category. ID: $categoryId returned.")
            }
        } else {
            categoryId = null
        }
        val toUpdate = Skill(
            id = skill.id, name = name, categoryId = categoryId, iconId = iconId
        )
        if (skill != toUpdate) {
            db.skillDao().update(toUpdate)
            Timber.d("Updated skill with ID: ${skill.id}.")
            SkillListFragment.skillAdapter.skills[itemPosition] = toUpdate
            SkillListFragment.skillAdapter.notifyItemChanged(itemPosition)
            getString(R.string.event_skill_updated).toast(requireContext())
        }
    }
}