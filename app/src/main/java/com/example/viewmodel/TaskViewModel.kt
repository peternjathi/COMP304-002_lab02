package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Priority
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * TaskViewModel implements the ViewModel part of the MVVM pattern.
 * It manages state for the UI, provides reactive streams of tasks, and handles user interactions.
 */
class TaskViewModel : ViewModel() {

    // Filter by priority (null = show all)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    val selectedPriority: StateFlow<Priority?> = _selectedPriority

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Combined filtered task list
    val filteredTasks: StateFlow<List<Task>> = combine(
        TaskRepository.tasksFlow,
        _selectedPriority,
        _searchQuery
    ) { allTasks, priority, query ->
        allTasks.filter { task ->
            val matchesPriority = priority == null || task.priority == priority
            val matchesQuery = query.isBlank() ||
                task.heading.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true) ||
                task.email.contains(query, ignoreCase = true)
            matchesPriority && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskRepository.getAllTasks()
    )

    fun setPriorityFilter(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(task: Task) {
        TaskRepository.addTask(task)
    }

    fun updateTask(task: Task): Boolean {
        return TaskRepository.updateTask(task)
    }

    fun deleteTask(id: String): Boolean {
        return TaskRepository.deleteTask(id)
    }

    fun getTaskById(id: String): Task? {
        return TaskRepository.getTaskById(id)
    }
}
