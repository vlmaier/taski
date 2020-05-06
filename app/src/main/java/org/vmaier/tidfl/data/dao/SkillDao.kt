package org.vmaier.tidfl.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.vmaier.tidfl.data.entity.Skill


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 16:43
 */
@Dao
interface SkillDao {

    @Query("SELECT * FROM skills")
    fun findAll(): List<Skill>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(skill: Skill)

    @Query("DELETE FROM skills")
    suspend fun deleteAll()
}