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

        fun setIcon(context: Context, icon: Icon) {

            val drawable = IconDrawableLoader(context).loadDrawable(icon)!!
            drawable.clearColorFilter()
            DrawableCompat.setTint(
                drawable, ContextCompat.getColor(
                    context, R.color.colorSecondary
                )
            )
            binding.selectIconButton.background = drawable
            binding.selectIconButton.tag = icon.id
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            saved: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_create_skill, container, false
        )

        val iconId = saved?.getInt(KEY_ICON_ID) ?: Random.nextInt(App.iconPack.allIcons.size)
        val iconDrawable = App.iconPack.getIconDrawable(
                iconId, IconDrawableLoader(requireContext())
        )!!

        DrawableCompat.setTint(
            iconDrawable, ContextCompat.getColor(
                requireContext(), R.color.colorSecondary
            )
        )

        binding.selectIconButton.background = iconDrawable
        binding.selectIconButton.tag = iconId

        binding.name.setText(saved?.getString(KEY_NAME) ?: "")
        binding.category.setText(saved?.getString(KEY_CATEGORY) ?: "")

        val db = AppDatabase(requireContext())
        val categories = db.skillDao().findAllCategories()

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, categories
        )
        binding.category.setAdapter(adapter)

        binding.createSkillButton.setOnClickListener {
            createSkillButtonClicked(it)
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        binding.cancelButton.setOnClickListener {
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }

        binding.name.onFocusChangeListener = KeyBoardHider()
        binding.category.onFocusChangeListener = KeyBoardHider()

        binding.name.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

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
        out.putInt(KEY_ICON_ID, Integer.parseInt(binding.selectIconButton.tag.toString()))
    }

    private fun createSkillButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {

        val name = binding.name.text.toString()
        val category = binding.category.text.toString()
        val iconId: Int = Integer.parseInt(binding.selectIconButton.tag.toString())
        val skill = Skill(
            name = name,
            category = category,
            iconId = iconId
        )
        val db = AppDatabase(requireContext())
        GlobalScope.launch {
            db.skillDao().insertSkill(skill)
            SkillListFragment.skillAdapter.notifyDataSetChanged()
        }
    }
}