package org.vmaier.tidfl.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
@Parcelize
data class Task(val id: Long, val goal: String, val details: String = "",
    val status: Status = Status.OPEN, val createdAt: String = Date().toString(),
    val duration: Int, val difficulty: Difficulty = Difficulty.REGULAR, val iconId: Int) :
    Parcelable {

    val xpGain: Int

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
            return difficulty.factor.times(duration).toInt()
        }
}