package com.vmaier.taski.features.skills

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.vmaier.taski.App
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.utils.Utils
import com.vmaier.taski.hideKeyboard
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 02/03/2020
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
            IconDrawableLoader(context).loadDrawable(icon)
            icon.drawable?.clearColorFilter()
            button.background = icon.drawable
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
        MainActivity.toolbar.title = getString(R.string.heading_skills)
        MainActivity.fab.hide()
        MainActivity.bottomNav.visibility = View.GONE
        MainActivity.bottomBar.visibility = View.GONE
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
        icon?.clearColorFilter()
        button.background = icon
        button.tag = iconId
    }

    fun setDeleteButtonOnClickListener(view: Button, position: Int, skill: Skill) {
        view.setOnClickListener {
            val countTasks = db.skillDao().countTasksWithSkillByStatus(skill.id)
            if (countTasks > 0) {
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder
                    .setTitle(getString(R.string.alert_skill_delete))
                    .setMessage(
                        resources.getQuantityString(
                            R.plurals.alert_assigned_task, countTasks, countTasks, skill.name
                        )
                    )
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.action_proceed_with_delete)) { _, _ ->
                        deleteSkill(it, position)
                    }
                    .setNegativeButton(getString(R.string.action_cancel)) { dialog, _ ->
                        dialog.cancel()
                    }
                dialogBuilder.create().show()
            } else {
                deleteSkill(it, position)
            }
        }
    }

    private fun deleteSkill(view: View, position: Int) {
        val toRestore = SkillListFragment.skillAdapter.removeItem(position)
        val snackbar =
            Snackbar.make(view, getString(R.string.event_skill_deleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo)) {
                    // undo is selected, restore the deleted item
                    SkillListFragment.skillAdapter.restoreItem(toRestore, position)
                }
                .setActionTextColor(Utils.getThemeColor(requireContext(), R.attr.colorSecondary))
        snackbar.view.setOnClickListener { snackbar.dismiss() }
        snackbar.show()
        view.findNavController().popBackStack()
        view.hideKeyboard()
    }
}