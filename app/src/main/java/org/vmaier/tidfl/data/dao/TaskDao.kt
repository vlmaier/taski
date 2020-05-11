package org.vmaier.tidfl.data.dao

import androidx.room.*
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.AssignedSkill
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.data.entity.Task


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 16:39
 */
@Dao
interface TaskDao {

    // ------------------------------------- CREATE QUERIES ------------------------------------- //

    @Insert(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    fun create(task: Task) : Long

    @Insert(entity = AssignedSkill::class, onConflict = OnConflictStrategy.IGNORE)
    fun assignSkill(assignedSkill: AssignedSkill)

    fun createTask(task: Task, skills: List<Skill>) {
        val id = create(task)
        for (skill in skills) {
            assignSkill(AssignedSkill(skillId = skill.id, taskId = id))
        }
    }

    // -------------------------------------  READ QUERIES  ------------------------------------- //

    @Query("""
        SELECT *
        FROM tasks
        WHERE id = :taskId
    """)
    fun findTaskById(taskId: Long) : Task

    @Query("""
        SELECT *
        FROM tasks
        WHERE status = :status
    """)
    fun findTasksWithStatus(status: Status) : List<Task>

    @Query("""
        SELECT SUM(xp_value)
        FROM tasks
        WHERE status = 'done'        
    """)
    fun countOverallXpValue() : Long

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Query("""
        UPDATE tasks
        SET status = :status
        WHERE id = :taskId
    """)
    fun changeTaskStatus(taskId: Long, status: Status)

    @Query("""
        UPDATE tasks
        SET event_id = :eventId
        WHERE id = :taskId
    """)
    fun updateTaskEventId(taskId: Long, eventId: String)

    @Update(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(task: Task)

    @Transaction
    fun reassignSkills(taskId: Long, skills: List<Skill>) {
        removeAssignedSkills(taskId)
        for (skill in skills) {
            assignSkill(AssignedSkill(skillId = skill.id, taskId = taskId))
        }
    }

    @Transaction
    fun updateTask(task: Task, skills: List<Skill>) {
        update(task)
        reassignSkills(task.id, skills)
    }

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Query("""
        DELETE FROM assigned_skills
        WHERE task_id = :taskId
    """)
    fun removeAssignedSkills(taskId: Long)
}