package com.vmaier.taski.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


/**
 * Created by Vladas Maier
 * on 10/01/2021
 * at 18:35
 */
@Entity(
    tableName = "closed_tasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(
            value = ["id"],
            unique = true
        ),
        Index(
            value = ["task_id", "closed_at"],
            unique = true
        )
    ]
)
data class ClosedTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "task_id")
    val taskId: Long,

    @ColumnInfo(name = "closed_at")
    val closedAt: Long
)
