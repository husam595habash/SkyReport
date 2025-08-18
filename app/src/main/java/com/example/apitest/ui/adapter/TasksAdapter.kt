package com.example.apitest.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.Task
import android.widget.EditText
import kotlin.math.log

class TasksAdapter(
    private var tasks: List<Task> = emptyList(),
    private val onDeleteClick: (Task) -> Unit,
    private val onTitleChanged: (Task, String) -> Unit
) : RecyclerView.Adapter<TasksAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: EditText = itemView.findViewById(R.id.taskTitle)
        val removeButton: ImageButton = itemView.findViewById(R.id.removeTask)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val task = tasks[position]

        holder.title.setText(task.title ?: "")
        Log.d("husam", "${task.title} + ${tasks.size}" )

        holder.title.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val updatedTitle = holder.title.text.toString()
                if (updatedTitle != task.title) {
                    Log.d("TasksAdapter", "Title changed for task id=${task.id}: $updatedTitle")
                    onTitleChanged(task, updatedTitle)
                }
            }
        }


        holder.removeButton.setOnClickListener {
            onDeleteClick(task)
            Log.d("husam", "${task.title} ${tasks.size}" )
        }
    }



    override fun getItemCount(): Int = tasks.size

    fun setTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }



}
