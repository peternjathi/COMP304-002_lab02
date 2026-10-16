package com.example.util

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for date manipulation and basic input validation.
 * Uses standard SimpleDateFormat compatible across Android API levels.
 */
object DateUtils {
    private const val DATE_PATTERN = "MMM dd, yyyy"

    /**
     * Returns today's date formatted (e.g., "Oct 15, 2026").
     * Used for automatically populating the date created field.
     */
    fun getTodayFormatted(): String {
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Formats epoch milliseconds (from Material 3 DatePicker) into a readable date string.
     */
    fun formatMillisToDate(millis: Long?): String {
        if (millis == null) return ""
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        // Adjust for UTC timezone offset in DatePicker if needed
        return sdf.format(Date(millis))
    }

    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    /**
     * Validates that an e-mail string conforms to standard e-mail patterns.
     * Uses pure Kotlin regex to avoid Android framework stub issues in JVM unit tests.
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matches(email.trim())
    }
}
