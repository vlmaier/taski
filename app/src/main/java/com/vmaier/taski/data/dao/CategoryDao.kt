package com.vmaier.taski.data.dao

import androidx.room.*
import com.vmaier.taski.data.entity.Category


/**
 * Created by Vladas Maier
 * on 08.05.2020
 * at 23:04
 */
@Dao
interface CategoryDao {

    // ------------------------------------- CREATE QUERIES ------------------------------------- //

    @Insert(entity = Category::class, onConflict = OnConflictStrategy.IGNORE)
    fun create(category: Category): Long

    // -------------------------------------  READ QUERIES  ------------------------------------- //

    @Query(
        """
        SELECT *
        FROM categories
        WHERE id = :categoryId
    """
    )
    fun findById(categoryId: Long): Category

    @Query(
        """
        SELECT name
        FROM categories
        WHERE id = :categoryId
    """
    )
    fun findNameById(categoryId: Long): String

    @Query(
        """
        SELECT *
        FROM categories
        WHERE name = :name COLLATE NOCASE
    """
    )
    fun findByName(name: String): Category?

    @Query(
        """
        SELECT *
        FROM categories
    """
    )
    fun findAll(): MutableList<Category>

    @Query(
        """
        SELECT SUM(tasks.xp_value * tasks.count_done)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        INNER JOIN skills
          ON skill_id = skills.id
        WHERE category_id = :categoryId
    """
    )
    fun countCategoryXp(categoryId: Long): Long

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Query(
        """
        UPDATE categories
        SET name = :name
        WHERE id = :categoryId
    """
    )
    fun updateName(categoryId: Long, name: String)

    @Query(
        """
        UPDATE categories
        SET color = :color
        WHERE id = :categoryId
    """
    )
    fun updateColor(categoryId: Long, color: String?)

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Delete
    fun delete(category: Category)
}