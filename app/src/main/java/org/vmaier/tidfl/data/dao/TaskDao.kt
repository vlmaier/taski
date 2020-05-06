package org.vmaier.tidfl.data.dao

import androidx.room.*
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.data.entity.TaskWithSkills


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 16:39
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun findAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun findOne(id: Long): Task

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun findTaskSkills(id: Long): List<TaskWithSkills>

    @Query("SELECT COALESCE(SUM(COALESCE(xp, 0)), 0) FROM tasks WHERE status = :status")
    suspend fun calculateOverallXp(status: Status): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: Task)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: Status): Int

    @Query("UPDATE tasks SET event_id = :eventId WHERE id = :id")
    suspend fun updateEventId(id: Long, eventId: String): Int

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}