package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.SET_NULL
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
@Parcelize
@Entity(
    tableName = "skills",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = [ "id" ],
            childColumns = [ "category_id" ],
            onDelete = SET_NULL
        )
    ],
    indices = [
        Index(
            value = [ "id" ],
            unique = true
        ),
        Index(
            value = [ "name" ],
            unique = true
        )
    ]
)
data class Skill(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,

    @ColumnInfo(name = "icon_id")
    val iconId: Int
) : Parcelable