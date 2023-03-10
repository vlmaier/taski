package com.vmaier.taski.data.entity

import android.os.Parcelable
import androidx.room.*
import com.vmaier.taski.data.Converters
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.Status
import com.vmaier.taski.utils.Utils
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
@Parcelize
@Entity(
    tableName = "tasks",
    indices = [
        Index(
            value = ["id"],
            unique = true
        ),
        Index(
            value = ["event_id"],
            unique = true
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "goal")
    val goal: String,

    @ColumnInfo(name = "details")
    val details: String? = null,

    @ColumnInfo(name = "status")
    @TypeConverters(Converters::class)
    var status: Status = Status.OPEN,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "closed_at")
    val closedAt: Long? = null,

    @ColumnInfo(name = "due_at")
    val dueAt: Long? = null,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "difficulty")
    @TypeConverters(Converters::class)
    val difficulty: Difficulty = Difficulty.REGULAR,

    @ColumnInfo(name = "xp_value")
    val xp: Int = Utils.calculateXp(difficulty, duration),

    @ColumnInfo(name = "icon_id")
    val iconId: Int,

    @ColumnInfo(name = "event_id")
    val eventId: String? = null,

    @ColumnInfo(name = "reminder_request_code")
    val reminderRequestCode: Int? = null,

    @ColumnInfo(name = "rrule")
    val rrule: String? = null,

    @ColumnInfo(name = "count_done", defaultValue = "0")
    val countDone: Int = 0
) : Parcelable {

    val doneAt: Long?
        get() = dueAt?.plus(duration * 60 * 1000)

    val calendarRRule: String?
        // remove 'RRULE:' otherwise the calendar cannot cope with it
        get() = rrule?.replace("RRULE:", "")

    fun isUpdateRequired(other: Task): Boolean {
        if (this.goal != other.goal) return true
        if (this.details != other.details) return true
        if (this.duration != other.duration) return true
        if (this.difficulty != other.difficulty) return true
        if (this.iconId != other.iconId) return true
        if (this.dueAt != other.dueAt) return true
        if (this.rrule != other.rrule) return true
        return false
    }
}

