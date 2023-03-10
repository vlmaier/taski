package com.vmaier.taski.features.tasks

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Task
import com.vmaier.taski.data.repository.TaskRepository
import com.vmaier.taski.features.tasks.TaskListFragment.Companion.taskAdapter
import com.vmaier.taski.lifecycleOwner
import com.vmaier.taski.toBitmap
import com.vmaier.taski.utils.Utils


/**
 * Created by Vladas Maier
 * on 06.02.2020
 * at 19:09
 */
class TaskItemSwipeHandler :
    SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        rv: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        val itemView = viewHolder.itemView
        val context = itemView.context
        val message: String
        val taskToRestore: Task?
        val closedTaskId: LiveData<Long>
        val isCounterIncremented: Boolean
        val closedAt: Long?
        if (direction == ItemTouchHelper.LEFT) {
            val completedTask = taskAdapter.removeItem(position, Status.DONE)
            taskToRestore = completedTask.first
            closedTaskId = completedTask.second
            isCounterIncremented = completedTask.third
            closedAt = completedTask.fourth
            message = context.getString(R.string.event_task_complete, taskToRestore?.xp)
        } else {
            val failedTask = taskAdapter.removeItem(position, Status.FAILED)
            taskToRestore = failedTask.first
            closedTaskId = failedTask.second
            isCounterIncremented = failedTask.third
            closedAt = failedTask.fourth
            message = context.getString(R.string.event_task_failed)
        }
        // show snackbar with "Undo" option
        val snackbar = Snackbar.make(MainActivity.fab, message, Snackbar.LENGTH_LONG)
            .setAction(context.getString(R.string.action_undo)) {
                // "Undo" is selected -> restore the deleted item
                val taskId = taskToRestore?.id ?: 0L
                taskAdapter.restoreItem(taskId, position, isCounterIncremented, closedAt)
                val lifecycleOwner = context.lifecycleOwner()
                if (lifecycleOwner != null) {
                    closedTaskId.observe(lifecycleOwner, { id ->
                        if (id != 0L) {
                            val taskRepository = TaskRepository(context)
                            taskRepository.deleteClosedTask(id)
                        }
                    })
                }
            }
            .setActionTextColor(Utils.getThemeColor(context, R.attr.colorSecondary))
        snackbar.view.setOnClickListener { snackbar.dismiss() }
        snackbar.show()
    }

    override fun onChildDraw(
        c: Canvas, rv: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val icon: Bitmap?
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val item = viewHolder.itemView
            val context = item.context
            val left = item.left.toFloat()
            val right = item.right.toFloat()
            val top = item.top.toFloat()
            val bottom = item.bottom.toFloat()
            val height = bottom - top
            val width = height / 3
            val paint = Paint()
            if (dX > 0) {
                // swipe to the right
                paint.color = ContextCompat.getColor(context, R.color.colorSwipeCancel)
                val background = RectF(left, top, dX, bottom)
                c.drawRect(background, paint)
                icon = AppCompatResources.getDrawable(
                    context, R.drawable.ic_cancel_24
                )?.toBitmap()
                val destination = RectF(
                    left + width, top + width,
                    left + 2 * width, bottom - width
                )
                if (icon != null) c.drawBitmap(icon, null, destination, paint)
            } else {
                // swipe to the left
                paint.color = ContextCompat.getColor(context, R.color.colorSwipeDone)
                val background = RectF(right + dX, top, right, bottom)
                c.drawRect(background, paint)
                icon = AppCompatResources.getDrawable(
                    context, R.drawable.ic_done_24
                )?.toBitmap()
                val destination = RectF(
                    right - 2 * width, top + width,
                    right - width, bottom - width
                )
                if (icon != null) c.drawBitmap(icon, null, destination, paint)
            }
        }
        super.onChildDraw(c, rv, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}