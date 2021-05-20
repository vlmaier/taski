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
import com.vmaier.taski.databinding.FragmentCreateSkillBinding
import com.vmaier.taski.features.skills.SkillListFragment.Companion.skillAdapter
import com.vmaier.taski.hideKeyboard
import com.vmaier.taski.lifecycleOwner
import com.vmaier.taski.utils.KeyBoardHider


/**
 * Created by Vladas Maier
 * on 02.03.2020
 * at 20:02
 */
class SkillCreateFragment : SkillFragment() {

    companion object {
        lateinit var binding: FragmentCreateSkillBinding
        fun isBindingInitialized() = ::binding.isInitialized
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
        if (isBindingInitialized()) {
            out.putString(KEY_NAME, binding.name.editText?.text.toString())
            out.putString(KEY_CATEGORY, binding.category.editText?.text.toString())
            out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
        }
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
                binding.name.error = getString(R.string.error_too_short, Const.Defaults.MINIMAL_INPUT_LENGTH)
                return false
            }
            val foundSkill = skillRepository.get(name)
            if (foundSkill != null) {
                binding.name.requestFocus()
                binding.name.error = getString(R.string.error_skill_already_exists)
                return false
            }
        }
        val categoryName = binding.category.editText?.text.toString().trim()
        if (categoryName.isNotBlank() && categoryName.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
            binding.category.requestFocus()
            binding.category.error = getString(R.string.error_too_short, Const.Defaults.MINIMAL_INPUT_LENGTH)
            return false
        }
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val skillId = skillRepository.create(name, iconId, null)
        val lifecycleOwner = requireContext().lifecycleOwner()
        if (lifecycleOwner != null) {
            skillId.observe(lifecycleOwner, { id ->
                if (id != null && categoryName.isNotBlank()) {
                    val foundCategory = categoryRepository.get(categoryName)
                    if (foundCategory != null) {
                        skillRepository.updateCategoryId(requireContext(), id, foundCategory.id, false)
                    } else {
                        categoryRepository.create(requireContext(), categoryName, null, id)
                    }
                }
            })
        }
        skillAdapter.notifyDataSetChanged()
        return true
    }
}