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

        val CREATE_TABLE_SKILLS =
                "CREATE TABLE $SKILLS (" +
                        "$ID INTEGER PRIMARY KEY, " +
                        "$NAME TEXT, " +
                        "$CATEGORY TEXT, " +
                        "$ICON_ID INTEGER, " +
                        "FOREIGN KEY($CATEGORY) REFERENCES $CATEGORIES($ID)" +
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
        db?.execSQL(CREATE_TABLE_SKILLS)
        db?.execSQL(CREATE_TABLE_TASK_SKILLS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // nothing to upgrade yet
    }

    fun calculateOverallXp(): Long {

        val query = "SELECT SUM($XP_GAIN) FROM $TASKS WHERE $STATUS = 'DONE'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val xpCounter = if (cursor.moveToFirst()) cursor.getLong(0) else 0
        cursor.close()
        db.close()
        return xpCounter
    }

    fun findAllTasks(): MutableList<Task> {

        val query = "SELECT * FROM $TASKS WHERE $STATUS = 'OPEN'"
        val db = this.readableDatabase
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
                val skills = findTaskAssociatedSkills(id)
                tasks.add(
                        Task(id, goal, details, status, createdAt, duration, difficulty, iconId, skills)
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return tasks
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
                        Skill(
                                id,
                                name,
                                category,
                                iconId
                        )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return skills
    }

    private fun findTaskAssociatedSkills(taskId: Long): ArrayList<Skill> {

        val query = "SELECT $ID, $NAME, $CATEGORY, $ICON_ID FROM $TASK_SKILLS " +
                "INNER JOIN $SKILLS ON $SKILL_ID = $ID " +
                "WHERE $TASK_ID = $taskId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val skills = arrayListOf<Skill>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getLong(0)
                val name = cursor.getString(1)
                val category = cursor.getString(2)
                val iconId = cursor.getInt(3)
                skills.add(Skill(id, name, category, iconId))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return skills
    }

    fun findAllSkillNames(): ArrayList<String> {

        val query = "SELECT $NAME FROM $SKILLS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val skillNames = arrayListOf<String>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val name = cursor.getString(0)
                skillNames.add(name)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return skillNames
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
            val duration = cursor.getInt(5)
            val difficulty = Difficulty.valueOf(cursor.getString(6))
            val iconId = cursor.getInt(7)
            val skills = findTaskAssociatedSkills(id)
            task = Task(id, goal, details, status, createdAt, duration, difficulty, iconId, skills)
        }
        cursor.close()
        return task
    }

    fun addTask(
            goal: String, details: String, status: Status, duration: Int,
            difficulty: Difficulty, iconId: Int, skills: Array<String> = arrayOf()
    ): Task? {

        val db = this.writableDatabase
        db.beginTransaction();
        try {
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
            if (skills.isNotEmpty()) addSkills(success, skills)
            db.setTransactionSuccessful()
            return findTask(success)
        } finally {
            db.endTransaction()
            db.close()
        }
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

    fun checkForChangesWithinTask(
            id: Long, goal: String, details: String, duration: Int, difficulty: Difficulty,
            iconId: Int, skills: Array<String>
    ): Boolean {

        val task = findTask(id) ?: return false
        return !(task.goal == goal &&
                task.details == details &&
                task.duration == duration &&
                task.difficulty == difficulty &&
                task.iconId == iconId &&
                task.skillNames == skills.toList())
    }

    fun checkForChangesWithinSkill(
            id: Long, name: String, category: String, iconId: Int
    ): Boolean {

        val skill = findSkill(id) ?: return false
        return !(skill.name == name &&
                skill.category == category &&
                skill.iconId == iconId)
    }

    fun updateTask(
            id: Long, goal: String, details: String, duration: Int, difficulty: Difficulty,
            iconId: Int, skills: Array<String>
    ): Task? {

        val db = this.writableDatabase
        db.beginTransaction();
        try {
            val values = ContentValues()
            values.put(GOAL, goal)
            values.put(DETAILS, details)
            values.put(CREATED_AT, Date().toString())
            values.put(DURATION, duration)
            values.put(DIFFICULTY, difficulty.name)
            values.put(ICON_ID, iconId)
            values.put(XP_GAIN, difficulty.factor.times(duration).toInt())
            db.update(TASKS, values, "$ID = ?", arrayOf(id.toString()))
            updateTaskSkills(id, skills)
            db.setTransactionSuccessful()
            return findTask(id)
        } finally {
            db.endTransaction()
            db.close()
        }
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

    private fun updateTaskSkills(taskId: Long, skills: Array<String>) {
        deleteTaskSkills(taskId)
        addSkills(taskId, skills)
    }

    private fun deleteTaskSkills(taskId: Long) {

        val db = this.writableDatabase
        db.delete(TASK_SKILLS, "$TASK_ID = ?", arrayOf(taskId.toString()))
    }

    fun restoreSkill(skill: Skill) {
        addSkill(skill.name, skill.category, skill.iconId)
    }

    fun updateTaskStatus(task: Task, status: Status): Task? {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(STATUS, status.name)
        db.update(TASKS, values, "$ID = ?", arrayOf(task.id.toString()))
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
        private const val CATEGORY = "category"
        private const val TASK_ID = "task_id"
        private const val SKILL_ID = "skill_id"
    }
}