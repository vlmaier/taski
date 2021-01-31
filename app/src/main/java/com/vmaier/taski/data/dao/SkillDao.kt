package com.vmaier.taski.data.dao

import androidx.room.*
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
    fun create(skill: Skill): Long

    // -------------------------------------  READ QUERIES  ------------------------------------- //

    @Query(
        """
        SELECT *
        FROM skills
    """
    )
    fun findAll(): MutableList<Skill>

    @Query(
        """
        SELECT COUNT(*)
        FROM skills
    """
    )
    fun countAll(): Int

    @Query(
        """
        SELECT *
        FROM skills
        WHERE id = :skillId
    """
    )
    fun findById(skillId: Long): Skill?

    @Query(
        """
        SELECT *
        FROM skills
        WHERE name = :name COLLATE NOCASE
    """
    )
    fun findByName(name: String): Skill?

    @Query(
        """
        SELECT *
        FROM skills
        WHERE name IN (:names) COLLATE NOCASE
    """
    )
    fun findByName(names: List<String>): List<Skill>

    @Query(
        """
        SELECT skills.*
        FROM assigned_skills 
        INNER JOIN skills
          ON skill_id = skills.id
        WHERE task_id = :taskId 
    """
    )
    fun findAssignedSkills(taskId: Long): List<Skill>

    @Query(
        """
        SELECT *
        FROM skills
        WHERE category_id = :categoryId 
    """
    )
    fun findSkillsByCategoryId(categoryId: Long): List<Skill>

    @Query(
        """
        SELECT *
        FROM assigned_skills
        WHERE skill_id = :skillId
    """
    )
    fun findAssignments(skillId: Long): List<AssignedSkill>

    @Query(
        """
        SELECT COUNT(*)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :skillId
          AND status != 'failed'
    """
    )
    fun countTasksWithSkill(skillId: Long): Int

    @Query(
        """
        SELECT SUM(tasks.count_done)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :skillId
    """
    )
    fun countDoneTasksWithSkill(skillId: Long): Long

    @Query(
        """
        SELECT SUM(duration * count_done) 
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :skillId
    """
    )
    fun countMinutes(skillId: Long): Int

    @Query(
        """
        SELECT tasks.*
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :skillId 
          AND status = :status 
    """
    )
    fun findTasksWithSkillByStatus(skillId: Long, status: Status): MutableList<Task>

    @Query(
        """
        SELECT COUNT(*)
        FROM skills
        WHERE category_id = :categoryId
    """
    )
    fun countSkillsWithCategory(categoryId: Long): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM assigned_skills
        WHERE task_id = :taskId 
    """
    )
    fun countAssignedSkills(taskId: Long): Int

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Update(entity = Skill::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(skill: Skill)

    @Query(
        """
        UPDATE skills
        SET name = :name
        WHERE id = :skillId
    """
    )
    suspend fun updateName(skillId: Long, name: String)

    @Query(
        """
        UPDATE skills
        SET icon_id = :iconId
        WHERE id = :skillId
    """
    )
    suspend fun updateIconId(skillId: Long, iconId: Int)

    @Query(
        """
        UPDATE skills
        SET category_id = :categoryId
        WHERE id = :skillId
    """
    )
    suspend fun updateCategoryId(skillId: Long, categoryId: Long?)

    @Query(
        """
        UPDATE skills
        SET xp_value = xp_value + :xp
        WHERE id = :skillId
    """
    )
    fun updateXp(skillId: Long, xp: Int)

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Delete
    fun delete(skill: Skill)
}