package org.vmaier.tidfl.data


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
data class Skill (
    val name: String,
    val description: String,
    val category: Category,
    val perks: List<Perk>?
)