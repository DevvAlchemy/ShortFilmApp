package com.example.shortfilmapp.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.Locale

// utils/DateUtils.kt
object DateUtils {
    /**
     * Formats a date string from API format (yyyy-MM-dd) to a more readable format
     */
    fun formatReleaseDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Gets the year from a date string
     */
    fun getYearFromDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date ?: return ""
            calendar.get(Calendar.YEAR).toString()
        } catch (e: Exception) {
            ""
        }
    }
}