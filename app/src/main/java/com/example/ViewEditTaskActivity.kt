package com.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.data.Priority
import com.example.data.Task
import com.example.data.TaskRepository
import com.example.ui.components.TaskFormContent
import com.example.ui.theme.MyApplicationTheme
import com.example.util.DateUtils

/**
 * ViewEditTaskActivity displays and allows editing an existing task.
 *
 * Requirements fulfilled:
 * 1. Displays the selected task's heading, description, e-mail, priority level, date created, and due date.
 * 2. Pre-filled with the existing task data.
 * 3. Allows the user to edit the task fields.
 * 4. Includes a Save/Update button to save the changes and return to Home Activity.
 * 5. Includes a Cancel button to discard changes and return to Home Activity.
 * 6. Lifecycle methods (onCreate, onStart, onResume, onPause, onStop, onDestroy)
 *    are explicitly implemented and logged.
 */
class ViewEditTaskActivity : ComponentActivity() {

    private val TAG = "ViewEditTaskActivity"

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    // ==========================================
    // ACTIVITY LIFECYCLE METHODS
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing ViewEditTaskActivity")
        enableEdgeToEdge()

        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: ""
        Log.d(TAG, "onCreate: Received task ID extra: $taskId")

        val task = TaskRepository.getTaskById(taskId)
        if (task == null) {
            Log.e(TAG, "Task with ID $taskId not found in repository")
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            MyApplicationTheme {
                ViewEditTaskScreen(
                    initialTask = task,
                    onUpdateTask = { updatedTask ->
                        Log.d(TAG, "Updating task ID: ${updatedTask.id}")
                        TaskRepository.updateTask(updatedTask)
                        Toast.makeText(this, getString(R.string.task_updated_success), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onDeleteTask = { idToDelete ->
                        Log.d(TAG, "Deleting task ID: $idToDelete")
                        TaskRepository.deleteTask(idToDelete)
                        Toast.makeText(this, getString(R.string.task_deleted_success), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onCancel = {
                        Log.d(TAG, "User cancelled task editing")
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ViewEditTaskActivity is visible")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ViewEditTaskActivity is active")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ViewEditTaskActivity is pausing")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ViewEditTaskActivity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ViewEditTaskActivity destroyed")
    }
}

/**
 * ViewEditTaskScreen composable hosting the pre-filled form and update logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEditTaskScreen(
    initialTask: Task,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit,
    onCancel: () -> Unit
) {
    // Form field states pre-filled with existing task data
    var heading by remember { mutableStateOf(initialTask.heading) }
    var description by remember { mutableStateOf(initialTask.description) }
    var email by remember { mutableStateOf(initialTask.email) }
    var priority by remember { mutableStateOf(initialTask.priority) }
    val dateCreated = remember { initialTask.dateCreated }
    var dueDate by remember { mutableStateOf(initialTask.dueDate) }

    // Validation error states
    var headingError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var dueDateError by remember { mutableStateOf<String?>(null) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to permanently delete '${initialTask.heading}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteTask(initialTask.id)
                    },
                    modifier = Modifier.testTag("dialog_confirm_delete_btn")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    fun validateAndUpdate() {
        var isValid = true

        if (heading.trim().isBlank()) {
            headingError = "Task heading is required."
            isValid = false
        } else {
            headingError = null
        }

        if (email.trim().isNotBlank() && !DateUtils.isValidEmail(email)) {
            emailError = "Please enter a valid e-mail address."
            isValid = false
        } else {
            emailError = null
        }

        if (dueDate.trim().isBlank()) {
            dueDateError = "Please select a due date."
            isValid = false
        } else {
            dueDateError = null
        }

        if (isValid) {
            val updated = initialTask.copy(
                heading = heading.trim(),
                description = description.trim(),
                email = email.trim(),
                priority = priority,
                dueDate = dueDate.trim()
            )
            onUpdateTask(updated)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.view_edit_task_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("btn_back_view_edit")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("btn_topbar_delete")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        TaskFormContent(
            heading = heading,
            onHeadingChange = {
                heading = it
                if (it.isNotBlank()) headingError = null
            },
            description = description,
            onDescriptionChange = { description = it },
            email = email,
            onEmailChange = {
                email = it
                if (DateUtils.isValidEmail(it)) emailError = null
            },
            priority = priority,
            onPriorityChange = { priority = it },
            dateCreated = dateCreated,
            dueDate = dueDate,
            onDueDateChange = {
                dueDate = it
                if (it.isNotBlank()) dueDateError = null
            },
            headingError = headingError,
            emailError = emailError,
            dueDateError = dueDateError,
            onSaveClick = { validateAndUpdate() },
            onCancelClick = onCancel,
            saveButtonText = stringResource(R.string.btn_update),
            isEditMode = true,
            onDeleteClick = { showDeleteConfirmDialog = true },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
