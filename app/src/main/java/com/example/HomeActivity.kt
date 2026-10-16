package com.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Priority
import com.example.data.Task
import com.example.ui.components.PriorityBadge
import com.example.ui.components.TaskCard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TaskViewModel

/**
 * HomeActivity: Main entry point of WritingTasksApp for COMP-304 Lab Assignment #2.
 *
 * Requirements fulfilled:
 * 1. Displays a list of tasks using a LazyColumn with at least one predefined task.
 * 2. Each task card displays heading, description, priority, e-mail id, date created, and due date.
 * 3. Includes a Floating Action Button (FAB) to navigate to CreateNewTaskActivity.
 * 4. Tapping a task card navigates to ViewEditTaskActivity.
 * 5. Lifecycle methods (onCreate, onStart, onResume, onPause, onStop, onDestroy, onRestart)
 *    are explicitly implemented and logged for transition management.
 */
class HomeActivity : ComponentActivity() {

    private val TAG = "HomeActivity"
    private val taskViewModel: TaskViewModel by viewModels()

    // Activity Result Launcher for launching CreateNewTaskActivity
    private val createTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "New task created successfully, refreshing view")
            Toast.makeText(this, getString(R.string.task_saved_success), Toast.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "Task creation cancelled by user")
        }
    }

    // Activity Result Launcher for launching ViewEditTaskActivity
    private val viewEditTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Task updated or deleted successfully")
        }
    }

    // ==========================================
    // ACTIVITY LIFECYCLE METHODS
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity created, initializing UI and ViewModel")
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                HomeScreen(
                    viewModel = taskViewModel,
                    onNavigateToCreateTask = {
                        Log.d(TAG, "Navigating to CreateNewTaskActivity via Intent")
                        val intent = Intent(this, CreateNewTaskActivity::class.java)
                        createTaskLauncher.launch(intent)
                    },
                    onNavigateToViewEditTask = { taskId ->
                        Log.d(TAG, "Navigating to ViewEditTaskActivity for taskId: $taskId")
                        val intent = Intent(this, ViewEditTaskActivity::class.java).apply {
                            putExtra(ViewEditTaskActivity.EXTRA_TASK_ID, taskId)
                        }
                        viewEditTaskLauncher.launch(intent)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity is becoming visible to the user")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity is in foreground and interactive; refreshing task state")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity is partially obscured or about to transition to background")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity is no longer visible to the user")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: Activity is returning to the foreground from stopped state")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity is being destroyed, releasing resources")
    }
}

/**
 * HomeScreen composable layout using Jetpack Compose components:
 * - Scaffold with TopAppBar and FloatingActionButton
 * - Priority filter chips and search bar
 * - LazyColumn displaying individual Task Cards
 * - Empty state with friendly feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToViewEditTask: (String) -> Unit
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val selectedPriority by viewModel.selectedPriority.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showInfoDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    // Deletion confirmation dialog
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${taskToDelete?.heading}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.id?.let { viewModel.deleteTask(it) }
                        taskToDelete = null
                    },
                    modifier = Modifier.testTag("dialog_confirm_delete")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Lab Info dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COMP-304 Lab #2")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("WritingTasksApp", fontWeight = FontWeight.Bold)
                    Text("Demonstrating:")
                    Text("• Multiple Activities (Home, Create, View/Edit)")
                    Text("• Explicit Lifecycle Methods logged in Logcat")
                    Text("• Jetpack Compose UI (LazyColumn, Card, TextField, Button, FAB)")
                    Text("• MVVM Architecture & Kotlin Collections")
                    Text("• DatePicker Composable & Priority Dropdown")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.home_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${tasks.size} Task${if (tasks.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("btn_app_info")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Lab Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_create_task")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create New Task",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search tasks by heading, description...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("input_search_tasks")
            )

            // Priority Filter Chips row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                // "All" chip
                FilterChip(
                    selected = selectedPriority == null,
                    onClick = { viewModel.setPriorityFilter(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(),
                    modifier = Modifier.testTag("filter_chip_all")
                )

                // Individual priority chips
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = selectedPriority == p,
                        onClick = {
                            viewModel.setPriorityFilter(if (selectedPriority == p) null else p)
                        },
                        label = { Text(p.displayName) },
                        modifier = Modifier.testTag("filter_chip_${p.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Task List using LazyColumn as required
            if (tasks.isEmpty()) {
                // Friendly Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Tasks Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.no_tasks_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("lazy_column_tasks"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToViewEditTask(task.id) },
                            onDeleteClick = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }
}
