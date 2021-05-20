package com.vmaier.taski.features.skills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maltaisn.recurpicker.RecurrenceFinder
import com.maltaisn.recurpicker.format.RRuleFormatter
import com.vmaier.taski.Const
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.databinding.FragmentEditSkillBinding
import com.vmaier.taski.hideKeyboard
import com.vmaier.taski.services.LevelService
import com.vmaier.taski.utils.KeyBoardHider
import com.vmaier.taski.utils.Utils


/**
 * Created by Vladas Maier
 * on 03.03.2020
 * at 17:38
 */
class SkillEditFragment : SkillFragment() {

    private var itemPosition: Int = 0
    private var isCanceled = false
    private lateinit var levelService: LevelService

    companion object {
        lateinit var binding: FragmentEditSkillBinding
        lateinit var taskAdapter: AssignedTaskAdapter
        lateinit var skill: Skill
        fun isTaskAdapterInitialized() = ::taskAdapter.isInitialized
        fun isBindingInitialized() = ::binding.isInitialized
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        levelService = LevelService(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_skill, container, false)

        // Focus header, so it's not one of the edit text views
        binding.header.isFocusable = true

        // Get arguments from bundle
        val args = SkillEditFragmentArgs.fromBundle(this.requireArguments())
        skill = args.skill
        itemPosition = args.itemPosition

        // Name settings
        binding.name.editText?.setText(saved?.getString(KEY_NAME) ?: skill.name)
        binding.name.onFocusChangeListener = KeyBoardHider()

        // Category settings
        val categoryId = skill.categoryId
        val categoryName =
            if (categoryId != null) categoryRepository.getNameById(categoryId) else null
        binding.category.editText?.setText(saved?.getString(KEY_CATEGORY) ?: categoryName)
        binding.category.onFocusChangeListener = KeyBoardHider()
        val arrayAdapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item,
            categoryNames
        )
        val autoCompleteCategory = binding.category.editText as AppCompatAutoCompleteTextView
        autoCompleteCategory.setAdapter(arrayAdapter)

        // "Done tasks" settings
        val doneTasksAmount = skillRepository.countDoneTasksBySkillId(skill.id)
        binding.skillDoneTasksValue.text = "$doneTasksAmount"

        // "Skill hours" settings
        val skillHoursAmount = skillRepository.countMinutes(skill.id).div(60)
        binding.skillHoursValue.text = "$skillHoursAmount"

        // XP settings
        binding.skillXp.text = getString(R.string.term_xp_value, skill.xp)

        // Level settings
        val skillLevel = levelService.getSkillLevel(skill)
        binding.skillLevel.text = skillLevel.toString()

        // Icon settings
        setSkillIcon(saved, binding.iconButton, skill.iconId)

        // Action buttons settings
        setDeleteButtonOnClickListener(binding.deleteSkillButton, itemPosition, skill)
        binding.iconButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            MainActivity.iconDialog.show(fragmentManager, Const.Tags.ICON_DIALOG_TAG)
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
            isCanceled = true
        }
        val tasks = skillRepository.getTasksWithSkillByStatus(skill.id, Status.OPEN)
        tasks.removeAll {
            if (it.rrule != null) {
                val found = RecurrenceFinder().findBasedOn(
                    RRuleFormatter().parse(it.rrule),
                    it.createdAt,
                    it.closedAt ?: it.createdAt,
                    it.countDone,
                    1,
                    it.closedAt ?: it.createdAt,
                    false
                )
                found.size == 0 || (it.closedAt != null && found[0] > Utils.getEndOfDay())
            } else {
                false
            }
        }
        tasks.sortBy { it.goal }
        taskAdapter = AssignedTaskAdapter(requireContext())
        taskAdapter.setTasks(tasks)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }

        // "Open tasks" settings
        binding.skillOpenTasksText.visibility = if (tasks.size > 0) View.VISIBLE else View.GONE
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        // check if the skill was not deleted
        if (skillRepository.get(skill.id) != null && !isCanceled) {
            saveChangesOnSkill()
        }
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
        saveChangesOnSkill()
    }

    private fun saveChangesOnSkill() {
        val context = requireContext()
        val name = binding.name.editText?.text.toString().trim()
        if (name.isBlank()) {
            binding.name.requestFocus()
            binding.name.error = getString(R.string.error_cannot_be_empty)
            return
        }
        if (name.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
            binding.name.requestFocus()
            binding.name.error =
                getString(R.string.error_too_short, Const.Defaults.MINIMAL_INPUT_LENGTH)
            return
        }
        if (skill.name != name) {
            skillRepository.updateName(context, skill.id, name)
        }
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        if (skill.iconId != iconId) {
            skillRepository.updateIconId(context, skill.id, iconId)
        }
        val categoryName = binding.category.editText?.text.toString().trim()
        if (categoryName.isNotBlank()) {
            if (categoryName.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
                binding.category.requestFocus()
                binding.category.error =
                    getString(R.string.error_too_short, Const.Defaults.MINIMAL_INPUT_LENGTH)
                return
            } else {
                val foundCategory = categoryRepository.get(categoryName)
                if (foundCategory != null) {
                    if (foundCategory.id != skill.categoryId) {
                        skillRepository.updateCategoryId(context, skill.id, foundCategory.id)
                    }
                } else {
                    categoryRepository.create(context, categoryName, null, skill.id)
                }
            }
        } else {
            if (skill.categoryId != null) {
                skillRepository.updateCategoryId(context, skill.id, null)
            }
        }
    }
}