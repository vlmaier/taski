package org.vmaier.tidfl.data.dao

import androidx.room.*
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.data.entity.TaskWithSkills


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 16:39
 */
@Dao
interface TaskDao {

    // --- CREATE

    @Transaction
    suspend fun insertTaskWithSkills(task: Task) {

        val id = insertTask(task)
        for (skill in task.skills) {
            skill.taskId = id
        }
        insertSkills(task.skills)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSkills(skills: List<Skill>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    // --- READ

    fun findTask(id: Long): Task {
        val taskWithSkills = findTaskWithSkills(id)
        val task = taskWithSkills.task
        task.skills = taskWithSkills.skills
        return task
    }

    fun findAllTasks(): List<Task> {
        val allTasksWithSkills = findAllTasksWithSkills()
        val tasks: MutableList<Task> = mutableListOf()
        for (taskWithSkills in allTasksWithSkills) {
            val task = taskWithSkills.task
            task.skills = taskWithSkills.skills
            tasks.add(task)
        }
        return tasks
    }

    fun findAllTasksWithStatus(status: Status): List<Task> {
        val allTasksWithSkills = findAllTasksWithSkillsWithStatus(status)
        val tasks: MutableList<Task> = mutableListOf()
        for (taskWithSkills in allTasksWithSkills) {
            val task = taskWithSkills.task
            task.skills = taskWithSkills.skills
            tasks.add(task)
        }
        return tasks
    }

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun findTaskWithSkills(id: Long): TaskWithSkills

    @Transaction
    @Query("SELECT * FROM tasks")
    fun findAllTasksWithSkills(): List<TaskWithSkills>

    @Transaction
    @Query("SELECT * FROM tasks WHERE status = :status")
    fun findAllTasksWithSkillsWithStatus(status: Status): List<TaskWithSkills>

    @Query("SELECT COALESCE(SUM(COALESCE(xp, 0)), 0) FROM tasks WHERE status = :status")
    suspend fun calculateOverallXp(status: Status): Long

    // --- UPDATE

    @Transaction
    fun updateTaskWithSkills(task: Task): Task {

        val taskId = task.id
        updateTask(task)
        for (skill in task.skills) {
            skill.taskId = taskId
        }
        insertSkills(task.skills)
        return findTask(taskId)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTask(task: Task)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: Status): Int

    @Query("UPDATE tasks SET event_id = :eventId WHERE id = :id")
    suspend fun updateTaskEventId(id: Long, eventId: String): Int

    // --- DELETE

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}