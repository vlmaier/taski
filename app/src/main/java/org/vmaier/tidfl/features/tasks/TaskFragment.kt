package org.vmaier.tidfl.features.tasks

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
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
import androidx.preference.PreferenceManager.*
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.vmaier.tidfl.App
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.entity.Task
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
        lateinit var skillNames: List<String>
        lateinit var difficulty: String
        lateinit var db: AppDatabase

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
        db = AppDatabase(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        val skills = db.skillDao().findAllSkills()
        skillNames = skills.map { it.name }
        return this.view
    }

    fun setTaskIcon(saved: Bundle?, button: ImageButton,
                    fallback: Int = Random.nextInt(App.iconPack.allIcons.size)) {

        val iconId = saved?.getInt(KEY_ICON_ID) ?: fallback
        val icon = App.iconPack.getIconDrawable(iconId, IconDrawableLoader(requireContext()))
        icon.setThemeTint(requireContext())
        button.background = icon
        button.tag = iconId
    }

    fun getSkillsTokenizer(): SpanChipTokenizer<ChipSpan> {
        return SpanChipTokenizer(requireContext(), object : ChipSpanChipCreator() {
            override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
                val skills = db.skillDao().findAllSkills()
                val skill = skills.find { it.name == text }
                var icon: Drawable? = null
                if (skill != null) {
                    icon = App.iconPack.getIconDrawable(skill.iconId, IconDrawableLoader(context))
                }
                if (icon != null) {
                    DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.colorWhite))
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
        return View.OnFocusChangeListener { _, b ->
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
            DatePickerDialog(requireContext(), dateSetListener,
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
            TimePickerDialog(requireContext(), timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true)
                    .show()
        }
    }

    fun addToCalendar(task: Task?) {

        val sharedPreferences = getDefaultSharedPreferences(requireContext())
        val isCalendarSyncOn = sharedPreferences.getBoolean("calendar_sync", false)
        if (!isCalendarSyncOn) return
        sharedPreferences.edit().putBoolean("calendar_sync", false).apply()
        if (task == null) return
        val calendarId = getCalendarId(requireContext()) ?: return
        val eventId: Uri?

        val startTimeMs = if (task.dueAt != null) {
            App.dateFormat.parse(task.dueAt).time
        } else {
            null
        }

        val event = ContentValues()
        event.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        event.put(CalendarContract.Events.TITLE, task.goal)
        event.put(CalendarContract.Events.DESCRIPTION, task.details)
        if (startTimeMs != null) {
            event.put(CalendarContract.Events.DTSTART, startTimeMs)
            event.put(CalendarContract.Events.DTEND, startTimeMs + task.duration * 60 * 1000)
        }
        val timeZone = TimeZone.getDefault().id
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
        val baseUri = Uri.parse("content://com.android.calendar/events")
        eventId = requireContext().contentResolver.insert(baseUri, event)
        GlobalScope.launch {
            db.taskDao().updateTaskEventId(task.id, eventId.toString())
        }
    }

    fun updateInCalendar(before: Task, after: Task?) {

        if (after == null) return
        val sharedPreferences = getDefaultSharedPreferences(requireContext())
        val isCalendarSyncOn = sharedPreferences.getBoolean("calendar_sync", false)
        if (!isCalendarSyncOn) return
        val eventId: Uri? = Uri.parse(after.eventId)
        if (eventId == null) {
            addToCalendar(after)
        } else {
            val calendarId = getCalendarId(requireContext()) ?: return
            val event = ContentValues()
            event.put(CalendarContract.Events.CALENDAR_ID, calendarId)
            if (before.goal != after.goal) {
                event.put(CalendarContract.Events.TITLE, after.goal)
            }
            if (before.details != after.details) {
                event.put(CalendarContract.Events.DESCRIPTION, after.details)
            }
            if (before.duration != after.duration ||
                before.dueAt != after.dueAt) {
                val startTimeMs = if (after.dueAt != null) {
                    App.dateFormat.parse(after.dueAt).time
                } else {
                    null
                }
                if (startTimeMs != null) {
                    event.put(CalendarContract.Events.DTSTART, startTimeMs)
                    event.put(CalendarContract.Events.DTEND, startTimeMs + before.duration * 60 * 1000)
                }
                val timeZone = TimeZone.getDefault().id
                event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
            }
            requireContext().contentResolver.update(eventId, event, null, null)
            GlobalScope.launch {
                db.taskDao().updateTaskEventId(before.id, eventId.toString())
            }
        }
    }

    private fun getCalendarId(context: Context) : Long? {

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)

        // check permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        // granted

        var cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + " = 1",
            null,
            CalendarContract.Calendars._ID + " ASC"
        )

        if (cursor != null && cursor.count <= 0) {
            cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                CalendarContract.Calendars._ID + " ASC"
            )
        }

        if (cursor != null && cursor.moveToFirst()) {
            val calId: String
            val idCol = cursor.getColumnIndex(projection[0])
            calId = cursor.getString(idCol)

            cursor.close()
            return calId.toLong()
        }

        return null
    }
}