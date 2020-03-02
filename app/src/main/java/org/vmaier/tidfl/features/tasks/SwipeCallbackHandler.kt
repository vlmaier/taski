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
import org.vmaier.tidfl.util.toBitmap


/**
 * Created by Vladas Maier
 * on 06/02/2020.
 * at 19:09
 */
class SwipeCallbackHandler :
    SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        if (direction == ItemTouchHelper.LEFT) {
            val doneTask = TaskListFragment.taskAdapter.removeItem(position, Status.DONE)
            // showing snack bar with undo option
            Snackbar.make(
                viewHolder.itemView,
                " Task completed (+${doneTask?.xpGain}XP)",
                Snackbar.LENGTH_LONG
            ).setAction("UNDO") {
                // undo is selected, restore the deleted item
                TaskListFragment.taskAdapter.restoreItem(doneTask!!, position)
            }.setActionTextColor(Color.YELLOW).show()
        } else {
            val failedTask = TaskListFragment.taskAdapter.removeItem(position, Status.FAILED)
            // showing snack bar with undo option
            Snackbar.make(
                viewHolder.itemView,
                " Task failed",
                Snackbar.LENGTH_LONG
            ).setAction("UNDO") {
                // undo is selected, restore the deleted item
                TaskListFragment.taskAdapter.restoreItem(failedTask!!, position)
            }.setActionTextColor(Color.YELLOW).show()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val icon: Bitmap
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3
            val p = Paint()

            // swipe to the right
            if (dX > 0) {
                p.color = ContextCompat.getColor(TaskListFragment.mContext, R.color.colorRedCancel)
                val background =
                    RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        dX,
                        itemView.bottom.toFloat()
                    )
                c.drawRect(background, p)
                icon = AppCompatResources.getDrawable(
                    TaskListFragment.mContext,
                    R.drawable.ic_outline_cancel_24
                )?.toBitmap()!!
                val destination = RectF(
                    itemView.left.toFloat() + width,
                    itemView.top.toFloat() + width,
                    itemView.left.toFloat() + 2 * width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, destination, p)

                // swipe to the left
            } else {
                p.color = ContextCompat.getColor(TaskListFragment.mContext, R.color.colorGreenDone)
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, p)
                icon =
                    AppCompatResources.getDrawable(
                        TaskListFragment.mContext,
                        R.drawable.ic_baseline_done_24
                    )?.toBitmap()!!
                val destination = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, destination, p)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }
}