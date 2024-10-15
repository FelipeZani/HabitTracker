package com.example.habittracker

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarManagement {
    // Default initialization with the current week and month
    var currentDate: LocalDate = LocalDate.now()

    fun getDay(displayCount: Int = 0): List<Int> {
        val updatedDate = currentDate.plusDays(displayCount.toLong())
        val wholeweek = mutableListOf<Int>()
        for (i in 0 until 7) {
            wholeweek.add(updatedDate.plusDays(i.toLong()).dayOfMonth)
        }
        return wholeweek
    }

    fun getWeek(displayCount: Int = 0): List<String> {
        val updatedDate = currentDate.plusDays(displayCount.toLong())
        val wholeweek = mutableListOf<String>()
        for (i in 0 until 7) {
            val nextDay = updatedDate.plusDays(i.toLong()).dayOfWeek //this block will gen the displayed day of week based in the index i
            wholeweek.add(nextDay.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3)
                .replaceFirstChar { it.uppercaseChar() })
        }
        return wholeweek
    }

    // Function to update the current date based on arrow click
    fun updateDate(daysToAdd: Int) {
        currentDate = currentDate.plusDays(daysToAdd.toLong())
    }
}
