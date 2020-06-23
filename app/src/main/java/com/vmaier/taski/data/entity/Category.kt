package com.vmaier.taski.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vladas Maier
 * on 08.05.2020
 * at 20:31
 */
@Parcelize
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
    val name: String
) : Parcelable