package org.vmaier.tidfl.data

/**
 * Created by Vladas Maier
 * on 22/01/2020.
 * at 20:43
 */
sealed class Status(val name : String) {
    object OPEN   : Status("open")
    object DONE   : Status("done")
    object FAILED : Status("failed")
}