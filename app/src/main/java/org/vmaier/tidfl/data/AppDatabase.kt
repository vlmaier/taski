package org.vmaier.tidfl.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.vmaier.tidfl.data.dao.SkillDao
import org.vmaier.tidfl.data.dao.TaskDao
import org.vmaier.tidfl.data.entity.Skill
import org.vmaier.tidfl.data.entity.Task


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 16:45
 */
@Database(entities = [Task::class, Skill::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun skillDao(): SkillDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java,
            "tidfl.db")
            .allowMainThreadQueries()
            .build()
    }
}