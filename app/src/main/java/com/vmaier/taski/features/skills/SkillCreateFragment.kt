package com.vmaier.taski.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.vmaier.taski.Const
import com.vmaier.taski.MainActivity.Companion.iconDialog
import com.vmaier.taski.R
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.databinding.FragmentCreateSkillBinding
import com.vmaier.taski.features.skills.SkillListFragment.Companion.skillAdapter
import com.vmaier.taski.hideKeyboard
import com.vmaier.taski.utils.KeyBoardHider
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 02.03.2020
 * at 20:02
 */
class SkillCreateFragment : SkillFragment() {

    companion object {
        lateinit var binding: FragmentCreateSkillBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_skill, container, false)

        // Name settings
        binding.name.editText?.setText(saved?.getString(KEY_NAME) ?: "")
        binding.name.onFocusChangeListener = KeyBoardHider()

        // Category settings
        binding.category.editText?.setText(saved?.getString(KEY_CATEGORY) ?: "")
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item,
            categoryNames
        )
        val autoCompleteCategory = binding.category.editText as AppCompatAutoCompleteTextView
        autoCompleteCategory.setAdapter(adapter)
        binding.category.onFocusChangeListener = KeyBoardHider()

        // Icon settings
        setSkillIcon(saved, binding.iconButton)

        // Action buttons settings
        binding.createSkillButton.setOnClickListener {
            if (createSkillButtonClicked()) {
                it.findNavController().popBackStack()
                it.hideKeyboard()
            }
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }
        binding.iconButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            iconDialog.show(fragmentManager, Const.Tags.ICON_DIALOG_TAG)
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.name.hideKeyboard()
        binding.category.hideKeyboard()
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putString(KEY_NAME, binding.name.editText?.text.toString())
        out.putString(KEY_CATEGORY, binding.category.editText?.text.toString())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
    }

    private fun createSkillButtonClicked(): Boolean {
        val name = binding.name.editText?.text.toString().trim()
        if (name.isBlank()) {
            binding.name.requestFocus()
            binding.name.error = getString(R.string.error_cannot_be_empty)
            return false
        } else {
            if (name.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
                binding.name.requestFocus()
                binding.name.error = getString(R.string.error_too_short)
                return false
            }
            val foundSkill = db.skillDao().findByName(name)
            if (foundSkill != null) {
                binding.name.requestFocus()
                binding.name.error = getString(R.string.error_skill_already_exists)
                return false
            }
        }
        val categoryName = binding.category.editText?.text.toString().trim()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        var categoryId: Long? = null
        if (categoryName.isNotBlank()) {
            if (categoryName.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
                binding.category.requestFocus()
                binding.category.error = getString(R.string.error_too_short)
                return false
            } else {
                val foundCategory = db.categoryDao().findByName(categoryName)
                if (foundCategory != null) {
                    categoryId = foundCategory.id
                } else {
                    categoryId = db.categoryDao().create(Category(name = categoryName))
                    Timber.d("Category ($categoryId) created.")
                }
            }
        }
        val skill = Skill(name = name, categoryId = categoryId, iconId = iconId)
        val id = db.skillDao().create(skill)
        Timber.d("Skill ($id) created.")
        skillAdapter.notifyDataSetChanged()
        return true
    }
}