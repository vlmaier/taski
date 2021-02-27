package com.vmaier.taski.data


/**
 * Created by Vladas Maier
 * on 27.07.2020
 * at 18:41
 */
enum class SortTasks(val value: String) {
    CREATED_AT("created_at"),
    GOAL("goal"),
    DURATION("duration"),
    DIFFICULTY("difficulty"),
    XP_GAIN("xp_gain"),
    DUE_ON("due_on")
}