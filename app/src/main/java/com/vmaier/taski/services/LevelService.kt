package com.vmaier.taski.services

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.animation.AnticipateOvershootInterpolator
import androidx.appcompat.app.AlertDialog
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import com.vmaier.taski.App
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.setIcon
import com.vmaier.taski.utils.Utils
import kotlin.math.pow


/**
 * Created by Vladas Maier
 * on 29/07/2020
 * at 15:50
 */
class LevelService(val context: Context) {

    val db = AppDatabase(context)

    fun checkForSkillLevelUp(skill: Skill, xp: Int) {
        val previousLevel = getSkillLevel(skill)
        val nextLevel = calculateLevel(skill.xp + xp)
        if (previousLevel != nextLevel) {
            Handler().postDelayed({
                val title = context.getString(R.string.term_skill_level_increased, skill.name)
                showDialog(title, previousLevel, nextLevel, skill.iconId)
            }, 1000)
        }
    }

    fun checkForOverallLevelUp(xpValue: Int) {
        val overallXpValue = db.taskDao().countOverallXp()
        val previousLevel = getOverallLevel(overallXpValue)
        val nextLevel = calculateLevel(overallXpValue + xpValue)
        if (previousLevel != nextLevel) {
            Handler().postDelayed({
                val title = context.getString(R.string.term_overall_level_increased)
                showDialog(title, previousLevel, nextLevel)
            }, 1000)
        }
    }

    private fun showDialog(title: String, previousLevel: Int, nextLevel: Int, iconId: Int? = null) {
        val dialogView = (context as Activity).layoutInflater
            .inflate(R.layout.level_up_dialog, null)
        val ticker: TickerView = dialogView.findViewById(R.id.ticker)
        ticker.animationInterpolator = AnticipateOvershootInterpolator()
        ticker.setCharacterLists(TickerUtils.provideNumberList())
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.action_ok)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        if (iconId != null) {
            val drawable = App.iconPack.getIcon(iconId)?.drawable
            drawable?.setTint(Utils.getThemeColor(context, R.attr.colorOnSurface))
            builder.setIcon(drawable)
        }
        val alertDialog = builder.create()
        ticker.text = "$previousLevel"
        alertDialog.show()
        for (level in previousLevel..nextLevel) {
            ticker.text = "$level"
        }
    }

    internal fun getOverallLevel(
        xp: Long = db.taskDao().countOverallXp()): Int {
        return calculateLevel(xp)
    }

    internal fun getSkillLevel(skill: Skill): Int {
        return calculateLevel(skill.xp)
    }

    private fun calculateLevel(xp: Long): Int {
        /* +-------+------------+
           | Level | Needed XP  |
           +-------+------------+
           |     1 |      1.600 |
           |     2 |      6.400 |
           |     3 |     14.400 |
           |     4 |     25.600 |
           |     5 |     40.000 |
           |   ... |        ... |
           |    10 |    160.000 |
           |   ... |        ... |
           |    15 |    360.000 |
           |   ... |        ... |
           |    25 |  1.000.000 |
           |   ... |        ... |
           |    50 |  4.000.000 |
           |   ... |        ... |
           |    75 |  9.000.000 |
           |   ... |        ... |
           |   100 | 16.000.000 |
           +-------+------------+ */
        return 0.025.times(xp.toDouble().pow(1.div(2.0))).toInt()
    }
}