package com.vmaier.taski.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.vmaier.taski.App
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.features.tasks.TaskFragment
import timber.log.Timber
import java.text.ParseException
import java.util.*


/**
 * Created by Vladas Maier
 * on 08/08/2020
 * at 12:50
 */
class CalendarService(val context: Context) {

    val db = AppDatabase(context)

    @SuppressLint("MissingPermission")
    fun addToCalendar(isCalendarSyncOn: Boolean, task: Task?) {
        if (!isCalendarSyncOn) return
        if (task == null) return
        val calendarId = getCalendarId() ?: return
        Timber.d("Picked calendar with ID $calendarId.")
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
        eventId = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, event)
        Timber.d("Created new event in calendar.")
        Timber.d("Event ID: $eventId")
        TaskFragment.db.taskDao().updateEventId(task.id, eventId.toString())
    }

    fun updateInCalendar(isCalendarSyncOn: Boolean, before: Task, after: Task?) {
        if (after == null) return
        if (!isCalendarSyncOn) return
        if (after.eventId == null) {
            addToCalendar(isCalendarSyncOn, after)
            return
        }
        val eventId: Uri? = Uri.parse(after.eventId)
        if (eventId != null) {
            val calendarId = getCalendarId() ?: return
            Timber.d("Picked calendar with ID $calendarId.")
            val event = updateEvent(calendarId, before, after)
            context.contentResolver.update(eventId, event, null, null)
            Timber.d("Updated event in calendar.")
            Timber.d("Event ID: $eventId")
            TaskFragment.db.taskDao().updateEventId(before.id, eventId.toString())
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

    fun deleteCalendarEvent(task: Task) {
        val eventId: Uri? = Uri.parse(task.eventId)
        if (eventId != null) {
            val calendarId = getCalendarId() ?: return
            Timber.d("Picked calendar with ID $calendarId.")
            context.contentResolver.delete(eventId, null, null)
            Timber.d("Deleted event in calendar.")
            Timber.d("Event ID: $eventId")
            db.taskDao().updateEventId(task.id, null)
        }
    }

    private fun getCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        // check permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
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