package com.vmaier.taski.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vmaier.taski.data.dao.CategoryDao
import com.vmaier.taski.data.dao.SkillDao
import com.vmaier.taski.data.dao.TaskDao
import com.vmaier.taski.data.entity.*


/**
 * Created by Vladas Maier
 * on 22/04/2020
 * at 16:45
 */
@Database(
    entities = [Task::class, Skill::class, Category::class, AssignedSkill::class, ClosedTask::class],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun skillDao(): SkillDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        private const val DB_NAME = "taski.db"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN reminder_request_code INTEGER")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE categories ADD COLUMN color TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE skills ADD COLUMN xp_value INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // change datetime format from "dd.MM.yyyy HH:mm" to "yyyy-MM-dd HH:mm" (ISO 8601)
                database.execSQL("UPDATE tasks SET created_at = strftime('%Y-%m-%d %H:%M', datetime(substr(created_at, 7, 4) || '-' || substr(created_at, 4, 2) || '-' || substr(created_at, 1, 2) || substr(created_at, 11, 8), 'utc')), closed_at = strftime('%Y-%m-%d %H:%M', datetime(substr(closed_at, 7, 4) || '-' || substr(closed_at, 4, 2) || '-' || substr(closed_at, 1, 2) || substr(closed_at, 11, 8), 'utc')), due_at = strftime('%Y-%m-%d %H:%M', datetime(substr(due_at, 7, 4) || '-' || substr(due_at, 4, 2) || '-' || substr(due_at, 1, 2) || substr(due_at, 11, 8), 'utc'))")
                // change datetime format to epoch
                database.execSQL("UPDATE tasks SET created_at = CAST(strftime('%s', created_at) AS INT) * 1000, closed_at = CAST(strftime('%s', closed_at) AS INT) * 1000, due_at = CAST(strftime('%s', due_at) AS INT) * 1000")
                database.beginTransaction()
                try {
                    // change type to INTEGER for created_at, closed_at and due_at columns
                    database.execSQL("ALTER TABLE tasks RENAME TO tmp_tasks")
                    database.execSQL("CREATE TABLE tasks (id INTEGER PRIMARY KEY NOT NULL, goal TEXT NOT NULL, details TEXT, status TEXT NOT NULL, created_at INTEGER NOT NULL, closed_at INTEGER, due_at INTEGER, duration INTEGER NOT NULL, difficulty TEXT NOT NULL, xp_value INTEGER NOT NULL, icon_id INTEGER NOT NULL, event_id TEXT, reminder_request_code INTEGER)")
                    database.execSQL("INSERT INTO tasks(id, goal, details, status, created_at, closed_at, due_at, duration, difficulty, xp_value, icon_id, event_id, reminder_request_code) SELECT id, goal, details, status, CAST(created_at AS INT), CAST(closed_at AS INT), CAST(due_at AS INT), duration, difficulty, xp_value, icon_id, event_id, reminder_request_code FROM tmp_tasks")
                    database.execSQL("DROP TABLE tmp_tasks")
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN rrule TEXT")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    database.execSQL("ALTER TABLE tasks ADD COLUMN count_done INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("UPDATE tasks SET count_done = 1 WHERE status = 'done'")
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tasks_id ON tasks(id)")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tasks_event_id ON tasks(event_id)")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    database.execSQL("CREATE TABLE closed_tasks (id INTEGER PRIMARY KEY NOT NULL, task_id INTEGER NOT NULL, closed_at INTEGER NOT NULL, FOREIGN KEY(task_id) REFERENCES tasks(id) ON DELETE CASCADE)")
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_closed_tasks_id ON closed_tasks(id)")
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_closed_tasks_task_id_closed_at ON closed_tasks(task_id, closed_at)")
                    database.execSQL("INSERT INTO closed_tasks (task_id, closed_at) SELECT id, closed_at FROM tasks WHERE closed_at IS NOT NULL")
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9
                ).build()
    }
}