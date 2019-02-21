package org.vmaier.tidfl.data

data class Skill (
    val name: String,
    val description: String,
    val category: Category,
    val perks: List<Perk>?
)