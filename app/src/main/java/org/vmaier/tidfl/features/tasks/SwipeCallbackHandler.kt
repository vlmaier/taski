package org.vmaier.tidfl.features.tasks

import android.graphics.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.data.entity.Task
import org.vmaier.tidfl.utils.toBitmap


/**
 * Created by Vladas Maier
 * on 06/02/2020
 * at 19:09
 */
class SwipeCallbackHandler :
    SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        rv: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeDirs (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // disable swiping on section items
        if (viewHolder is TaskAdapter.TaskSectionViewHolder) return 0
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val itemView = viewHolder.itemView
        val context = itemView.context
        lateinit var message: String
        lateinit var taskToRestore: Task
        if (direction == ItemTouchHelper.LEFT) {
            taskToRestore = TaskListFragment.taskAdapter.removeItem(position, Status.DONE)
            message = context.getString(R.string.event_task_complete, taskToRestore.xpValue)
        } else {
            taskToRestore = TaskListFragment.taskAdapter.removeItem(position, Status.FAILED)
            message = context.getString(R.string.event_task_failed)
        }
        // showing snack bar with undo option
        Snackbar.make(itemView, message, Snackbar.LENGTH_LONG)
            .setAction(context.getString(R.string.action_undo)) {
                // undo is selected, restore the deleted item
                TaskListFragment.taskAdapter.restoreItem(taskToRestore)
            }.setActionTextColor(Color.YELLOW).show()
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
                paint.color = ContextCompat.getColor(context, R.color.colorRedCancel)
                val background = RectF(left, top, dX, bottom)
                c.drawRect(background, paint)
                icon = AppCompatResources.getDrawable(
                    context, R.drawable.ic_outline_cancel_24
                )?.toBitmap()
                val destination = RectF(
                    left + width, top + width,
                    left + 2 * width, bottom - width
                )
                if (icon != null) c.drawBitmap(icon, null, destination, paint)
            } else {
                // swipe to the left
                paint.color = ContextCompat.getColor(context, R.color.colorGreenDone)
                val background = RectF(right + dX, top, right, bottom)
                c.drawRect(background, paint)
                icon = AppCompatResources.getDrawable(
                    context, R.drawable.ic_baseline_done_24
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