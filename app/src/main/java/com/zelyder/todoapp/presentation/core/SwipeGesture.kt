package com.zelyder.todoapp.presentation.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zelyder.todoapp.R
import com.zelyder.todoapp.presentation.tasks_list.TasksListAdapter

class SwipeGesture(private val adapter: TasksListAdapter, context: Context) :
    ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val deleteBackgroundColor =
        ColorDrawable(ContextCompat.getColor(context, R.color.red_light))

    private val checkIcon = ContextCompat.getDrawable(context, R.drawable.ic_check)
    private val checkBackgroundColor =
        ColorDrawable(ContextCompat.getColor(context, R.color.green_light))

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        when {
            dX > 0 -> { // Swiping to the right
                if (checkIcon != null) {
                    val iconMargin: Int = (itemView.height - checkIcon.intrinsicHeight) / 2
                    val iconTop: Int =
                        itemView.top + (itemView.height - checkIcon.intrinsicHeight) / 2
                    val iconBottom: Int = iconTop + checkIcon.intrinsicHeight

                    val iconLeft: Int = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + checkIcon.intrinsicWidth
                    checkIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    checkBackgroundColor.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )

                    checkBackgroundColor.draw(c)
                    checkIcon.draw(c)
                }
            }
            dX < 0 -> { // Swiping to the left
                if (deleteIcon != null) {
                    val iconMargin: Int = (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconTop: Int =
                        itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconBottom: Int = iconTop + deleteIcon.intrinsicHeight


                    val iconLeft: Int = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteBackgroundColor.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top, itemView.right, itemView.bottom
                    )
                    deleteBackgroundColor.draw(c)
                    deleteIcon.draw(c)
                }
            }
            else -> { // view is unSwiped
                deleteBackgroundColor.setBounds(0, 0, 0, 0)
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> adapter.deleteItem(position)
            ItemTouchHelper.RIGHT -> adapter.checkItem(position)
        }
    }
}