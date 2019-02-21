package org.vmaier.tidfl.data

import java.util.*

data class Task(
    val goal: String,
    val details: String = "",
    val createdAt: Date = Date(),
    val duration: Int,
    val difficulty: Difficulty = Difficulty.REGULAR,
    val affectedSkills: List<Skill>? = listOf()
) {

    val goldGain: Double

        /**
         *   5 min TRIVIAL =  1 G
         *   5 min REGULAR =  2 G
         *   5 min HARD    =  3 G
         *   5 min INSANE  =  4 G
         *
         *   1 h   TRIVIAL = 12 G
         *   1 h   REGULAR = 24 G
         *   1 h   HARD    = 36 G
         *   1 h   INSANE  = 48 G
         */
        get() {
            // TODO: other factors from skills or badges etc.
            return difficulty.factor.times(duration.div(5))
        }

    val xpGain: Double

        /**
         *   1 min TRIVIAL =   1 XP
         *   1 min REGULAR =   2 XP
         *   1 min HARD    =   3 XP
         *   1 min INSANE  =   4 XP
         *
         *   1 h   TRIVIAL =  60 XP
         *   1 h   REGULAR = 120 XP
         *   1 h   HARD    = 180 XP
         *   1 h   INSANE  = 240 XP
         */
        get() {
            // TODO: other factors from skills or badges etc.
            return difficulty.factor.times(duration)
        }
}