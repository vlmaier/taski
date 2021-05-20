package com.vmaier.taski.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Transaction
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.dao.SkillDao
import com.vmaier.taski.data.dao.TaskDao
import com.vmaier.taski.data.entity.AssignedSkill
import com.vmaier.taski.data.entity.ClosedTask
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.features.skills.SkillEditFragment
import com.vmaier.taski.features.tasks.TaskListFragment
import com.vmaier.taski.toast
import com.vmaier.taski.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


/**
 * Created by Vladas Maier
 * on 02.02.2021
 * at 19:14
 */
class TaskRepository(context: Context) {

    private var taskDao: TaskDao
    private var skillDao: SkillDao

    init {
        val database: AppDatabase = AppDatabase.invoke(context)
        taskDao = database.taskDao()
        skillDao = database.skillDao()
    }

    fun get(id: Long): Task? {
        return taskDao.get(id)
    }

    fun getByStatus(status: Status): MutableList<Task> {
        return taskDao.getByStatus(status)
    }

    fun getClosedTasks(calendar: Calendar = Calendar.getInstance()): List<Task> {
        val after = Utils.getStartOfDay(calendar)
        val before = Utils.getEndOfDay(calendar)
        return taskDao.getClosedTasks(after, before)
    }

    fun countByStatus(status: Status): Int {
        return taskDao.countByStatus(status)
    }

    fun countOverallXp(): Long {
        return taskDao.countOverallXp()
    }

    fun countDailyXp(calendar: Calendar = Calendar.getInstance()): Long {
        val after = Utils.getStartOfDay(calendar)
        val before = Utils.getEndOfDay(calendar)
        return taskDao.countDailyXp(after, before)
    }

    fun countDailyTasks(calendar: Calendar = Calendar.getInstance()): Int {
        val after = Utils.getStartOfDay(calendar)
        val before = Utils.getEndOfDay(calendar)
        return taskDao.countDailyTasks(after, before)
    }

    @Transaction
    fun create(task: Task, skillIds: List<Long>?): LiveData<Long> {
        val liveData = MutableLiveData<Long>()
        CoroutineScope(Dispatchers.IO).launch {
            val id = taskDao.create(task)
            Timber.d("$task created")
            if (skillIds != null) {
                for (skillId in skillIds) {
                    assignSkill(id, skillId)
                }
            }
            liveData.postValue(id)
        }
        return liveData
    }

    @Transaction
    fun close(
        id: Long,
        status: Status,
        closedAt: Long = System.currentTimeMillis(),
        isRecurring: Boolean = false
    ): LiveData<Long> {
        val liveData = MutableLiveData<Long>()
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateClosedAt(id, closedAt)
            if (!isRecurring) {
                taskDao.updateStatus(id, status)
                Timber.d("Task(id=$id) status updated to '$status'")
            }
            val closedTaskId: Long = if (status == Status.DONE) {
                val entity = ClosedTask(taskId = id, closedAt = closedAt)
                val closedTaskId = taskDao.close(entity)
                Timber.d("Task(id=$id) closed")
                closedTaskId
            } else {
                0L
            }
            liveData.postValue(closedTaskId)
        }
        return liveData
    }

    @Transaction
    fun reopen(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.reopen(id)
            Timber.d("'Task(id=$id) reopened")
        }
    }

    @Transaction
    fun incrementCountDone(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.incrementCountDone(id)
            Timber.d("Task(id=$id) counter incremented")
        }
    }

    @Transaction
    fun decrementCountDone(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.decrementCountDone(id)
            Timber.d("Task(id=$id) counter decremented")
        }
    }

    @Transaction
    fun updateGoal(context: Context, id: Long, goal: String) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateGoal(id, goal)
            Timber.d("Task(id=$id) goal updated to '$goal'")
            refreshUI(context, id, true)
        }
    }

    @Transaction
    fun updateDetails(context: Context, id: Long, details: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateDetails(id, details)
            Timber.d("Task(id=$id) details updated to '$details'")
            refreshUI(context, id, false)
        }
    }

    @Transaction
    fun updateDuration(context: Context, id: Long, duration: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = get(id)
            if (task != null) {
                taskDao.updateDuration(id, duration)
                taskDao.updateXpValue(id, Utils.calculateXp(task.difficulty, duration))
                Timber.d("Task(id=$id) duration updated to '$duration'")
                refreshUI(context, id, true)
            }
        }
    }

    @Transaction
    fun updateDifficulty(context: Context, id: Long, difficulty: Difficulty) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = get(id)
            if (task != null) {
                taskDao.updateDifficulty(id, difficulty)
                taskDao.updateXpValue(id, Utils.calculateXp(difficulty, task.duration))
                Timber.d("Task(id=$id) difficulty updated to '$difficulty'")
                refreshUI(context, id, true)
            }
        }
    }

    @Transaction
    fun updateDurationAndDifficulty(
        context: Context,
        id: Long,
        duration: Int,
        difficulty: Difficulty
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = get(id)
            if (task != null) {
                taskDao.updateDuration(id, duration)
                taskDao.updateDifficulty(id, difficulty)
                taskDao.updateXpValue(id, Utils.calculateXp(difficulty, duration))
                Timber.d("Task(id=$id) duration updated to '$duration'")
                Timber.d("Task(id=$id) difficulty updated to '$difficulty'")
                refreshUI(context, id, true)
            }
        }
    }

    @Transaction
    fun updateIconId(context: Context, id: Long, iconId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateIconId(id, iconId)
            Timber.d("Task(id=$id) icon ID updated to '$iconId'")
            refreshUI(context, id, false)
        }
    }

    @Transaction
    fun updateDueAt(context: Context, id: Long, dueAt: Long?) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateDueAt(id, dueAt)
            if (dueAt == null) {
                Timber.d("Task(id=$id) deadline timestamp reset")
            } else {
                Timber.d("Task(id=$id) deadline timestamp updated to '$dueAt'")
            }
            refreshUI(context, id, true)
        }
    }

    @Transaction
    fun updateClosedAt(id: Long, closedAt: Long? = System.currentTimeMillis()) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateClosedAt(id, closedAt)
            if (closedAt == null) {
                Timber.d("Task(id=$id) closure timestamp reset")
            } else {
                Timber.d("Task(id=$id) closure timestamp updated to '$closedAt'")
            }
        }
    }

    @Transaction
    fun updateEventId(id: Long, eventId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateEventId(id, eventId)
            if (eventId == null) {
                Timber.d("Task(id=$id) event ID reset")
            } else {
                Timber.d("Task(id=$id) event ID updated to '$eventId'")
            }
        }
    }

    @Transaction
    fun updateRRule(context: Context, id: Long, rrule: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateRRule(id, rrule)
            if (rrule == null) {
                Timber.d("Task(id=$id) RRULE reset")
            } else {
                Timber.d("Task(id=$id) RRULE updated to '$rrule'")
            }
            refreshUI(context, id, false)
        }
    }

    @Transaction
    fun updateRequestCode(id: Long, requestCode: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateRequestCode(id, requestCode)
            Timber.d("Task(id=$id) request code updated to '$requestCode'")
        }
    }

    @Transaction
    fun update(context: Context, new: Task, newSkillIds: List<Long>) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = new.id
            val task = taskDao.get(id)
            if (task != null) {
                if (task.goal != new.goal) updateGoal(context, id, new.goal)
                if (task.details != new.details) updateDetails(context, id, new.details)
                if (task.iconId != new.iconId) updateIconId(context, id, new.iconId)
                if (task.dueAt != new.dueAt) updateDueAt(context, id, new.dueAt)
                if (task.rrule != new.rrule) updateRRule(context, id, new.rrule)
                if (task.duration != new.duration && task.difficulty != new.difficulty) {
                    updateDurationAndDifficulty(context, id, new.duration, new.difficulty)
                } else if (task.duration != new.duration) {
                    updateDuration(context, id, new.duration)
                } else if (task.difficulty != new.difficulty) {
                    updateDifficulty(context, id, new.difficulty)
                }
                val skillIds = skillDao.getAssignedSkills(id).map { it.id }
                if (skillIds != newSkillIds) {
                    reassignSkills(context, id, newSkillIds)
                }
            }
        }
    }

    @Transaction
    fun assignSkill(id: Long, skillId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = AssignedSkill(taskId = id, skillId = skillId)
            taskDao.assignSkill(entity)
            Timber.d("Skill(id=$skillId) assigned to Task(id=$id)")
        }
    }

    @Transaction
    fun unassignSkills(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.unassignSkills(id)
            Timber.d("Skills unassigned from Task(id=$id)")
        }
    }

    @Transaction
    fun reassignSkills(context: Context, id: Long, skillIds: List<Long>) {
        CoroutineScope(Dispatchers.IO).launch {
            unassignSkills(id)
            for (skillId in skillIds) {
                assignSkill(id, skillId)
            }
            refreshUI(context, id, false, skillIds)
        }
    }

    @Transaction
    fun deleteClosedTask(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.deleteClosedTask(id)
            Timber.d("ClosedTask(id=$id) deleted")
        }
    }

    private suspend fun refreshUI(
        context: Context,
        id: Long,
        sort: Boolean = false,
        skillIds: List<Long> = arrayListOf()
    ) {
        withContext(Dispatchers.Main) {
            val task = taskDao.get(id)
            if (task != null) {
                val i = TaskListFragment.taskAdapter.tasks.indexOfFirst { it.id == id }
                TaskListFragment.taskAdapter.tasks[i] = task
                TaskListFragment.taskAdapter.notifyItemChanged(i)
                if (sort) {
                    val tasks = TaskListFragment.taskAdapter.tasks
                    TaskListFragment.sortTasks(context, tasks)
                    TaskListFragment.taskAdapter.setTasks(tasks)
                    TaskListFragment.taskAdapter.notifyDataSetChanged()
                }
                if (SkillEditFragment.isTaskAdapterInitialized() && skillIds.isNotEmpty()) {
                    val j = SkillEditFragment.taskAdapter.tasks.indexOfFirst { it.id == id }
                    if (skillIds.contains(SkillEditFragment.skill.id) && j != -1) {
                        SkillEditFragment.taskAdapter.tasks[j] = task
                    } else {
                        SkillEditFragment.taskAdapter.tasks.remove(task)
                    }
                }
                context.getString(R.string.event_task_updated).toast(context)
            }
        }
    }
}