package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
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
    val createdAt: String = Date().toString(),
    val duration: Int,
    val difficulty: Difficulty = Difficulty.REGULAR,
    val iconId: Int
) : Parcelable {

    val xpGain: Int
        get() {
            return difficulty.factor.times(duration).toInt()
        }
}