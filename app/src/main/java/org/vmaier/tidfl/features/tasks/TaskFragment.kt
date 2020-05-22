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
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.utils.*
import java.text.ParseException
import java.util.*
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 15/02/2020
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_tasks)
        MainActivity.bottomNav.visibility = View.GONE
        val skills = db.skillDao().findAll()
        skillNames = skills.map { it.name }
        return this.view
    }

    fun setTaskIcon(
        saved: Bundle?, button: ImageButton,
        fallback: Int = Random.nextInt(App.iconPack.allIcons.size)
    ) {
        val iconId = saved?.getInt(KEY_ICON_ID) ?: fallback
        val icon = App.iconPack.getIconDrawable(iconId, IconDrawableLoader(requireContext()))
        icon.setThemeTint(requireContext())
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
                    DrawableCompat.setTint(
                        icon, ContextCompat.getColor(context, R.color.colorWhite)
                    )
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
        return View.OnFocusChangeListener { _, _ ->
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

    fun getDurationBarListener(
        durationValue: TextView, xpGainValue: TextView, durationBar: SeekBar
    ): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                durationValue.text = seek.getHumanReadableValue()
                // do not allow the seek bar going beyond 1
                if (progress <= 1) seek.progress = 1
                updateXpGain(xpGainValue, durationBar)
            }

            override fun onStartTrackingTouch(seek: SeekBar) = Unit
            override fun onStopTrackingTouch(seek: SeekBar) = Unit
        }
    }

    fun updateXpGain(xpGainValue: TextView, durationBar: SeekBar) {
        val xpValue = Difficulty.valueOf(difficulty)
            .factor.times(durationBar.getDurationInMinutes()).toInt()
        xpGainValue.text = resources.getString(R.string.term_xp_value, xpValue)
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

    fun addToCalendar(task: Task?) {
        val sharedPrefs = getDefaultSharedPreferences(requireContext())
        val isCalendarSyncOn = sharedPrefs.getBoolean(Const.Prefs.CALENDAR_SYNC, false)
        if (!isCalendarSyncOn) return
        if (task == null) return
        val calendarId = getCalendarId(requireContext()) ?: return
        val eventId: Uri?
        val startTimeMs = if (task.dueAt != null) {
            try {
                App.dateFormat.parse(task.dueAt)?.time
            } catch (e: ParseException) {
                null
            }
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
        eventId = requireContext()
            .contentResolver.insert(CalendarContract.Events.CONTENT_URI, event)
        db.taskDao().updateEventId(task.id, eventId.toString())
    }

    fun updateInCalendar(before: Task, after: Task?) {
        if (after == null) return
        val sharedPrefs = getDefaultSharedPreferences(requireContext())
        val isCalendarSyncOn = sharedPrefs.getBoolean(Const.Prefs.CALENDAR_SYNC, false)
        if (!isCalendarSyncOn) return
        if (after.eventId == null) {
            addToCalendar(after)
            return
        }
        val eventId: Uri? = Uri.parse(after.eventId)
        if (eventId != null) {
            val calendarId = getCalendarId(requireContext()) ?: return
            val event = updateEvent(calendarId, before, after)
            requireContext().contentResolver.update(eventId, event, null, null)
            db.taskDao().updateEventId(before.id, eventId.toString())
        }
    }

    private fun updateEvent(calendarId: Long, before: Task, after: Task): ContentValues {
        val event = ContentValues()
        event.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        if (before.goal != after.goal) {
            event.put(CalendarContract.Events.TITLE, after.goal)
        }
        if (before.details != after.details) {
            event.put(CalendarContract.Events.DESCRIPTION, after.details)
        }
        if (before.duration != after.duration || before.dueAt != after.dueAt) {
            val startTimeMs = if (after.dueAt != null) {
                try {
                    App.dateFormat.parse(after.dueAt)?.time
                } catch (e: ParseException) {
                    null
                }
            } else {
                null
            }
            if (startTimeMs != null) {
                event.put(CalendarContract.Events.DTSTART, startTimeMs)
                event.put(
                    CalendarContract.Events.DTEND,
                    startTimeMs + before.duration * 60 * 1000
                )
            }
            val timeZone = TimeZone.getDefault().id
            event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
        }
        return event
    }

    private fun getCalendarId(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        // check permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        var cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            CalendarContract.Calendars.VISIBLE + " = 1 AND " +
                    CalendarContract.Calendars.IS_PRIMARY + " = 1",
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