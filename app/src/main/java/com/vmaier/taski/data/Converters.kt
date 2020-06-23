package com.vmaier.taski.data

import androidx.room.TypeConverter


/**
 * Created by Vladas Maier
 * on 22/04/2020
 * at 19:34
 */
class Converters {

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.value
    }

    @TypeConverter
    fun toDifficulty(value: String): Difficulty {
        return when (value) {
            Difficulty.TRIVIAL.value -> Difficulty.TRIVIAL
            Difficulty.REGULAR.value -> Difficulty.REGULAR
            Difficulty.HARD.value -> Difficulty.HARD
            Difficulty.INSANE.value -> Difficulty.INSANE
            else -> Difficulty.REGULAR
        }
    }

    @TypeConverter
    fun fromStatus(status: Status): String {
        return status.value
    }

    @TypeConverter
    fun toStatus(value: String): Status {
        return when (value) {
            Status.OPEN.value -> Status.OPEN
            Status.DONE.value -> Status.DONE
            Status.FAILED.value -> Status.FAILED
            else -> Status.OPEN
        }
    }
}