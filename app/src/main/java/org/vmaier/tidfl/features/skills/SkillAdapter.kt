package org.vmaier.tidfl.features.skills

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.data.entity.Skill


/**
 * Created by Vladas Maier
 * on 25/02/2020.
 * at 19:27
 */
class SkillAdapter(list: MutableList<Skill>, private val context: Context) :
        RecyclerView.Adapter<SkillViewHolder>() {

    private val dbHandler = DatabaseHandler(context)

    var items: MutableList<Skill> = list.toMutableList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SkillViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill: Skill = items[position]
        holder.bind(context, skill)
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(position: Int): Skill? {
        val skill = items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
        dbHandler.deleteSkill(skill)
        return skill
    }

    fun restoreItem(skill: Skill, position: Int) {
        items.add(position, skill)
        notifyItemInserted(position)
        dbHandler.restoreSkill(skill)
    }
}