package com.vmaier.taski.features.categories

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import com.vmaier.taski.R
import com.vmaier.taski.features.categories.CategoryListFragment.Companion.categoryAdapter
import com.vmaier.taski.utils.Utils
import kotlinx.android.synthetic.main.item_category.view.*


/**
 * Created by Vladas Maier
 * on 24/07/2020
 * at 10:22
 */
class CategoryItemSwipeHandler :
    SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        rv: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeDirs(
        rv: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is CategoryAdapter.CategoryMenuViewHolder) {
            ItemTouchHelper.RIGHT
        } else {
            ItemTouchHelper.LEFT
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        if (categoryAdapter.isMenuShown(position)) {
            categoryAdapter.closeMenu(position)
        } else {
            categoryAdapter.showMenu(position)
        }
    }

    override fun onChildDraw(
        c: Canvas, rv: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val item = viewHolder.itemView
        val context = item.context
        var background = ColorDrawable(item.cv.cardBackgroundColor.defaultColor)
        when {
            // swipe to the right
            dX > 0 -> {
                background.setBounds(
                    item.left, item.top, item.left + dX.toInt(), item.bottom
                )
            }
            // swipe to the left
            dX < 0 -> {
                background = ColorDrawable(Utils.getThemeColor(context, R.attr.colorPrimary))
                background.setBounds(
                    item.right + dX.toInt(), item.top, item.right, item.bottom
                )
            }
        }
        background.draw(c)
        super.onChildDraw(c, rv, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}