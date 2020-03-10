package org.vmaier.tidfl.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
@Parcelize
data class Skill(
    val id: Long,
    val name: String,
    val category: String,
    val iconId: Int
) : Parcelable