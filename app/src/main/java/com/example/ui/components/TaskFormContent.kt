package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Priority
import com.example.util.DateUtils

/**
 * TaskFormContent arranges input fields vertically using a Column as required:
 * - Task heading (OutlinedTextField)
 * - Task description (OutlinedTextField)
 * - E-mail ID (OutlinedTextField with email validation)
 * - Priority level (Dropdown: Low, Medium, High, Critical)
 * - Date created (today's date, automatically populated)
 * - Due date (DatePicker composable)
 * - Save / Update Button
 * - Cancel Button
 * - Optional Delete Button in edit mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormContent(
    heading: String,
    onHeadingChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    dateCreated: String,
    dueDate: String,
    onDueDateChange: (String) -> Unit,
    headingError: String?,
    emailError: String?,
    dueDateError: String?,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    saveButtonText: String = stringResource(R.string.btn_save),
    isEditMode: Boolean = false,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // DatePickerDialog Composable for selecting Due Date
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            onDueDateChange(DateUtils.formatMillisToDate(selectedMillis))
                        }
                        showDatePicker = false
                    },
                    modifier = Modifier.testTag("date_picker_confirm_button")
                ) {
                    Text("Select Date")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    modifier = Modifier.testTag("date_picker_cancel_button")
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header Info Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isEditMode) "Edit Task Details" else "New Task Writing Pad",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Fill all required fields to manage your task effectively.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // 1. Task Heading Field
        OutlinedTextField(
            value = heading,
            onValueChange = onHeadingChange,
            label = { Text(stringResource(R.string.label_heading)) },
            placeholder = { Text("e.g. Complete Lab Assignment #2") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = headingError != null,
            supportingText = headingError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_task_heading")
        )

        // 2. Task Description Field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.label_description)) },
            placeholder = { Text("Provide details, deliverables, and instructions...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            minLines = 3,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_task_description")
        )

        // 3. E-mail ID Field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.label_email)) },
            placeholder = { Text("e.g. johnsmith@centennialcollege.ca") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_task_email")
        )

        // 4. Priority Level Field (Using Drop Down: Low, Medium, High, Critical)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = priority.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_priority)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = priority.textColor
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand Priority Dropdown",
                        modifier = Modifier.clickable { priorityDropdownExpanded = true }
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { priorityDropdownExpanded = true }
                    .testTag("dropdown_priority_trigger")
            )

            DropdownMenu(
                expanded = priorityDropdownExpanded,
                onDismissRequest = { priorityDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Priority.entries.forEach { level ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = level.displayName,
                                    fontWeight = if (level == priority) FontWeight.Bold else FontWeight.Normal
                                )
                                PriorityBadge(priority = level)
                            }
                        },
                        onClick = {
                            onPriorityChange(level)
                            priorityDropdownExpanded = false
                        },
                        modifier = Modifier.testTag("priority_option_${level.name}")
                    )
                }
            }
        }

        // 5. Date Created (today's date, automatically populated)
        OutlinedTextField(
            value = dateCreated,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_date_created)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = { Text("Automatically populated with today's date") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_date_created")
        )

        // 6. Due Date (as Date Picker composable)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dueDate,
                onValueChange = onDueDateChange,
                readOnly = true,
                label = { Text(stringResource(R.string.label_due_date)) },
                placeholder = { Text("Tap to select due date") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Open Date Picker",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                isError = dueDateError != null,
                supportingText = dueDateError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
                    .testTag("input_due_date")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons: Save/Update Button and Cancel Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancel Button
            OutlinedButton(
                onClick = onCancelClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_cancel_task")
            ) {
                Text(
                    text = stringResource(R.string.btn_cancel),
                    fontSize = 16.sp
                )
            }

            // Save / Update Button
            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .weight(1.2f)
                    .height(52.dp)
                    .testTag("btn_save_task")
            ) {
                Text(
                    text = saveButtonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Delete Button (if in Edit mode)
        if (isEditMode && onDeleteClick != null) {
            OutlinedButton(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_delete_task")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete This Task",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
