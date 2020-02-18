package org.vmaier.tidfl.features.tasks

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.Task
import java.util.*


/**
 * Created by Vladas Maier
 * on 13.05.2019
 * at 19:13
 */
class DatabaseHandler(context: Context) : SQLiteOpenHelper(
    context, DB_NAME, null,
    DB_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE_TASKS =
            "CREATE TABLE $TASKS (" +
                    "$ID INTEGER PRIMARY KEY, " +
                    "$GOAL TEXT, " +
                    "$DETAILS TEXT, " +
                    "$STATUS TEXT, " +
                    "$CREATED_AT TEXT, " +
                    "$DURATION INTEGER, " +
                    "$DIFFICULTY TEXT, " +
                    "$ICON_ID INTEGER, " +
                    "$XP_GAIN TEXT" +
                    ")"

        val CREATE_TABLE_CATEGORIES =
            "CREATE TABLE $CATEGORIES (" +
                    "$ID INTEGER PRIMARY KEY, " +
                    "$NAME TEXT, " +
                    "$DESCRIPTION TEXT" +
                    ")"

        val CREATE_TABLE_SKILLS =
            "CREATE TABLE $SKILLS (" +
                    "$ID INTEGER PRIMARY KEY, " +
                    "$NAME TEXT, " +
                    "$DESCRIPTION TEXT, " +
                    "$CATEGORY_ID INTEGER, " +
                    "FOREIGN KEY($CATEGORY_ID) REFERENCES $CATEGORIES($ID)" +
                    ")"

        val CREATE_TABLE_TASK_SKILLS =
            "CREATE TABLE $TASK_SKILLS (" +
                    "$TASK_ID INTEGER, " +
                    "$SKILL_ID INTEGER, " +
                    "PRIMARY KEY($TASK_ID, $SKILL_ID), " +
                    "FOREIGN KEY($TASK_ID) REFERENCES $TASKS($ID), " +
                    "FOREIGN KEY($SKILL_ID) REFERENCES $SKILLS($ID)" +
                    ")"

        db?.execSQL(CREATE_TABLE_TASKS)
        db?.execSQL(CREATE_TABLE_CATEGORIES)
        db?.execSQL(CREATE_TABLE_SKILLS)
        db?.execSQL(CREATE_TABLE_TASK_SKILLS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // nothing to upgrade yet
    }

    fun calculateOverallXp(): Long {

        val query = "SELECT SUM($XP_GAIN) FROM $TASKS WHERE $STATUS = 'DONE'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val xpCounter = if (cursor.moveToFirst()) cursor.getLong(0) else 0
        cursor.close()
        return xpCounter
    }

    fun findAllTasks(): MutableList<Task> {

        val query = "SELECT * FROM $TASKS WHERE $STATUS = 'OPEN'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val tasks = arrayListOf<Task>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getLong(0)
                val goal = cursor.getString(1)
                val details = cursor.getString(2)
                val status = Status.valueOf(cursor.getString(3))
                val createdAt = cursor.getString(4)
                val duration = cursor.getInt(5)
                val difficulty = Difficulty.valueOf(cursor.getString(6))
                val iconId = cursor.getInt(7)
                tasks.add(Task(id, goal, details, status, createdAt, duration, difficulty, iconId))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return tasks
    }

    fun findTask(taskId: Long): Task? {

        val query = "SELECT * FROM $TASKS WHERE $ID = $taskId"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var task: Task? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            val goal = cursor.getString(1)
            val details = cursor.getString(2)
            val status = Status.valueOf(cursor.getString(3))
            val createdAt = cursor.getString(4)
            val duration = cursor.getInt(5)
            val difficulty = Difficulty.valueOf(cursor.getString(6))
            val iconId = cursor.getInt(7)
            task = Task(id, goal, details, status, createdAt, duration, difficulty, iconId)
        }
        cursor.close()
        return task
    }

    fun addTask(
        goal: String, details: String, status: Status, duration: Int,
        difficulty: Difficulty, iconId: Int
    ): Task? {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(GOAL, goal)
        values.put(DETAILS, details)
        values.put(STATUS, status.name)
        values.put(CREATED_AT, Date().toString())
        values.put(DURATION, duration)
        values.put(DIFFICULTY, difficulty.name)
        values.put(ICON_ID, iconId)
        values.put(XP_GAIN, difficulty.factor.times(duration).toInt())
        val success = db.insert(TASKS, null, values)
        Log.i("DB", "Added new task (ID: $success)")
        db.close()
        return findTask(success)
    }

    fun checkForChangesWithinTask(
        id: Long, goal: String, details: String, duration: Int, difficulty: Difficulty,
        iconId: Int
    ): Boolean {

        val task = findTask(id) ?: return false
        return !(task.goal == goal &&
                task.details == details &&
                task.duration == duration &&
                task.difficulty == difficulty &&
                task.iconId == iconId)
    }

    fun updateTask(
        id: Long, goal: String, details: String, duration: Int, difficulty: Difficulty,
        iconId: Int
    ): Task? {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(GOAL, goal)
        values.put(DETAILS, details)
        values.put(CREATED_AT, Date().toString())
        values.put(DURATION, duration)
        values.put(DIFFICULTY, difficulty.name)
        values.put(ICON_ID, iconId)
        values.put(XP_GAIN, difficulty.factor.times(duration).toInt())
        val success = db.update(
            TASKS, values, "$ID = ?",
            arrayOf(id.toString())
        )
        Log.i(
            "DB", "Updating of task with ID $id " +
                    if (success != -1) "is successful" else "failed"
        )
        db.close()
        return findTask(id)
    }

    fun updateTaskStatus(task: Task, status: Status): Task? {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(STATUS, status.name)
        val success = db.update(
            TASKS, values, "$ID = ?",
            arrayOf(task.id.toString())
        )
        Log.i(
            "DB", "Updating status of task with ID ${task.id}" +
                    " (${task.status.name} -> ${status.name})" +
                    if (success != -1) " is successful" else "failed"
        )
        db.close()
        return findTask(task.id)
    }

    companion object {

        private const val DB_NAME = "tidfl"
        private const val DB_VERSION = 1

        private const val TASKS = "tasks"
        private const val CATEGORIES = "categories"
        private const val SKILLS = "skills"
        private const val TASK_SKILLS = "task_skills"

        private const val ID = "id"
        private const val GOAL = "goal"
        private const val DETAILS = "details"
        private const val STATUS = "status"
        private const val CREATED_AT = "created_at"
        private const val DURATION = "duration"
        private const val DIFFICULTY = "difficulty"
        private const val ICON_ID = "icon_id"
        private const val XP_GAIN = "xp_gain"
        private const val NAME = "name";
        private const val DESCRIPTION = "description"
        private const val CATEGORY_ID = "category_id"
        private const val TASK_ID = "task_id"
        private const val SKILL_ID = "skill_id"
    }
}