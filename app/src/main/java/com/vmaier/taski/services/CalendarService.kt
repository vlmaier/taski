package com.vmaier.taski.services

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.core.content.ContextCompat.checkSelfPermission
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.data.repository.TaskRepository
import timber.log.Timber
import java.util.*
import android.provider.CalendarContract as Calendar
import android.provider.CalendarContract.Calendars as Calendars


/**
 * Created by Vladas Maier
 * on 08.08.2020
 * at 12:50
 */
class CalendarService(val context: Context) {

    val taskRepository = TaskRepository(context)

    fun addToCalendar(isCalendarSyncOn: Boolean, task: Task?) {
        if (!isCalendarSyncOn) return
        if (task == null) return
        val calendarId = getCalendarId() ?: return
        val event = createEvent(calendarId, task)
        try {
            val eventId = context.contentResolver.insert(Calendar.Events.CONTENT_URI, event)
            Timber.d("Created new event ($eventId) in calendar.")
            taskRepository.updateEventId(task.id, eventId.toString())
        } catch (e: IllegalArgumentException) {
            Timber.e("Could not create event in calendar.")
            Timber.d(e)
        }
    }

    fun updateInCalendar(isCalendarSyncOn: Boolean, before: Task, after: Task?) {
        if (after == null) return
        if (!isCalendarSyncOn) {
            // calendar synchronization gets deactivated -> delete event
            if (after.eventId != null) {
                deleteFromCalendar(after)
                return
            } else {
                return
            }
        }
        // calendar synchronization gets activated -> add new event
        if (after.eventId == null) {
            addToCalendar(isCalendarSyncOn, after)
            return
        }
        val eventId: Uri? = Uri.parse(after.eventId)
        // update existing event
        if (eventId != null) {
            val calendarId = getCalendarId() ?: return
            val event = updateEvent(calendarId, before, after)
            try {
                context.contentResolver.update(eventId, event, null, null)
                Timber.d("Updated event ($eventId) in calendar.")
                taskRepository.updateEventId(before.id, eventId.toString())
            } catch (e: IllegalArgumentException) {
                Timber.e("Could not update event ($eventId) in calendar.")
                Timber.d(e)
            }
        }
    }

    fun deleteFromCalendar(task: Task) {
        val eventId: Uri? = Uri.parse(task.eventId)
        if (eventId != null) {
            try {
                context.contentResolver.delete(eventId, null, null)
                Timber.d("Deleted event ($eventId) from calendar.")
                taskRepository.updateEventId(task.id, null)
            } catch (e: IllegalArgumentException) {
                Timber.e("Could not delete event ($eventId) from calendar.")
                Timber.d(e)
            }
        }
    }

    private fun createEvent(calendarId: Long, task: Task): ContentValues {
        val event = ContentValues()
        event.put(Calendar.Events.CALENDAR_ID, calendarId)
        event.put(Calendar.Events.TITLE, task.goal)
        event.put(Calendar.Events.DESCRIPTION, task.details)
        val startTimeMs = task.dueAt
        if (startTimeMs != null) {
            val endTimeMs = task.doneAt
            event.put(Calendar.Events.DTSTART, startTimeMs)
            event.put(Calendar.Events.DTEND, endTimeMs)
        }
        if (task.rrule != null) {
            event.put(Calendar.Events.RRULE, task.calendarRRule)
        }
        val timeZone = TimeZone.getDefault().id
        event.put(Calendar.Events.EVENT_TIMEZONE, timeZone)
        return event
    }

    private fun updateEvent(calendarId: Long, before: Task, after: Task): ContentValues {
        val event = ContentValues()
        event.put(Calendar.Events.CALENDAR_ID, calendarId)
        if (before.goal != after.goal) {
            event.put(Calendar.Events.TITLE, after.goal)
        }
        if (before.details != after.details) {
            event.put(Calendar.Events.DESCRIPTION, after.details)
        }
        if (before.duration != after.duration || before.dueAt != after.dueAt) {
            val startTimeMs = after.dueAt
            if (startTimeMs != null) {
                val endTimeMs = before.doneAt
                event.put(Calendar.Events.DTSTART, startTimeMs)
                event.put(Calendar.Events.DTEND, endTimeMs)
            }
            val timeZone = TimeZone.getDefault().id
            event.put(Calendar.Events.EVENT_TIMEZONE, timeZone)
        }
        if (before.rrule != after.rrule) {
            if (after.rrule == null) {
                event.putNull(Calendar.Events.RRULE)
            } else {
                event.put(Calendar.Events.RRULE, after.calendarRRule)
            }
        }
        return event
    }

    private fun getCalendarId(): Long? {
        val isAccessGranted = checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
        if (isAccessGranted != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        var cursor = queryForCalendarId()
        if (cursor != null && cursor.count == 0) {
            cursor = queryForCalendarId(false)
        }
        if (cursor != null && cursor.moveToFirst()) {
            val calendarId: String
            val id = cursor.getColumnIndex(Calendars._ID)
            calendarId = cursor.getString(id)
            cursor.close()
            Timber.d("Picked calendar ($calendarId).")
            return calendarId.toLong()
        }
        return null
    }

    private fun queryForCalendarId(isPrimary: Boolean = true): Cursor? {
        val uri = Calendars.CONTENT_URI
        val projection = arrayOf(Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME)
        var selection = Calendars.VISIBLE + " = 1"
        if (isPrimary) {
            selection += " AND " + Calendars.IS_PRIMARY + " = 1"
        }
        val order = Calendars._ID + " ASC"
        return context.contentResolver.query(uri, projection, selection, null, order)
    }
}