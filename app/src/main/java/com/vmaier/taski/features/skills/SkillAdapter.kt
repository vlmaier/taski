package com.vmaier.taski.features.skills

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.Const
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.SortSkills
import com.vmaier.taski.data.entity.AssignedSkill
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.features.skills.SkillListFragment.Companion.updateSortedByHeader
import com.vmaier.taski.services.LevelService
import com.vmaier.taski.setIcon
import kotlinx.android.synthetic.main.item_skill.view.*
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 25.02.2020
 * at 19:27
 */
class SkillAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var skills: MutableList<Skill> = mutableListOf()
    var levelService = LevelService(context)
    val db = AppDatabase(context)

    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView: TextView = itemView.findViewById(R.id.skill_name)
        var categoryView: TextView = itemView.findViewById(R.id.skill_category)
        var levelView: TextView = itemView.findViewById(R.id.skill_level)
        var sortIndicatorView: TextView = itemView.findViewById(R.id.skill_sort_indicator)
        var iconView: ImageView = itemView.findViewById(R.id.skill_icon)
    }

    internal fun setSkills(skills: List<Skill>) {
        this.skills = skills.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val itemView = inflater.inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {

        val skill: Skill = skills[position]

        // setup "Name" view
        holder.nameView.text = skill.name
        holder.nameView.isSelected = true

        // setup "Category" view
        val category: Category? =
            if (skill.categoryId != null) db.categoryDao().findById(skill.categoryId) else null
        holder.categoryView.text = category?.name ?: ""
        holder.categoryView.isSelected = true
        if (category?.color != null) {
            holder.itemView.cv.strokeColor = Color.parseColor(category.color)
        } else {
            // set transparent
            holder.itemView.cv.strokeColor = 0x00000000
        }

        // setup "Icon" view
        holder.iconView.setIcon(skill.iconId)

        // setup "Level" view
        val skillLevel = levelService.getSkillLevel(skill)
        holder.levelView.text = context.getString(R.string.term_level_value, skillLevel)

        // setup "Sort indicator" view
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val sortPref = prefs.getString(Const.Prefs.SORT_SKILLS, Const.Defaults.SORT_SKILLS)
        holder.sortIndicatorView.text = when (sortPref) {
            SortSkills.XP.value -> {
                holder.sortIndicatorView.visibility = View.VISIBLE
                "(" + skill.xp + " XP)"
            }
            else -> {
                holder.sortIndicatorView.visibility = View.GONE
                ""
            }
        }

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(
                SkillListFragmentDirections
                    .actionSkillListFragmentToSkillEditFragment(skill, position)
            )
        }
    }

    override fun getItemCount(): Int = skills.size

    fun removeItem(position: Int): Pair<Skill, List<AssignedSkill>> {
        val skill = skills.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, skills.size)
        updateSortedByHeader(context, skills)
        val foundAssignments = db.skillDao().findAssignments(skill.id)
        db.skillDao().delete(skill)
        Timber.d("Skill (${skill.id}) removed.")
        return Pair(skill, foundAssignments)
    }

    fun restoreItem(toRestore: Pair<Skill, List<AssignedSkill>>, position: Int) {
        skills.add(position, toRestore.first)
        notifyItemInserted(position)
        updateSortedByHeader(context, skills)
        db.skillDao().create(toRestore.first)
        toRestore.second.forEach {
            db.taskDao().assignSkill(it)
        }
        Timber.d("Skill (${toRestore.first.id}) restored.")
    }
}