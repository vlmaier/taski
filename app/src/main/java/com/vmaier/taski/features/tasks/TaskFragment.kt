package com.vmaier.taski.features.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.vmaier.taski.*
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.services.CalendarService
import com.vmaier.taski.services.NotificationService
import com.vmaier.taski.utils.Utils
import java.util.*
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 15/02/2020
 * at 15:22
 */
open class TaskFragment : Fragment() {

    lateinit var prefs: SharedPreferences
    lateinit var calendarService: CalendarService

    companion object {
        lateinit var skillNames: List<String>
        lateinit var difficulty: String
        lateinit var db: AppDatabase

        var notificationService = NotificationService()

        const val KEY_GOAL = "goal"
        const val KEY_DETAILS = "details"
        const val KEY_DIFFICULTY = "difficulty"
        const val KEY_DURATION = "duration"
        const val KEY_SKILLS = "skills"
        const val KEY_ICON_ID = "icon_id"
        const val KEY_DEADLINE_DATE = "deadline_date"
        const val KEY_DEADLINE_TIME = "deadline_time"

        fun setIcon(context: Context, icon: Icon, button: ImageButton) {
            IconDrawableLoader(context).loadDrawable(icon)
            icon.drawable?.clearColorFilter()
            button.background = icon.drawable
            button.tag = icon.id
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = AppDatabase(context)
        prefs = getDefaultSharedPreferences(context)
        calendarService = CalendarService(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_tasks)
        toggleBottomMenu(false, View.GONE)
        val skills = db.skillDao().findAll()
        skillNames = skills.map { it.name }
        return this.view
    }

    fun setTaskIcon(saved: Bundle?, button: ImageButton, fallback: Int = Random.nextInt(App.iconPack.allIcons.size)) {
        val iconId = saved?.getInt(KEY_ICON_ID) ?: fallback
        val icon = App.iconPack.getIconDrawable(iconId, IconDrawableLoader(requireContext()))
        icon?.clearColorFilter()
        button.background = icon
        button.tag = iconId
    }

    fun getSkillsTokenizer(): SpanChipTokenizer<ChipSpan> {
        return SpanChipTokenizer(requireContext(), object : ChipSpanChipCreator() {
            override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
                val skills = db.skillDao().findAll()
                val skill = skills.find { it.name == text }
                var icon: Drawable? = null
                if (skill != null) {
                    icon = App.iconPack.getIconDrawable(skill.iconId, IconDrawableLoader(context))
                }
                if (icon != null) {
                    DrawableCompat.setTint(icon, Color.WHITE)
                }
                return ChipSpan(context, text, icon, data)
            }

            override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
                super.configureChip(chip, chipConfiguration)
                chip.setShowIconOnLeft(true)
                chip.setBackgroundColor(
                    ColorStateList.valueOf(Utils.getThemeColor(requireContext(), R.attr.colorControlHighlight))
                )
                chip.setTextColor(Utils.getThemeColor(requireContext(), R.attr.colorOnSurface))
                chip.setIconBackgroundColor(Utils.getThemeColor(requireContext(), R.attr.colorSecondary))
            }
        }, ChipSpan::class.java)
    }

    fun getSkillsRestrictor(skills: NachoTextView): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, _ ->
            val allChips = skills.allChips
            val chipList: MutableList<ChipInfo> = arrayListOf()
            for (chip in allChips) {
                if (skillNames.contains(chip.text) && chipList.find { it.text == chip.text } == null) {
                    chipList.add(ChipInfo(chip.text, chip.data))
                }
            }
            skills.setTextWithChips(chipList)
        }
    }

    fun getDurationBarListener(
        durationValue: TextView, xpGain: TextView, durationBar: SeekBar
    ): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                durationValue.text = seek.getHumanReadableValue()
                // do not allow the seekbar going beyond 1
                if (progress <= 1) seek.progress = 1
                updateXpGain(xpGain, durationBar)
            }

            override fun onStartTrackingTouch(seek: SeekBar) = Unit
            override fun onStopTrackingTouch(seek: SeekBar) = Unit
        }
    }

    fun updateXpGain(xpGain: TextView, durationBar: SeekBar) {
        val xp = Utils.calculateXp(Difficulty.valueOf(difficulty), durationBar.getDurationInMinutes())
        xpGain.text = resources.getString(R.string.term_xp_value, xp)
    }

    fun setDeadlineDateOnClickListener(view: EditText?) {
        view?.setOnClickListener {
            view.hideKeyboard()
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                view.setText(calendar.time.getDateInAppFormat())
            }
            DatePickerDialog(
                requireContext(), dateSetListener,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    fun setDeadlineDateOnTextChangedListener(calendarSync: CheckBox, view: EditText?) {
        view?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val isCalendarSyncOn = prefs.getBoolean(Const.Prefs.CALENDAR_SYNC, false)
                    calendarSync.isChecked = isCalendarSyncOn
                    calendarSync.isEnabled = true
                } else {
                    calendarSync.isChecked = false
                    calendarSync.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // not used
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // not used
            }
        })
    }

    fun setDeadlineTimeOnClickListener(view: EditText?) {
        view?.setOnClickListener {
            it.hideKeyboard()
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                view.setText(calendar.time.getTimeInAppFormat())
            }
            TimePickerDialog(
                requireContext(), timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}