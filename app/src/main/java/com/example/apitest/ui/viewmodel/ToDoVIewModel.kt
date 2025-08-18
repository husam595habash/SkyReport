package com.example.apitest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apitest.data.model.Task

class ToDoViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> get() = _tasks

    fun addTask() {
        val currentTasks = _tasks.value ?: emptyList()
        val newTask = Task(title = "", date = "", isChecked = false)
        _tasks.value = currentTasks + newTask
        Log.d("ToDoViewModel", "Task added. Total tasks: ${_tasks.value?.size}")
    }



    fun removeTask(task: Task) {
        _tasks.value = _tasks.value.orEmpty().toMutableList().apply { remove(task) }
    }

    fun toggleTaskCheck(task: Task, isChecked: Boolean) {
        _tasks.value = _tasks.value.orEmpty().map {
            if (it == task) it.copy(isChecked = isChecked) else it
        }

        Log.d("TasksViewModel", "check")
    }

    fun updateTaskTitle(task: Task, newTitle: String) {
        _tasks.value = _tasks.value.orEmpty().map {
            if (it.id == task.id) it.copy(title = newTitle) else it
        }

        Log.d("TasksViewModel", "title  $newTitle")
    }

    fun clearTasks() {
        _tasks.value = emptyList()
    }



}

