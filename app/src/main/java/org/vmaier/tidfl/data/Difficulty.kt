package org.vmaier.tidfl.data


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
sealed class Difficulty(val factor: Double) {
    object TRIVIAL : Difficulty(0.5)
    object REGULAR : Difficulty(1.0)
    object HARD    : Difficulty(1.5)
    object INSANE  : Difficulty(2.0)
}