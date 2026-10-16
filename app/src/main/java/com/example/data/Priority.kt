package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.PriorityCriticalBg
import com.example.ui.theme.PriorityCriticalColor
import com.example.ui.theme.PriorityHighBg
import com.example.ui.theme.PriorityHighColor
import com.example.ui.theme.PriorityLowBg
import com.example.ui.theme.PriorityLowColor
import com.example.ui.theme.PriorityMediumBg
import com.example.ui.theme.PriorityMediumColor

/**
 * Priority enum representing the four task priority levels required by the assignment:
 * Low, Medium, High, and Critical.
 */
enum class Priority(
    val displayName: String,
    val textColor: Color,
    val backgroundColor: Color
) {
    LOW(
        displayName = "Low",
        textColor = PriorityLowColor,
        backgroundColor = PriorityLowBg
    ),
    MEDIUM(
        displayName = "Medium",
        textColor = PriorityMediumColor,
        backgroundColor = PriorityMediumBg
    ),
    HIGH(
        displayName = "High",
        textColor = PriorityHighColor,
        backgroundColor = PriorityHighBg
    ),
    CRITICAL(
        displayName = "Critical",
        textColor = PriorityCriticalColor,
        backgroundColor = PriorityCriticalBg
    );

    companion object {
        fun fromString(value: String): Priority {
            return entries.firstOrNull { it.displayName.equals(value, ignoreCase = true) }
                ?: MEDIUM
        }
    }
}
