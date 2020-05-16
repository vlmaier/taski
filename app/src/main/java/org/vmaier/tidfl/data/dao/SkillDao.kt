package org.vmaier.tidfl.data.dao

import androidx.room.*
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.AssignedSkill
import org.vmaier.tidfl.data.entity.Skill


/**
 * Created by Vladas Maier
 * on 22/04/2020.
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
    fun findAll(): List<Skill>

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
        WHERE name IN (:names)
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
          AND status = :status 
    """
    )
    fun countTasksWithSkillByStatus(skillId: Long, status: Status): Long

    @Query(
        """
        SELECT COUNT(*)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :skillId
    """
    )
    fun countTasksWithSkillByStatus(skillId: Long): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM assigned_skills
        WHERE task_id = :taskId 
    """
    )
    fun countAssignedSkills(taskId: Long): Int

    @Query(
        """
        SELECT SUM(xp_value)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        WHERE skill_id = :skillId 
          AND status = 'done'
    """
    )
    fun countSkillXpValue(skillId: Long): Long

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Update(entity = Skill::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(skill: Skill)

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Delete
    fun delete(skill: Skill)
}