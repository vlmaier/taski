package org.vmaier.tidfl.data.entity


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
data class Skill(
    val id: Long,
    val name: String,
    val category: String,
    val iconId: Int
) {
    val xpGain: Int
        get() {
            return 0
        }

    val level: Int
        get() {
            return 1
        }
}