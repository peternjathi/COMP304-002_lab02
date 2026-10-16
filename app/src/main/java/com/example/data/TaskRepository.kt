package com.example.data

import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * TaskRepository manages the collection of tasks using basic Kotlin collections (MutableList)
 * and exposes reactive StateFlow updates for the MVVM architecture.
 *
 * It is pre-populated with predefined tasks as required by the assignment:
 * "In the beginning, it should have at least one predefined task".
 */
object TaskRepository {

    private val today = DateUtils.getTodayFormatted()

    // Predefined tasks to demonstrate functionality on first launch
    private val taskList: MutableList<Task> = mutableListOf(
        Task(
            id = "predefined-task-1",
            heading = "COMP-304 Lab 2 Demonstration",
            description = "Develop WritingTasksApp with multiple activities, Task data class, Jetpack Compose UI, and MVVM architecture.",
            email = "peternjathi78@gmail.com",
            priority = Priority.CRITICAL,
            dateCreated = today,
            dueDate = "Oct 18, 2026"
        ),
        Task(
            id = "predefined-task-2",
            heading = "Jetpack Compose Navigation Review",
            description = "Study NavHost, NavController, and activity lifecycle transitions (onCreate, onStart, onResume).",
            email = "student@centennialcollege.ca",
            priority = Priority.HIGH,
            dateCreated = today,
            dueDate = "Oct 20, 2026"
        ),
        Task(
            id = "predefined-task-3",
            heading = "Prepare Final Project Proposal",
            description = "Draft requirements and architecture specifications for the upcoming Android mobile apps capstone project.",
            email = "peter.n@college.edu",
            priority = Priority.MEDIUM,
            dateCreated = today,
            dueDate = "Oct 25, 2026"
        )
    )

    private val _tasksFlow = MutableStateFlow<List<Task>>(taskList.toList())
    val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()

    /**
     * Retrieve all tasks.
     */
    fun getAllTasks(): List<Task> {
        return taskList.toList()
    }

    /**
     * Find a task by its unique ID.
     */
    fun getTaskById(id: String): Task? {
        return taskList.firstOrNull { it.id == id }
    }

    /**
     * Add a newly created task to the collection and notify observers.
     */
    fun addTask(task: Task) {
        taskList.add(0, task) // Add to top of list
        _tasksFlow.value = taskList.toList()
    }

    /**
     * Update an existing task in the collection.
     */
    fun updateTask(updatedTask: Task): Boolean {
        val index = taskList.indexOfFirst { it.id == updatedTask.id }
        return if (index != -1) {
            taskList[index] = updatedTask
            _tasksFlow.value = taskList.toList()
            true
        } else {
            false
        }
    }

    /**
     * Delete a task by ID.
     */
    fun deleteTask(id: String): Boolean {
        val removed = taskList.removeIf { it.id == id }
        if (removed) {
            _tasksFlow.value = taskList.toList()
        }
        return removed
    }

    /**
     * Reset repository with sample tasks (useful for testing and reset feature).
     */
    fun resetToDefaults() {
        taskList.clear()
        taskList.addAll(
            listOf(
                Task(
                    id = UUID.randomUUID().toString(),
                    heading = "COMP-304 Lab 2 Demonstration",
                    description = "Develop WritingTasksApp with multiple activities, Task data class, Jetpack Compose UI, and MVVM architecture.",
                    email = "peternjathi78@gmail.com",
                    priority = Priority.CRITICAL,
                    dateCreated = DateUtils.getTodayFormatted(),
                    dueDate = "Oct 18, 2026"
                ),
                Task(
                    id = UUID.randomUUID().toString(),
                    heading = "Jetpack Compose Navigation Review",
                    description = "Study NavHost, NavController, and activity lifecycle transitions (onCreate, onStart, onResume).",
                    email = "student@centennialcollege.ca",
                    priority = Priority.HIGH,
                    dateCreated = DateUtils.getTodayFormatted(),
                    dueDate = "Oct 20, 2026"
                )
            )
        )
        _tasksFlow.value = taskList.toList()
    }
}
