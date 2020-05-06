package org.vmaier.tidfl.data.entity

import androidx.room.Embedded
import androidx.room.Relation


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 20:56
 */
data class TaskWithSkills(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val skills: List<Skill>
)