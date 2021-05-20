package com.vmaier.taski.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.AssignedSkill
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.data.entity.Task


/**
 * Created by Vladas Maier
 * on 22.04.2020
 * at 16:43
 */
@Dao
interface SkillDao {

    // ------------------------------------- CREATE QUERIES ------------------------------------- //

    @Insert(entity = Skill::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(skill: Skill): Long

    // -------------------------------------  READ QUERIES  ------------------------------------- //

    @Query(
        """
        SELECT *
        FROM skills
        WHERE id = :id
    """
    )
    fun get(id: Long): Skill?

    @Query(
        """
        SELECT *
        FROM skills
        WHERE name = :name COLLATE NOCASE
    """
    )
    fun get(name: String): Skill?

    @Query(
        """
        SELECT *
        FROM skills
    """
    )
    fun getAll(): MutableList<Skill>

    @Query(
        """
        SELECT *
        FROM skills
        WHERE name IN (:names) COLLATE NOCASE
    """
    )
    fun getByNames(names: List<String>): List<Skill>

    @Query(
        """
        SELECT *
        FROM skills
        WHERE category_id = :categoryId 
    """
    )
    fun getByCategoryId(categoryId: Long): List<Skill>

    @Query(
        """
        SELECT skills.*
        FROM assigned_skills 
        INNER JOIN skills
          ON skill_id = skills.id
        WHERE task_id = :taskId 
    """
    )
    fun getAssignedSkills(taskId: Long): List<Skill>

    @Query(
        """
        SELECT *
        FROM assigned_skills
        WHERE skill_id = :id
    """
    )
    fun getAssignments(id: Long): List<AssignedSkill>

    @Query(
        """
        SELECT tasks.*
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :id
          AND status = :status 
    """
    )
    fun getTasksWithSkillByStatus(id: Long, status: Status): MutableList<Task>

    @Query(
        """
        SELECT COUNT(*)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :id
          AND status != 'failed'
    """
    )
    fun countTasksBySkillId(id: Long): Int

    @Query(
        """
        SELECT SUM(tasks.count_done)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :id
    """
    )
    fun countDoneTasksBySkillId(id: Long): Int

    @Query(
        """
        SELECT SUM(duration * count_done) 
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :id
    """
    )
    fun countMinutes(id: Long): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM skills
        WHERE category_id = :categoryId
    """
    )
    fun countSkillsByCategoryId(categoryId: Long): Int

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Query(
        """
        UPDATE skills
        SET name = :name
        WHERE id = :id
    """
    )
    suspend fun updateName(id: Long, name: String)

    @Query(
        """
        UPDATE skills
        SET icon_id = :iconId
        WHERE id = :id
    """
    )
    suspend fun updateIconId(id: Long, iconId: Int)

    @Query(
        """
        UPDATE skills
        SET category_id = :categoryId
        WHERE id = :id
    """
    )
    suspend fun updateCategoryId(id: Long, categoryId: Long?)

    @Query(
        """
        UPDATE skills
        SET xp_value = xp_value + :xp
        WHERE id = :id
    """
    )
    suspend fun updateXp(id: Long, xp: Int)

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Query(
        """
        DELETE FROM skills
        WHERE id = :id
    """
    )
    suspend fun delete(id: Long)
}