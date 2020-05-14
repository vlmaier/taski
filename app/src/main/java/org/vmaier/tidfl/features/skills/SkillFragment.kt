package org.vmaier.tidfl.features.skills

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.util.hideKeyboard
import org.vmaier.tidfl.util.setThemeTint
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 02/03/2020.
 * at 20:48
 */
open class SkillFragment : Fragment() {

    companion object {
        lateinit var categoryNames: List<String>
        lateinit var db: AppDatabase

        const val KEY_NAME = "name"
        const val KEY_CATEGORY = "category"
        const val KEY_ICON_ID = "icon_id"

        fun setIcon(context: Context, icon: Icon, button: ImageButton) {
            val drawable = IconDrawableLoader(context).loadDrawable(icon)
            drawable.setThemeTint(context)
            button.background = drawable
            button.tag = icon.id
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = AppDatabase(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        val categories = db.categoryDao().findAll()
        categoryNames = categories.map { it.name }
        return this.view
    }

    fun setSkillIcon(
        saved: Bundle?, button: ImageButton,
        fallback: Int = Random.nextInt(App.iconPack.allIcons.size)
    ) {
        val iconId = saved?.getInt(KEY_ICON_ID) ?: fallback
        val icon = App.iconPack.getIconDrawable(iconId, IconDrawableLoader(requireContext()))
        icon.setThemeTint(requireContext())
        button.background = icon
        button.tag = iconId
    }

    fun setDeleteButtonOnClickListener(view: Button, position: Int) {
        view.setOnClickListener {
            val removedSkill = SkillListFragment.skillAdapter.removeItem(position)
            Snackbar.make(
                it,
                getString(R.string.event_skill_deleted),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.action_undo)) {
                // undo is selected, restore the deleted item
                SkillListFragment.skillAdapter.restoreItem(removedSkill, position)
            }.setActionTextColor(Color.YELLOW).show()
            it.findNavController().popBackStack()
            it.hideKeyboard()
        }
    }
}