package com.zelyder.todoapp.presentation.tasks_list

import android.content.res.ColorStateList
import android.graphics.Paint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zelyder.todoapp.R
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.isOverdue

class TasksListAdapter(val clickListener: TasksListItemClickListener) : ListAdapter<Task, TasksListAdapter.TasksViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun deleteItem(position: Int){
        clickListener.onDelete(getItem(position))
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)

    }
    fun checkItem(position: Int) {
        val item = getItem(position)
        item.isDone = !item.isDone
        clickListener.onCheck(item)
        val newList = currentList.toMutableList()
        newList[position] = item
        submitList(newList)
        notifyItemChanged(position)
    }

    fun editItem(position: Int) {
        val item = getItem(position)
        clickListener.onEdit(item)
        val newList = currentList.toMutableList()
        newList[position] = item
        submitList(newList)
        notifyItemChanged(position)
    }

    inner class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.tvItemText)
        private val dateTimeText: TextView = itemView.findViewById(R.id.tvItemDateTime)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbItemTask)
        private val infoImg: ImageView = itemView.findViewById(R.id.infoImgItemTask)
        private val priorityImg: ImageView = itemView.findViewById(R.id.priorityImgItemTask)

        fun bind(task: Task) {
            val typedValue = TypedValue()
            itemView.context.theme.resolveAttribute(
                R.attr.colorOnBackground,
                typedValue,
                true
            )

            checkBox.isChecked = task.isDone
            if (task.isDone) {
                textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray))
            } else {
                textView.paintFlags = 0
                textView.setTextColor(typedValue.data)
            }
            itemView.setOnClickListener {
                clickListener.onItemClick(task)
            }
            task.date?.let {
                if(isOverdue(it)) {
                    val iconsColorStates = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_selected)),
                        intArrayOf(ContextCompat.getColor(itemView.context, R.color.red_light)))

                    checkBox.buttonTintList = iconsColorStates
                }
            }
            checkBox.setOnClickListener {
                task.isDone = checkBox.isChecked
                clickListener.onCheck(task)
                if (task.isDone) {
                    textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray))
                } else {


                    textView.setTextColor(typedValue.data)
                    textView.paintFlags = 0
                }
            }

            textView.text = task.text
            if (task.date == null) {
                dateTimeText.visibility = View.GONE
            } else {
                dateTimeText.text = task.date
                dateTimeText.visibility = View.VISIBLE
            }
            val drawable = when (task.importance) {
                Importance.NONE -> null
                Importance.LOW -> R.drawable.ic_priority_low
                Importance.HIGH -> R.drawable.ic_priority_high
            }
            if (drawable != null) {
                priorityImg.setImageDrawable(ContextCompat.getDrawable(itemView.context, drawable))
                priorityImg.visibility = View.VISIBLE
            } else {
                priorityImg.visibility = View.GONE
            }
        }

    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem == newItem
        }
    }
}