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
class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null,
    DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE =
            "CREATE TABLE $TASKS (" +
                "$ID Integer PRIMARY KEY, " +
                "$GOAL TEXT, " +
                "$DETAILS TEXT, " +
                "$STATUS TEXT, " +
                "$CREATED_AT TEXT, " +
                "$DURATION INTEGER, " +
                "$DIFFICULTY TEXT," +
                "$ICON_ID TEXT" +
                ")"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun findAllTasks(): MutableList<Task> {

        val query = "SELECT * FROM $TASKS"
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

    fun addTask(goal: String, details: String, status: Status, duration: Int,
                difficulty: Difficulty, iconId: Int): Boolean {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(GOAL, goal)
        values.put(DETAILS, details)
        values.put(STATUS, status.name)
        values.put(CREATED_AT, Date().toString())
        values.put(DURATION, duration)
        values.put(DIFFICULTY, difficulty.name)
        values.put(ICON_ID, iconId)
        val success = db.insert(TASKS, null, values)
        Log.i("DB", "Inserted ID $success")
        db.close()
        return (Integer.parseInt("$success") != -1)
    }

    fun deleteTask(task: Task): Boolean {

        val db = this.writableDatabase
        val success = db.delete(TASKS, "$ID = ?", arrayOf(task.id.toString()))
        Log.i("DB", "Removal of task with ID ${task.id} is " +
                if(success != -1) "successful" else "failed"
        )
        db.close()
        return (Integer.parseInt("$success") != -1)
    }

    companion object {
        private const val DB_NAME = "tidfl"
        private const val DB_VERSION = 1
        private const val TASKS = "tasks"
        private const val ID = "id"
        private const val GOAL = "goal"
        private const val DETAILS = "details"
        private const val STATUS = "status"
        private const val CREATED_AT = "created_at"
        private const val DURATION = "duration"
        private const val DIFFICULTY = "difficulty"
        private const val ICON_ID = "icon_id"

        // TODO: outsource to skills table: task to skill(s) relationship (1:n)
        // private val AFFECTED_SKILLS = "affectedSkills"
    }
}