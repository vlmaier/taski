package org.vmaier.tidfl.features.skills

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.entity.AssignedSkill
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.utils.setIcon


/**
 * Created by Vladas Maier
 * on 25/02/2020
 * at 19:27
 */
class SkillAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var skills: MutableList<Skill> = mutableListOf()

    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView: TextView = itemView.findViewById(R.id.skill_name)
        var categoryView: TextView = itemView.findViewById(R.id.skill_category)
        var levelView: TextView = itemView.findViewById(R.id.skill_level)
        var iconView: ImageView = itemView.findViewById(R.id.skill_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val itemView = inflater.inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {

        val db = AppDatabase(context)
        val skill: Skill = skills[position]

        // --- Name settings
        holder.nameView.text = skill.name
        holder.nameView.isSelected = true

        // --- Category settings
        val categoryName = if (skill.categoryId == null) {
            ""
        } else {
            db.categoryDao().findNameById(skill.categoryId)
        }
        holder.categoryView.text = categoryName
        holder.categoryView.isSelected = true

        // --- Icon settings
        holder.iconView.setIcon(skill.iconId)

        // --- Level settings
        val xpValue = db.skillDao().countSkillXpValue(skill.id)
        val levelValue = xpValue.div(1000) + 1
        holder.levelView.text = context.getString(R.string.term_level_value, levelValue)

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(
                SkillListFragmentDirections
                    .actionSkillListFragmentToSkillEditFragment(skill, position)
            )
        }
    }

    internal fun setSkills(skills: List<Skill>) {
        this.skills = skills.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = skills.size

    fun removeItem(position: Int): Pair<Skill, List<AssignedSkill>> {
        val skill = skills.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, skills.size)
        val db = AppDatabase(this.inflater.context)
        val foundAssignments = db.skillDao().findAssignments(skill.id)
        db.skillDao().delete(skill)
        return Pair(skill, foundAssignments)
    }

    fun restoreItem(toRestore: Pair<Skill, List<AssignedSkill>>, position: Int) {
        skills.add(position, toRestore.first)
        notifyItemInserted(position)
        val db = AppDatabase(this.inflater.context)
        db.skillDao().create(toRestore.first)
        toRestore.second.forEach {
            db.taskDao().assignSkill(it)
        }
    }
}