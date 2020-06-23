package com.vmaier.taski.data

/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
enum class Difficulty(val value: String, val factor: Double) {
    TRIVIAL("trivial", 0.5),
    REGULAR("regular", 1.0),
    HARD("hard", 1.5),
    INSANE("insane", 2.0)
}