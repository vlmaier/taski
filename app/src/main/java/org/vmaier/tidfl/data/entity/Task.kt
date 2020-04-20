package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.vmaier.tidfl.App
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Status
import java.util.*


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
@Parcelize
data class Task(
        val id: Long,
        val goal: String,
        val details: String = "",
        val status: Status = Status.OPEN,
        val createdAt: String = App.dateFormat.format(Date()),
        val dueAt: String = "",
        val duration: Int,
        val difficulty: Difficulty = Difficulty.REGULAR,
        val iconId: Int,
        val skills: ArrayList<Skill> = arrayListOf(),
        val eventId: String = ""
) : Parcelable {

    val xp: Int
        get() {
            return difficulty.factor.times(duration).toInt()
        }

    val skillNames: List<String>
        get() {
            if (skills.isEmpty()) return listOf()
            return skills.map { skill -> skill.name }
        }
}