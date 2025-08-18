package com.example.apitest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apitest.data.model.Section
import com.example.apitest.data.model.Task

class SectionsViewModel : ViewModel() {
    private val _sections = MutableLiveData<List<Section>>(emptyList())
    val sections: LiveData<List<Section>> get() = _sections

    fun addSection(section: Section) {
        _sections.value = _sections.value.orEmpty() + section
    }

    fun removeSection(section: Section) {
        _sections.value = _sections.value.orEmpty().filter { it != section }
    }

    // Add task to a section
    fun addTaskToSection(sectionIndex: Int, task: Task) {
        val currentSections = _sections.value.orEmpty().toMutableList()
        val section = currentSections[sectionIndex]
        val updatedTasks = section.tasks + task
        currentSections[sectionIndex] = section.copy(tasks = updatedTasks)
        _sections.value = currentSections
    }

}
