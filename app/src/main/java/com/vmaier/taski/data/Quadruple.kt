package com.vmaier.taski.data


/**
 * Created by Vladas Maier
 * on 24.01.2021
 * at 15:41
 */
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}