package com.example

import com.example.data.Priority
import com.example.data.Task
import com.example.data.TaskRepository
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests verifying task management, data class functionality,
 * repository CRUD operations, and date/email utilities for COMP-304 Lab #2.
 */
class ExampleUnitTest {

    @Before
    fun setup() {
        TaskRepository.resetToDefaults()
    }

    @Test
    fun testPredefinedTasksExist() {
        val tasks = TaskRepository.getAllTasks()
        assertTrue("Repository should have at least one predefined task on startup", tasks.isNotEmpty())
    }

    @Test
    fun testAddTaskIncreasesCount() {
        val initialCount = TaskRepository.getAllTasks().size
        val newTask = Task(
            heading = "New Test Task",
            description = "Test description",
            email = "student@example.com",
            priority = Priority.HIGH,
            dateCreated = DateUtils.getTodayFormatted(),
            dueDate = "Oct 30, 2026"
        )
        TaskRepository.addTask(newTask)
        assertEquals(initialCount + 1, TaskRepository.getAllTasks().size)
        assertNotNull(TaskRepository.getTaskById(newTask.id))
    }

    @Test
    fun testUpdateTask() {
        val task = TaskRepository.getAllTasks().first()
        val updatedTask = task.copy(
            heading = "Updated Task Heading",
            priority = Priority.CRITICAL
        )
        val result = TaskRepository.updateTask(updatedTask)
        assertTrue(result)
        val retrieved = TaskRepository.getTaskById(task.id)
        assertEquals("Updated Task Heading", retrieved?.heading)
        assertEquals(Priority.CRITICAL, retrieved?.priority)
    }

    @Test
    fun testDeleteTask() {
        val task = TaskRepository.getAllTasks().first()
        val result = TaskRepository.deleteTask(task.id)
        assertTrue(result)
        assertEquals(null, TaskRepository.getTaskById(task.id))
    }

    @Test
    fun testEmailValidation() {
        assertTrue(DateUtils.isValidEmail("john.smith@centennialcollege.ca"))
        assertTrue(DateUtils.isValidEmail("user@domain.com"))
        assertFalse(DateUtils.isValidEmail("invalid-email"))
        assertFalse(DateUtils.isValidEmail(""))
    }

    @Test
    fun testPriorityEnum() {
        assertEquals(Priority.LOW, Priority.fromString("Low"))
        assertEquals(Priority.MEDIUM, Priority.fromString("Medium"))
        assertEquals(Priority.HIGH, Priority.fromString("High"))
        assertEquals(Priority.CRITICAL, Priority.fromString("Critical"))
    }
}
