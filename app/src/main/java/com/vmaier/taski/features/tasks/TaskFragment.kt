package com.vmaier.taski.features.tasks

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.appcompat.app.AlertDialog
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
import com.vmaier.taski.*
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.Difficulty
import com.vmaier.taski.data.DurationUnit
import com.vmaier.taski.services.CalendarService
import com.vmaier.taski.services.NotificationService
import com.vmaier.taski.services.PreferenceService
import com.vmaier.taski.utils.Utils
import java.util.*
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 15.02.2020
 * at 15:22
 */
open class TaskFragment : Fragment() {

    lateinit var db: AppDatabase
    lateinit var calendarService: CalendarService

    companion object {
        lateinit var skillNames: List<String>
        lateinit var difficulty: String

        var durationValue: Int = 15
        var durationUnit: DurationUnit = DurationUnit.MINUTE

        var notificationService = NotificationService()

        const val KEY_GOAL = "goal"
        const val KEY_DETAILS = "details"
        const val KEY_DIFFICULTY = "difficulty"
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

        fun isDifficultyInitialized() = ::difficulty.isInitialized
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = AppDatabase(context)
        calendarService = CalendarService(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_tasks)
        toggleBottomMenu(false, View.GONE)
        val skills = db.skillDao().findAll()
        skillNames = skills.map { it.name }
        return this.view
    }

    fun setTaskIcon(
        saved: Bundle?,
        button: ImageButton,
        fallback: Int = Random.nextInt(App.iconPack.allIcons.size)
    ) {
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
                    ColorStateList.valueOf(
                        Utils.getThemeColor(
                            requireContext(),
                            R.attr.colorControlHighlight
                        )
                    )
                )
                chip.setTextColor(Utils.getThemeColor(requireContext(), R.attr.colorOnSurface))
                chip.setIconBackgroundColor(
                    Utils.getThemeColor(
                        requireContext(),
                        R.attr.colorSecondary
                    )
                )
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

    fun updateXpGain(xpGain: TextView) {
        val xp = Utils.calculateXp(Difficulty.valueOf(difficulty), getDurationInMinutes())
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
                    val prefService = PreferenceService(requireContext())
                    val isCalendarSyncOn = prefService.isCalendarSyncEnabled()
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

    fun showDurationPickerDialog(durationButton: Button, xpGain: TextView) {
        val minutes = IntRange(0, 60).step(5).toMutableList()
        minutes.remove(0)
        val hours = IntRange(1, 24).toList()
        val days = IntRange(1, 7).toList()
        val weeks = IntRange(1, 52).toList()
        val years = IntRange(1, 10).toList()

        val dialogView =
            (context as Activity).layoutInflater.inflate(R.layout.duration_picker_dialog, null)
        val durationValuePicker = dialogView.findViewById(R.id.duration_value) as NumberPicker
        val durationUnitPicker = dialogView.findViewById(R.id.duration_unit) as NumberPicker

        var durationUnitPickerValue = 1
        var array: List<Int> = when (durationUnit) {
            DurationUnit.MINUTE -> {
                durationUnitPickerValue = 1
                minutes
            }
            DurationUnit.HOUR -> {
                durationUnitPickerValue = 2
                hours
            }
            DurationUnit.DAY -> {
                durationUnitPickerValue = 3
                days
            }
            DurationUnit.WEEK -> {
                durationUnitPickerValue = 4
                weeks
            }
            DurationUnit.YEAR -> {
                durationUnitPickerValue = 5
                years
            }
        }

        durationValuePicker.wrapSelectorWheel = false
        durationValuePicker.minValue = 1
        durationValuePicker.maxValue = array.size
        durationValuePicker.value = array.indexOf(durationValue) + 1
        durationValuePicker.displayedValues = array.map { it.toString() }.toTypedArray()

        durationUnitPicker.wrapSelectorWheel = false
        durationUnitPicker.minValue = 1
        updateDurationUnitValue(durationUnitPicker, durationValue)
        durationUnitPicker.value = durationUnitPickerValue

        durationUnitPicker.setOnValueChangedListener { _, _, newVal ->
            durationValuePicker.displayedValues = null
            when (newVal) {
                1 -> array = minutes
                2 -> array = hours
                3 -> array = days
                4 -> array = weeks
                5 -> array = years
            }
            durationValuePicker.maxValue = array.size
            durationValuePicker.displayedValues = array.map { it.toString() }.toTypedArray()
            durationValuePicker.value = 1
            updateDurationUnitValue(durationUnitPicker, array[0])
        }

        durationValuePicker.setOnValueChangedListener { _, _, newVal ->
            var value = 0
            when (durationUnitPicker.value) {
                1 -> value = minutes[newVal - 1]
                2 -> value = hours[newVal - 1]
                3 -> value = days[newVal - 1]
                4 -> value = weeks[newVal - 1]
                5 -> value = years[newVal - 1]
            }
            updateDurationUnitValue(durationUnitPicker, value)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.pick_task_duration))
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.action_set)) { _, _ ->
                durationUnit = durationUnitPicker.getDurationUnit()
                durationButton.text = when (durationUnitPicker.value) {
                    1 -> {
                        durationValue = minutes[durationValuePicker.value - 1]
                        resources.getQuantityString(
                            R.plurals.duration_minute,
                            durationValue,
                            durationValue
                        )
                    }
                    2 -> {
                        durationValue = hours[durationValuePicker.value - 1]
                        resources.getQuantityString(
                            R.plurals.duration_hour,
                            durationValue,
                            durationValue
                        )
                    }
                    3 -> {
                        durationValue = days[durationValuePicker.value - 1]
                        resources.getQuantityString(
                            R.plurals.duration_day,
                            durationValue,
                            durationValue
                        )
                    }
                    4 -> {
                        durationValue = weeks[durationValuePicker.value - 1]
                        resources.getQuantityString(
                            R.plurals.duration_week,
                            durationValue,
                            durationValue
                        )
                    }
                    5 -> {
                        durationValue = years[durationValuePicker.value - 1]
                        resources.getQuantityString(
                            R.plurals.duration_year,
                            durationValue,
                            durationValue
                        )
                    }
                    else -> {
                        resources.getQuantityString(
                            R.plurals.duration_hour,
                            durationValue,
                            durationValue
                        )
                    }
                }
                updateXpGain(xpGain)
            }
            .setNegativeButton(getString(R.string.action_cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun getHumanReadableDuration(): String {
        return when (durationUnit) {
            DurationUnit.MINUTE -> resources.getQuantityString(
                R.plurals.duration_minute,
                durationValue,
                durationValue
            )
            DurationUnit.HOUR -> resources.getQuantityString(
                R.plurals.duration_hour,
                durationValue,
                durationValue
            )
            DurationUnit.DAY -> resources.getQuantityString(
                R.plurals.duration_day,
                durationValue,
                durationValue
            )
            DurationUnit.WEEK -> resources.getQuantityString(
                R.plurals.duration_week,
                durationValue,
                durationValue
            )
            DurationUnit.YEAR -> resources.getQuantityString(
                R.plurals.duration_year,
                durationValue,
                durationValue
            )
        }
    }

    fun getDurationInMinutes(): Int {
        return when (durationUnit) {
            DurationUnit.MINUTE -> durationValue
            DurationUnit.HOUR -> durationValue.times(60)
            DurationUnit.DAY -> durationValue.times(60).times(24)
            DurationUnit.WEEK -> durationValue.times(60).times(24).times(7)
            DurationUnit.YEAR -> durationValue.times(60).times(24).times(7).times(52)
        }
    }

    private fun updateDurationUnitValue(picker: NumberPicker, value: Int) {
        val min =
            resources.getQuantityString(R.plurals.duration_minute, value, value).substringAfter(" ")
        val h =
            resources.getQuantityString(R.plurals.duration_hour, value, value).substringAfter(" ")
        val d =
            resources.getQuantityString(R.plurals.duration_day, value, value).substringAfter(" ")
        val w =
            resources.getQuantityString(R.plurals.duration_week, value, value).substringAfter(" ")
        val a =
            resources.getQuantityString(R.plurals.duration_year, value, value).substringAfter(" ")
        val values = arrayOf(min, h, d, w, a)
        picker.displayedValues = values
        picker.maxValue = values.size
    }
}