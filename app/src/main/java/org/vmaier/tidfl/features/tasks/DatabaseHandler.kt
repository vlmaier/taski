package org.vmaier.tidfl.features.tasks

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.vmaier.tidfl.data.Task
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 13.05.2019
 * at 19:13
 */
class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private val TAG = DatabaseHandler::class.java.simpleName

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$ID Integer PRIMARY KEY, " +
                "$GOAL TEXT, " +
                "$DETAILS TEXT, " +
                "$CREATED_AT TEXT, " +
                "$DURATION INTEGER, " +
                "$DIFFICULTY TEXT" +
                ")"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun addTask(task: Task): Boolean {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(GOAL, task.goal)
        values.put(DETAILS, task.details)
        values.put(CREATED_AT, task.createdAt.toString())
        values.put(DURATION, task.duration)
        values.put(DIFFICULTY, task.difficulty.toString())
        val success = db.insert(TABLE_NAME, null, values)
        Timber.i("Inserted ID $success")
        return (Integer.parseInt("$success") != -1)
    }

    companion object {
        private const val DB_NAME = "tidfl"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val ID = "id"
        private const val GOAL = "goal"
        private const val DETAILS = "details"
        private const val CREATED_AT = "createdAt"
        private const val DURATION = "duration"
        private const val DIFFICULTY = "difficulty"

        // TODO: outsource to skills table: task to skill(s) relationship (1:n)
        // private val AFFECTED_SKILLS = "affectedSkills"
    }
}