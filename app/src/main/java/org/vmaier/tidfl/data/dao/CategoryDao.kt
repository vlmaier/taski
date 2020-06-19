package org.vmaier.tidfl.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.vmaier.tidfl.data.entity.Category


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
        WHERE name = :name
    """
    )
    fun findByName(name: String): Category?

    @Query(
        """
        SELECT *
        FROM categories
    """
    )
    fun findAll(): List<Category>

    @Query(
        """
        SELECT SUM(xp_value)
        FROM assigned_skills 
        INNER JOIN tasks
          ON task_id = tasks.id
        INNER JOIN skills
          ON skill_id = skills.id
        WHERE category_id = :categoryId
          AND status = 'done'
    """
    )
    fun countCategoryXpValue(categoryId: Long): Long

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //


    // ------------------------------------- DELETE QUERIES ------------------------------------- //

}