package org.vmaier.tidfl.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.entity.Category
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.databinding.FragmentCreateSkillBinding
import org.vmaier.tidfl.util.KeyBoardHider
import org.vmaier.tidfl.util.hideKeyboard


/**
 * Created by Vladas Maier
 * on 02/03/2020.
 * at 20:02
 */
class SkillCreateFragment : SkillFragment() {

    companion object {
        lateinit var binding: FragmentCreateSkillBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_skill, container, false
        )

        // --- Name settings
        binding.name.editText?.setText(saved?.getString(KEY_NAME) ?: "")
        binding.name.onFocusChangeListener = KeyBoardHider()

        // --- Category settings
        binding.category.editText?.setText(saved?.getString(KEY_CATEGORY) ?: "")
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item, categoryNames
        )
        val autoCompleteCategory = binding.category.editText as AppCompatAutoCompleteTextView
        autoCompleteCategory.setAdapter(adapter)
        binding.category.onFocusChangeListener = KeyBoardHider()

        // -- Icon settings
        setSkillIcon(saved, binding.iconButton)

        // --- Action buttons settings
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
        val name = binding.name.editText?.text.toString()
        if (name.isBlank()) {
            binding.name.requestFocus()
            binding.name.error = getString(R.string.error_name_cannot_be_empty)
            return false
        }
        val categoryName = binding.category.editText?.text.toString()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        var categoryId: Long? = null
        if (categoryName.isNotBlank()) {
            val foundCategory = db.categoryDao().findByName(categoryName)
            categoryId = foundCategory?.id ?: db.categoryDao().create(Category(name = categoryName))
        }
        val skill = Skill(name = name, categoryId = categoryId, iconId = iconId)
        db.skillDao().create(skill)
        SkillListFragment.skillAdapter.notifyDataSetChanged()
        return true
    }
}