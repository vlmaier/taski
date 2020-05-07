package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
@Parcelize
@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "icon_id") val iconId: Int,
    @ColumnInfo(name = "task_id") var taskId: Long? = null
) : Parcelable {

    @Ignore var xp: Long = 0
    @Ignore var level: Long = 0
}