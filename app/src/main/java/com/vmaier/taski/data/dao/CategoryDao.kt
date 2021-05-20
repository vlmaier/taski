package com.vmaier.taski.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    suspend fun create(category: Category): Long

    // -------------------------------------  READ QUERIES  ------------------------------------- //

    @Query(
        """
        SELECT *
        FROM categories
        WHERE id = :id
    """
    )
    fun get(id: Long): Category?

    @Query(
        """
        SELECT *
        FROM categories
        WHERE name = :name COLLATE NOCASE
    """
    )
    fun get(name: String): Category?

    @Query(
        """
        SELECT *
        FROM categories
    """
    )
    fun getAll(): MutableList<Category>

    @Query(
        """
        SELECT name
        FROM categories
        WHERE id = :id
    """
    )
    fun getNameById(id: Long): String?

    @Query(
        """
        SELECT SUM(skills.xp_value)
        FROM skills
        WHERE category_id = :id
    """
    )
    fun countXP(id: Long): Long

    @Query(
        """
        SELECT COUNT(*)
        FROM skills
        WHERE category_id = :id
    """
    )
    fun countSkills(id: Long): Int

    // ------------------------------------- UPDATE QUERIES ------------------------------------- //

    @Query(
        """
        UPDATE categories
        SET name = :name
        WHERE id = :id
    """
    )
    suspend fun updateName(id: Long, name: String)

    @Query(
        """
        UPDATE categories
        SET color = :color
        WHERE id = :id
    """
    )
    suspend fun updateColor(id: Long, color: String?)

    // ------------------------------------- DELETE QUERIES ------------------------------------- //

    @Query(
        """
        DELETE FROM categories
        WHERE id = :id
    """
    )
    suspend fun delete(id: Long)
}