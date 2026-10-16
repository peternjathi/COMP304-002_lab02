package com.example.data

import java.io.Serializable
import java.util.UUID

/**
 * Data class representing a task in the WritingTasksApp as required by Lab Assignment #2.
 *
 * @param id Unique identifier for the task
 * @param heading Title or heading of the task
 * @param description Detailed description of the task
 * @param email E-mail ID associated with the task creator/assignee
 * @param priority Priority level: Low, Medium, High, or Critical
 * @param dateCreated Automatically populated creation date (today's date)
 * @param dueDate Due date chosen via date picker composable
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    var heading: String,
    var description: String,
    var email: String,
    var priority: Priority = Priority.MEDIUM,
    val dateCreated: String,
    var dueDate: String
) : Serializable
