package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import org.vmaier.tidfl.App
import org.vmaier.tidfl.data.Converters
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Status
import java.util.*


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
    val status: Status = Status.OPEN,

    @ColumnInfo(name = "created_at")
    val createdAt: String = App.dateFormat.format(Date()),

    @ColumnInfo(name = "due_at")
    val dueAt: String? = null,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "difficulty")
    @TypeConverters(Converters::class)
    val difficulty: Difficulty = Difficulty.REGULAR,

    @ColumnInfo(name = "xp_value")
    val xpValue: Int = difficulty.factor.times(duration).toInt(),

    @ColumnInfo(name = "icon_id")
    val iconId: Int,

    @ColumnInfo(name = "event_id")
    val eventId: String? = null
) : Parcelable