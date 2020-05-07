package org.vmaier.tidfl.data.dao

import androidx.room.*
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Skill


/**
 * Created by Vladas Maier
 * on 22/04/2020.
 * at 16:43
 */
@Dao
interface SkillDao {

    // --- CREATE

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: Skill)

    // --- READ

    @Transaction
    fun findSkill(name: String): Skill {
        val skill = _findSkill(name)
        skill.xp = calculateSkillXp(name, Status.DONE)
        skill.level = skill.xp.div(1000) + 1
        return skill
    }

    @Query("SELECT * FROM skills WHERE name = :name")
    fun _findSkill(name: String): Skill

    @Query("SELECT * FROM skills WHERE name IN (:names)")
    fun findSkills(names: List<String>): List<Skill>

    @Transaction
    fun findAllSkills(): List<Skill> {
        val allSkills = _findAllSkills()
        val skills: MutableList<Skill> = mutableListOf()
        for (skill in allSkills) {
            skill.xp = calculateSkillXp(skill.name, Status.DONE)
            skill.level = skill.xp.div(1000) + 1
            skills.add(skill)
        }
        return skills
    }

    @Query("SELECT * FROM skills GROUP BY name")
    fun _findAllSkills(): List<Skill>

    @Query("SELECT SUM(xp) FROM tasks INNER JOIN skills ON tasks.id = skills.task_id WHERE skills.name = :name AND tasks.status = :status")
    fun calculateSkillXp(name: String, status: Status): Long

    @Query("SELECT COUNT(*) FROM tasks INNER JOIN skills ON tasks.id = skills.task_id WHERE skills.name = :name AND tasks.status = :status")
    fun findAmountOfTasks(name: String, status: Status): Long

    @Query("SELECT DISTINCT(category) FROM skills")
    fun findAllCategories(): List<String>

    // --- UPDATE

    @Transaction
    suspend fun updateSkill(name: String, skill: Skill): Skill {
        updateSkill(name, skill.name, skill.category, skill.iconId)
        return findSkill(skill.name)
    }

    @Query("UPDATE skills SET name = :newName, category = :category, icon_id = :iconId WHERE name = :oldName")
    suspend fun updateSkill(oldName: String, newName: String, category: String, iconId: Int)

    // --- DELETE

    @Query("DELETE FROM skills WHERE name = :name")
    suspend fun deleteSkill(name: String)

    @Query("DELETE FROM skills")
    suspend fun deleteAllSkills()
}