package com.vmaier.taski.data.dao

import androidx.room.*
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.AssignedSkill
import com.vmaier.taski.data.entity.ClosedTask
import com.vmaier.taski.data.entity.Task


/**
 * Created by Vladas Maier
 * on 22.04.2020
 * at 16:39
 */
@Dao
interface TaskDao {

    // ------------------------------------- CREATE QUERIES ------------------------------------- //

    @Insert(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(task: Task): Long

    @Insert(entity = AssignedSkill::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun assignSkill(assignedSkill: AssignedSkill)

    @Insert(entity = ClosedTask::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun close(closedTask: ClosedTask): Long

    // -------------------------------------  READ QUERIES  ------------------------------------- //

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE id = :id
    """
    )
    fun get(id: Long): Task?

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE status = :status
    """
    )
    fun getByStatus(status: Status): MutableList<Task>

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE closed_at > :after AND closed_at < :before AND count_done > 0
    """
    )
    fun getClosedTasks(after: Long, before: Long): List<Task>

    @Query(
        """
        SELECT COUNT(*)
        FROM tasks
        WHERE status = :status
    """
    )
    fun countByStatus(status: Status): Int

    @Query(
        """
        SELECT SUM(xp_value * count_done)
        FROM tasks
    """
    )
    fun countOverallXp(): Long

    @Query(
        """
        SELECT SUM(xp_value)
        FROM tasks
        INNER JOIN closed_tasks 
          ON tasks.id = closed_tasks.task_id
        WHERE closed_tasks.closed_at > :after AND closed_tasks.closed_at < :before
    """
    )
    fun countDailyXp(after: Long, before: Long): Long

    @Query(
        """
        SELECT COUNT(*)
        FROM tasks
        INNER JOIN closed_tasks 
          ON tasks.id = closed_tasks.task_id
        WHERE closed_tasks.closed_at > :after AND closed_tasks.closed_at < :before
    """
    )
    fun countDailyTasks(after: Long, before: Long): Int

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Query(
        """
        UPDATE tasks
        SET count_done = count_done + 1
        WHERE id = :id
    """
    )
    suspend fun incrementCountDone(id: Long)

    @Query(
        """
        UPDATE tasks
        SET count_done = count_done - 1
        WHERE id = :id
    """
    )
    suspend fun decrementCountDone(id: Long)

    @Query(
        """
        UPDATE tasks
        SET goal = :goal
        WHERE id = :id
    """
    )
    suspend fun updateGoal(id: Long, goal: String)

    @Query(
        """
        UPDATE tasks
        SET details = :details
        WHERE id = :id
    """
    )
    suspend fun updateDetails(id: Long, details: String?)

    @Query(
        """
        UPDATE tasks
        SET duration = :duration
        WHERE id = :id
    """
    )
    suspend fun updateDuration(id: Long, duration: Int)

    @Query(
        """
        UPDATE tasks
        SET icon_id = :iconId
        WHERE id = :id
    """
    )
    suspend fun updateIconId(id: Long, iconId: Int)

    @Query(
        """
        UPDATE tasks
        SET status = :status
        WHERE id = :id
    """
    )
    suspend fun updateStatus(id: Long, status: Status)

    @Query(
        """
        UPDATE tasks
        SET difficulty = :difficulty
        WHERE id = :id
    """
    )
    suspend fun updateDifficulty(id: Long, difficulty: Difficulty)

    @Query(
        """
        UPDATE tasks
        SET xp_value = :xp
        WHERE id = :id
    """
    )
    suspend fun updateXpValue(id: Long, xp: Int)

    @Query(
        """
        UPDATE tasks
        SET due_at = :dueAt
        WHERE id = :id
    """
    )
    suspend fun updateDueAt(id: Long, dueAt: Long?)

    @Query(
        """
        UPDATE tasks
        SET closed_at = :closedAt
        WHERE id = :id
    """
    )
    suspend fun updateClosedAt(id: Long, closedAt: Long?)

    @Query(
        """
        UPDATE tasks
        SET event_id = :eventId
        WHERE id = :id
    """
    )
    suspend fun updateEventId(id: Long, eventId: String?)

    @Query(
        """
        UPDATE tasks
        SET rrule = :rrule
        WHERE id = :id
    """
    )
    suspend fun updateRRule(id: Long, rrule: String?)

    @Query(
        """
        UPDATE tasks
        SET reminder_request_code = :requestCode
        WHERE id = :id
    """
    )
    suspend fun updateRequestCode(id: Long, requestCode: Int)

    @Query(
        """
        UPDATE tasks
        SET status = 'open'
        WHERE id = :id
    """
    )
    suspend fun reopen(id: Long)

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Query(
        """
        DELETE FROM assigned_skills
        WHERE task_id = :id
    """
    )
    suspend fun unassignSkills(id: Long)

    @Query(
        """
        DELETE FROM closed_tasks
        WHERE id = :id
    """
    )
    suspend fun deleteClosedTask(id: Long)
}