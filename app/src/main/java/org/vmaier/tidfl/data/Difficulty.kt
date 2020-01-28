package org.vmaier.tidfl.data


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
enum class Difficulty(val factor: Double) {
    TRIVIAL(0.5),
    REGULAR(1.0),
    HARD(1.5),
    INSANE(2.0)
}