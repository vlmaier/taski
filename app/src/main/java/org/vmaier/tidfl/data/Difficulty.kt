package org.vmaier.tidfl.data

sealed class Difficulty(val factor: Double) {
    object TRIVIAL : Difficulty(0.5)
    object REGULAR : Difficulty(1.0)
    object HARD    : Difficulty(1.5)
    object INSANE  : Difficulty(2.0)
}