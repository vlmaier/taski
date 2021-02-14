package com.vmaier.taski.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Transaction
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.dao.CategoryDao
import com.vmaier.taski.data.dao.SkillDao
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.entity.Skill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 30.01.2021
 * at 17:09
 */
class CategoryRepository(context: Context) {

    private var categoryDao: CategoryDao
    private var skillDao: SkillDao
    private var skillRepository: SkillRepository

    init {
        val database: AppDatabase = AppDatabase.invoke(context)
        categoryDao = database.categoryDao()
        skillDao = database.skillDao()
        skillRepository = SkillRepository(context)
    }

    fun get(id: Long): Category? {
        return categoryDao.get(id)
    }

    fun get(name: String): Category? {
        return categoryDao.get(name)
    }

    fun getAll(): MutableList<Category> {
        return categoryDao.getAll()
    }

    fun getAllNames(): List<String> {
        return categoryDao.getAll().map { it.name }
    }

    fun getNameById(id: Long): String? {
        return categoryDao.getNameById(id)
    }

    fun countXP(id: Long): Long {
        return categoryDao.countXP(id)
    }

    @Transaction
    fun create(name: String, color: String?): LiveData<Long> {
        val liveData = MutableLiveData<Long>()
        CoroutineScope(IO).launch {
            val category = Category(name = name, color = color)
            val id = categoryDao.create(category)
            Timber.d("$category created")
            liveData.postValue(id)
        }
        return liveData
    }

    @Transaction
    fun create(context: Context, name: String, color: String?, skillId: Long) {
        CoroutineScope(IO).launch {
            val category = Category(name = name, color = color)
            val id = categoryDao.create(category)
            Timber.d("$category created")
            skillRepository.updateCategoryId(context, skillId, id, false)
        }
    }

    @Transaction
    fun restore(category: Category, skills: List<Skill>) {
        CoroutineScope(IO).launch {
            val id = categoryDao.create(category)
            Timber.d("$category restored")
            skills.forEach {
                // UI refresh is not needed here
                skillDao.updateCategoryId(it.id, id)
                Timber.d("$it reassigned to $category")
            }
        }
    }

    @Transaction
    fun updateName(id: Long, name: String) {
        CoroutineScope(IO).launch {
            categoryDao.updateName(id, name)
            Timber.d("Category(id=$id) name updated to '$name'")
        }
    }

    @Transaction
    fun updateColor(id: Long, color: String?) {
        CoroutineScope(IO).launch {
            categoryDao.updateColor(id, color)
            if (color == null) {
                Timber.d("Category(id=$id) color reset")
            } else {
                Timber.d("Category(id=$id) color updated to '$color'")
            }
        }
    }

    @Transaction
    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            categoryDao.delete(id)
            Timber.d("Category(id=$id) deleted")
        }
    }
}