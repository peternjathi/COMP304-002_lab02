package com.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * CreateNewTaskActivity allows the user to write and save a new task.
 *
 * Requirements fulfilled:
 * 1. Contains input fields for task heading, task description, e-mail, priority level
 *    (Low, Medium, High, Critical via dropdown), date created (today's date automatically populated),
 *    and due date (as date picker composable).
 * 2. Uses vertical Column layout and OutlinedTextField components.
 * 3. Includes Save button to save the task and return to Home Activity.
 * 4. Includes Cancel button to cancel creation and return to Home Activity.
 * 5. Lifecycle methods (onCreate, onStart, onResume, onPause, onStop, onDestroy)
 *    are explicitly implemented and logged.
 */
class CreateNewTaskActivity : ComponentActivity() {

    private val TAG = "CreateNewTaskActivity"

    // ==========================================
    // ACTIVITY LIFECYCLE METHODS
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing CreateNewTaskActivity")
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CreateTaskScreen(
                    onSaveTask = { newTask ->
                        Log.d(TAG, "Saving new task: ${newTask.heading}")
                        TaskRepository.addTask(newTask)
                        Toast.makeText(this, getString(R.string.task_saved_success), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onCancel = {
                        Log.d(TAG, "User cancelled task creation")
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: CreateNewTaskActivity is visible")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: CreateNewTaskActivity is active and ready for input")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: CreateNewTaskActivity is pausing")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: CreateNewTaskActivity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: CreateNewTaskActivity destroyed")
    }
}

/**
 * CreateTaskScreen composable hosting the form elements and state handling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun CreateTaskScreen(
    onSaveTask: (Task) -> Unit,
    onCancel: () -> Unit
) {
    // Form field states
    var heading by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    val dateCreated = remember { DateUtils.getTodayFormatted() }
    var dueDate by remember { mutableStateOf("") }

    // Validation error states
    var headingError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var dueDateError by remember { mutableStateOf<String?>(null) }

    fun validateAndSave() {
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
            val newTask = Task(
                heading = heading.trim(),
                description = description.trim(),
                email = email.trim(),
                priority = priority,
                dateCreated = dateCreated,
                dueDate = dueDate.trim()
            )
            onSaveTask(newTask)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_task_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("btn_back_create")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            onSaveClick = { validateAndSave() },
            onCancelClick = onCancel,
            saveButtonText = stringResource(R.string.btn_save),
            isEditMode = false,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
