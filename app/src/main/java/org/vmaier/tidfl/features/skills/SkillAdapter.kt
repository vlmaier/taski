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
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.util.setIcon


/**
 * Created by Vladas Maier
 * on 25/02/2020.
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
        var xpView: TextView = itemView.findViewById(R.id.skill_xp_gain)
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

        // --- Category settings
        val categoryName = if (skill.categoryId == null) {
            ""
        } else {
            db.categoryDao().findNameById(skill.categoryId)
        }
        holder.categoryView.text = categoryName

        // --- Icon settings
        holder.iconView.setIcon(skill.iconId)

        // --- XP value settings
        val xpValue = db.skillDao().countSkillXpValue(skill.id)
        holder.xpView.text = context.getString(R.string.term_xp_value, xpValue)

        // --- Level settings
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

    fun removeItem(position: Int): Skill {
        val skill = skills.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, skills.size)
        val db = AppDatabase(this.inflater.context)
        db.skillDao().delete(skill)
        return skill
    }

    fun restoreItem(skill: Skill, position: Int) {
        skills.add(position, skill)
        notifyItemInserted(position)
        val db = AppDatabase(this.inflater.context)
        db.skillDao().create(skill)
    }
}