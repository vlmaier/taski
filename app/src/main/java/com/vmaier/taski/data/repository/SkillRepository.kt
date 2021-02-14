package com.vmaier.taski.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Transaction
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.dao.SkillDao
import com.vmaier.taski.data.dao.TaskDao
import com.vmaier.taski.data.entity.AssignedSkill
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.features.skills.SkillListFragment
import com.vmaier.taski.features.skills.SkillListFragment.Companion.skillAdapter
import com.vmaier.taski.toast
import kotlinx.coroutines.*
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 01.02.2021
 * at 15:34
 */
class SkillRepository(context: Context) {

    private var skillDao: SkillDao
    private var taskDao: TaskDao

    init {
        val database: AppDatabase = AppDatabase.invoke(context)
        skillDao = database.skillDao()
        taskDao = database.taskDao()
    }

    fun getTasksWithSkillByStatus(id: Long, status: Status): MutableList<Task> {
        return skillDao.getTasksWithSkillByStatus(id, status)
    }

    fun get(id: Long): Skill? {
        return skillDao.get(id)
    }

    fun get(name: String): Skill? {
        return skillDao.get(name)
    }

    fun getAll(): MutableList<Skill> {
        return skillDao.getAll()
    }

    fun getAllNames(): List<String> {
        return skillDao.getAll().map { it.name }
    }

    fun getByNames(names: List<String>): List<Skill> {
        return skillDao.getByNames(names)
    }

    fun getByCategoryId(categoryId: Long): List<Skill> {
        return skillDao.getByCategoryId(categoryId)
    }

    fun getAssignedSkills(taskId: Long): List<Skill> {
        return skillDao.getAssignedSkills(taskId)
    }

    fun getAssignments(id: Long): List<AssignedSkill> {
        return skillDao.getAssignments(id)
    }

    fun countTasksBySkillId(id: Long): Int {
        return skillDao.countTasksBySkillId(id)
    }

    fun countDoneTasksBySkillId(id: Long): Int {
        return skillDao.countDoneTasksBySkillId(id)
    }

    fun countMinutes(id: Long): Int {
        return skillDao.countMinutes(id)
    }

    fun countSkillsByCategoryId(categoryId: Long): Int {
        return skillDao.countSkillsByCategoryId(categoryId)
    }

    @Transaction
    fun create(name: String, iconId: Int, categoryId: Long?): LiveData<Long> {
        val liveData = MutableLiveData<Long>()
        CoroutineScope(Dispatchers.IO).launch {
            val skill = Skill(name = name, iconId = iconId, categoryId = categoryId)
            val id = skillDao.create(skill)
            Timber.d("$skill created")
            liveData.postValue(id)
        }
        return liveData
    }

    @Transaction
    fun restore(skill: Skill, assignments: List<AssignedSkill>) {
        CoroutineScope(Dispatchers.IO).launch {
            skillDao.create(skill)
            Timber.d("$skill restored")
            assignments.forEach {
                taskDao.assignSkill(it)
                Timber.d("$it reassigned to $skill")
            }
        }
    }

    @Transaction
    fun updateName(context: Context, id: Long, name: String) {
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                skillDao.updateName(id, name)
                Timber.d("Skill(id=$id) name updated to '$name'")
                refreshUI(context, id, true)
            }
        }
    }

    @Transaction
    fun updateIconId(context: Context, id: Long, iconId: Int) {
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                skillDao.updateIconId(id, iconId)
                Timber.d("Skill(id=$id) icon ID updated to '$iconId'")
                refreshUI(context, id, false)
            }
        }
    }

    @Transaction
    fun updateCategoryId(context: Context, id: Long, categoryId: Long?, showMessage: Boolean = true) {
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                skillDao.updateCategoryId(id, categoryId)
                if (categoryId == null) {
                    Timber.d("Skill(id=$id) category reset")
                } else {
                    Timber.d("Skill(id=$id) category ID updated to '$categoryId'")
                }
                refreshUI(context, id, true, showMessage)
            }
        }
    }

    @Transaction
    fun updateXp(id: Long, xp: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            skillDao.updateXp(id, xp)
            Timber.d("Skill(id=$id) XP updated to '$xp'")
        }
    }

    @Transaction
    fun delete(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            skillDao.delete(id)
            Timber.d("Skill(id=$id) deleted")
        }
    }

    private suspend fun refreshUI(context: Context, id: Long, sort: Boolean = false, showMessage: Boolean = true) {
        withContext(Dispatchers.Main) {
            val skill = skillDao.get(id)
            if (skill != null) {
                val i = skillAdapter.skills.indexOfFirst { it.id == id }
                skillAdapter.skills[i] = skill
                skillAdapter.notifyItemChanged(i)
                if (sort) {
                    val skills = skillAdapter.skills
                    SkillListFragment.sortSkills(context, skills)
                    skillAdapter.setSkills(skills)
                    skillAdapter.notifyDataSetChanged()
                }
                if (showMessage) context.getString(R.string.event_skill_updated).toast(context)
            }
        }
    }
}