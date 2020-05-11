package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vladas Maier
 * on 08.05.2020
 * at 20:34
 */
@Parcelize
@Entity(
    tableName = "assigned_skills",
    foreignKeys = [
        ForeignKey(
            entity = Skill::class,
            parentColumns = [ "id" ],
            childColumns = [ "skill_id" ],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = [ "id" ],
            childColumns = [ "task_id" ],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(
            value = [ "id" ],
            unique = true
        ),
        Index(
            value = [ "skill_id", "task_id" ],
            unique = true
        )
    ]
)
data class AssignedSkill(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "skill_id")
    val skillId: Long,

    @ColumnInfo(name = "task_id")
    val taskId: Long
) : Parcelable