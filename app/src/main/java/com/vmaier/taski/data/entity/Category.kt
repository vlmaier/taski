package com.vmaier.taski.data.entity

import androidx.room.*


/**
 * Created by Vladas Maier
 * on 08.05.2020
 * at 20:31
 */
@Entity(
    tableName = "categories",
    indices = [
        Index(
            value = ["id"],
            unique = true
        ),
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "color")
    var color: String? = null
) {
    @Ignore
    var isMenuShowed: Boolean = false
}