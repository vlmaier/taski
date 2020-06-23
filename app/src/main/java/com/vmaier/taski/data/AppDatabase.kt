package com.vmaier.taski.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vmaier.taski.data.dao.CategoryDao
import com.vmaier.taski.data.dao.SkillDao
import com.vmaier.taski.data.dao.TaskDao
import com.vmaier.taski.data.entity.AssignedSkill
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.data.entity.Task


/**
 * Created by Vladas Maier
 * on 22/04/2020
 * at 16:45
 */
@Database(
    entities = [Task::class, Skill::class, Category::class, AssignedSkill::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun skillDao(): SkillDao
    abstract fun categoryDao(): CategoryDao

    companion object {

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

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "taski.db"
            ).allowMainThreadQueries().build()
    }
}