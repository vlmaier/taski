package org.vmaier.tidfl.features.skills

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.entity.Category
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.databinding.FragmentCreateSkillBinding
import org.vmaier.tidfl.util.KeyBoardHider
import org.vmaier.tidfl.util.hideKeyboard
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 02/03/2020.
 * at 20:02
 */
class SkillCreateFragment : SkillFragment() {

    companion object {
        lateinit var binding: FragmentCreateSkillBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_skill, container, false
        )

        // --- Name settings
        binding.name.setText(saved?.getString(KEY_NAME) ?: "")
        binding.name.onFocusChangeListener = KeyBoardHider()

        // --- Category settings
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item, categoryNames
        )
        binding.category.setAdapter(adapter)
        binding.category.onFocusChangeListener = KeyBoardHider()
        binding.category.setText(saved?.getString(KEY_CATEGORY) ?: "")

        // -- Icon settings
        setSkillIcon(saved, binding.iconButton)

        // --- Action buttons settings
        binding.createSkillButton.setOnClickListener {
            createSkillButtonClicked(it)
            it.findNavController().popBackStack()
            it.hideKeyboard()
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

        out.putString(KEY_NAME, binding.name.text.toString())
        out.putString(KEY_CATEGORY, binding.category.text.toString())
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.iconButton.tag.toString()))
    }

    private fun createSkillButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {

        val name = binding.name.text.toString()
        val categoryName = binding.category.text.toString()
        val iconId: Int = Integer.parseInt(binding.iconButton.tag.toString())
        val db = AppDatabase(requireContext())
        var categoryId: Long? = null
        if (categoryName.isNotBlank()) {
            val foundCategory = db.categoryDao().findByName(categoryName)
            categoryId = foundCategory?.id ?: db.categoryDao().create(Category(name = categoryName))
        }
        val skill = Skill(name = name, categoryId = categoryId, iconId = iconId)
        db.skillDao().create(skill)
        SkillListFragment.skillAdapter.notifyDataSetChanged()
    }
}