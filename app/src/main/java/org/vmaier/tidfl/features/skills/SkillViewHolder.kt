package org.vmaier.tidfl.features.skills

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.entity.Skill


/**
 * Created by Vladas Maier
 * on 25/02/2020.
 * at 19:40
 */
class SkillViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
    inflater.inflate(
        R.layout.item_skill, parent,
        false
    )
) {

    private var id: Long = 0
    private var nameView: TextView? = itemView.findViewById(R.id.skill_name)
    private var categoryView: TextView? = itemView.findViewById(R.id.skill_category)
    private var levelView: TextView? = itemView.findViewById(R.id.skill_level)
    private var iconView: ImageView? = itemView.findViewById(R.id.skill_icon)
    private var xpView: TextView? = itemView.findViewById(R.id.skill_xp_gain)

    fun bind(context: Context, skill: Skill) {
        id = skill.id
        nameView?.text = skill.name
        categoryView?.text = "(${skill.category})"
        val drawable = App.iconPack.getIcon(skill.iconId)?.drawable!!
        DrawableCompat.setTint(
            drawable, ContextCompat.getColor(
                context, R.color.colorSecondary
            )
        )
        iconView?.background = App.iconPack.getIcon(skill.iconId)?.drawable
        xpView?.text = "${skill.xpGain}XP"
        levelView?.text = "Level ${skill.level}"

        itemView.setOnClickListener {
            it.findNavController().navigate(
                SkillListFragmentDirections.actionSkillListFragmentToSkillEditFragment(
                    skill,
                    this.adapterPosition
                )
            )
        }
    }
}