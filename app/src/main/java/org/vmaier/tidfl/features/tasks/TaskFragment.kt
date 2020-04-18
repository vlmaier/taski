package org.vmaier.tidfl.features.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.util.getDurationInMinutes
import org.vmaier.tidfl.util.getHumanReadableValue
import org.vmaier.tidfl.util.hideKeyboard
import org.vmaier.tidfl.util.setThemeTint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 15/02/2020.
 * at 15:22
 */
open class TaskFragment : Fragment() {

    companion object {
        lateinit var cntxt: Context
        lateinit var dbHandler: DatabaseHandler
        lateinit var skillNames: List<String>
        lateinit var difficulty: String

        const val KEY_GOAL = "goal"
        const val KEY_DETAILS = "details"
        const val KEY_DIFFICULTY = "difficulty"
        const val KEY_DURATION = "duration"
        const val KEY_SKILLS = "skills"
        const val KEY_ICON_ID = "icon_id"
        const val KEY_DEADLINE_DATE = "deadline_date"
        const val KEY_DEADLINE_TIME = "deadline_time"

        fun setIcon(context: Context, icon: Icon, button: ImageButton) {
            val drawable = IconDrawableLoader(context).loadDrawable(icon)
            drawable.setThemeTint(context)
            button.background = drawable
            button.tag = icon.id
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cntxt = context
        dbHandler = DatabaseHandler(cntxt)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        skillNames = dbHandler.findAllSkillNames()
        return this.view
    }

    fun setTaskIcon(saved: Bundle?, button: ImageButton,
                    fallback: Int = Random.nextInt(App.iconPack.allIcons.size)) {

        val iconId = saved?.getInt(KEY_ICON_ID) ?: fallback
        val icon = App.iconPack.getIconDrawable(iconId, IconDrawableLoader(cntxt))
        icon.setThemeTint(cntxt)
        button.background = icon
        button.tag = iconId
    }

    fun getSkillsTokenizer(): SpanChipTokenizer<ChipSpan> {
        return SpanChipTokenizer(cntxt, object : ChipSpanChipCreator() {
            override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
                val skills = dbHandler.findAllSkills()
                val skill = skills.find { it.name == text }
                var icon: Drawable? = null
                if (skill != null) {
                    icon = App.iconPack.getIconDrawable(skill.iconId, IconDrawableLoader(cntxt))
                }
                if (icon != null) {
                    DrawableCompat.setTint(icon, ContextCompat.getColor(cntxt, R.color.colorWhite))
                }
                return ChipSpan(context, text, icon, data)
            }

            override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
                super.configureChip(chip, chipConfiguration)
                chip.setShowIconOnLeft(true)
            }
        }, ChipSpan::class.java)
    }

    fun getSkillsRestrictor(skills: NachoTextView): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { view, b ->
            val allChips = skills.allChips
            val chipList: MutableList<ChipInfo> = arrayListOf()
            for (chip in allChips) {
                if (skillNames.contains(chip.text) &&
                        chipList.find { it.text == chip.text } == null
                ) {
                    chipList.add(ChipInfo(chip.text, chip.data))
                }
            }
            skills.setTextWithChips(chipList)
        }
    }

    fun getDurationBarListener(durationValue: TextView, xpGainValue: TextView, durationBar: SeekBar
    ): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                durationValue.text = seek.getHumanReadableValue()
                if (progress <= 1) {
                    seek.progress = 1
                }
                updateXpGained(xpGainValue, durationBar)
            }

            override fun onStartTrackingTouch(seek: SeekBar) = Unit
            override fun onStopTrackingTouch(seek: SeekBar) = Unit
        }
    }

    fun updateXpGained(xpGainValue: TextView, durationBar: SeekBar) {
        xpGainValue.text = "${Difficulty.valueOf(difficulty).factor.times(
                durationBar.getDurationInMinutes()).toInt()} XP"
    }

    fun setDeadlineDateOnClickListener(view: EditText) {
        view.setOnClickListener {
            view.hideKeyboard()
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                view.setText(SimpleDateFormat(
                        App.dateFormat.toPattern().split(" ")[0],
                        Locale.GERMAN).format(cal.time))
            }
            DatePickerDialog(cntxt, dateSetListener,
                    cal[Calendar.YEAR],
                    cal[Calendar.MONTH],
                    cal[Calendar.DAY_OF_MONTH])
                    .show()
        }
    }

    fun setDeadlineTimeOnClickListener(view: EditText) {
        view.setOnClickListener {
            it.hideKeyboard()
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                view.setText(SimpleDateFormat(
                        App.dateFormat.toPattern().split(" ")[1],
                        Locale.GERMAN).format(cal.time))
            }
            TimePickerDialog(cntxt, timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true)
                    .show()
        }

    }
}