package org.vmaier.tidfl.features.skills

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.data.entity.Task


/**
 * Created by Vladas Maier
 * on 25/02/2020.
 * at 19:27
 */
class SkillAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var skills: MutableList<Skill> = mutableListOf()

    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // text views
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
        val skill: Skill = skills[position]
        holder.nameView.text = skill.name
        holder.categoryView.text = "${skill.category}"

        // skill icon
        val drawable: Drawable? = App.iconPack.getIcon(skill.iconId)?.drawable
        if (drawable != null) {
            DrawableCompat.setTint(
                drawable, ContextCompat.getColor(
                    holder.iconView.context, R.color.colorSecondary
                )
            )
            holder.iconView.background = drawable
        }

        holder.xpView.text = "${skill.xp} XP"
        holder.levelView.text = "Level ${skill.level}"

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
        GlobalScope.launch {
            db.skillDao().deleteSkill(skill.name)
        }
        return skill
    }

    fun restoreItem(skill: Skill, position: Int) {
        skills.add(position, skill)
        notifyItemInserted(position)
        val db = AppDatabase(this.inflater.context)
        GlobalScope.launch {
            db.skillDao().insertSkill(skill)
        }
    }
}