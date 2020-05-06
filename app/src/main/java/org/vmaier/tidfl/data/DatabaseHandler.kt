package org.vmaier.tidfl.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.data.entity.Task
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

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun calculateSkillXp(skillId: Long): Long {

        val query = "SELECT SUM($XP) FROM $TASKS " +
                "INNER JOIN $TASK_SKILLS ON $TASK_ID = $ID " +
                "WHERE $SKILL_ID = $skillId AND $STATUS = 'DONE'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val xpCounter = if (cursor.moveToFirst()) cursor.getLong(0) else 0
        cursor.close()
        db.close()
        return xpCounter
    }

    fun findAllSkills(): MutableList<Skill> {

        val query = "SELECT * FROM $SKILLS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val skills = arrayListOf<Skill>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getLong(0)
                val name = cursor.getString(1)
                val category = cursor.getString(2)
                val iconId = cursor.getInt(3)
                skills.add(
                        Skill(id, name, category, iconId)
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return skills
    }

    fun findAmountOfTasksForSkill(skillId: Long, status: Status): Int {

        val query = "SELECT COUNT(*) FROM $TASK_SKILLS " +
                "INNER JOIN $TASKS ON $TASK_ID = $ID " +
                "WHERE $SKILL_ID = $skillId " +
                "AND $STATUS = '${status.name}'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val taskCounter = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        db.close()
        return taskCounter
    }

    fun findAllCategories(): ArrayList<String> {

        val query = "SELECT DISTINCT(category) FROM $SKILLS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val categories = arrayListOf<String>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val category = cursor.getString(0)
                categories.add(category)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return categories
    }

    private fun findTask(taskId: Long): Task? {

        val query = "SELECT * FROM $TASKS WHERE $ID = $taskId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var task: Task? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            val goal = cursor.getString(1)
            val details = cursor.getString(2)
            val status = Status.valueOf(cursor.getString(3))
            val createdAt = cursor.getString(4)
            val dueAt = cursor.getString(5)
            val duration = cursor.getInt(6)
            val difficulty = Difficulty.valueOf(cursor.getString(7))
            val iconId = cursor.getInt(8)
            val xp = cursor.getInt(9)
            val eventId = cursor?.getString(10) ?: ""
           task = Task(
                id, goal, details, status,
                createdAt, dueAt, duration, difficulty, xp, iconId, eventId
            )
        }
        cursor.close()
        return task
    }

    private fun addSkills(taskId: Long, skills: Array<String>) {

        val db = this.writableDatabase
        val allSkills = findAllSkills()
        for (skill in skills) {
            val foundSkill = allSkills.find { it.name == skill } ?: continue
            val values = ContentValues()
            values.put(TASK_ID, taskId)
            values.put(SKILL_ID, foundSkill.id)
            db.insert(TASK_SKILLS, null, values)
        }
    }

    private fun findSkill(skillId: Long): Skill? {

        val query = "SELECT * FROM $SKILLS WHERE $ID = $skillId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var skill: Skill? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            val name = cursor.getString(1)
            val category = cursor.getString(2)
            val iconId = cursor.getInt(3)
            skill = Skill(id, name, category, iconId)
        }
        cursor.close()
        return skill
    }

    fun addSkill(
            name: String, category: String, iconId: Int
    ): Skill? {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME, name)
        values.put(CATEGORY, category)
        values.put(ICON_ID, iconId)
        val success = db.insert(SKILLS, null, values)
        return findSkill(success)
    }

    fun updateSkill(
            id: Long, name: String, category: String, iconId: Int
    ): Skill? {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME, name)
        values.put(CATEGORY, category)
        values.put(ICON_ID, iconId)
        db.update(SKILLS, values, "$ID = ?", arrayOf(id.toString()))
        db.close()
        return findSkill(id)
    }

    fun deleteSkill(skill: Skill) {

        val db = this.writableDatabase
        db.delete(SKILLS, "$ID = ?", arrayOf(skill.id.toString()))
        db.close()
    }

    fun restoreSkill(skill: Skill) {
        addSkill(skill.name, skill.category, skill.iconId)
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
        private const val DUE_AT = "due_at"
        private const val DURATION = "duration"
        private const val DIFFICULTY = "difficulty"
        private const val ICON_ID = "icon_id"
        private const val XP = "xp"
        private const val NAME = "name"
        private const val CATEGORY = "category"
        private const val TASK_ID = "task_id"
        private const val SKILL_ID = "skill_id"
        private const val EVENT_ID = "event_id"
    }
}