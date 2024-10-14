package com.example.habittracker

import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.selects.whileSelect
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarManagement {
    var thisDaysMonths: List<Int> = getDay()
    var thisDaysWeeks : List<String> = getWeek() // Store all the week days in order to be displayed




    fun getDay():List<Int>{ //returns Today's date
        var now = LocalDate.now()
        var day = now.getDayOfMonth()
        var wholeweek = mutableListOf<Int>()
        repeat(7){index->
            wholeweek.add(day+index)
        }
        return wholeweek
    }

    fun getWeek() : MutableList<String>{ //returns today's weekday
        var now = LocalDate.now()
        var today = now.getDayOfWeek()
        var wholeweek = mutableListOf<String>()
        repeat(7){index->
            var nextDay = today.plus(index.toLong())
            wholeweek.add(nextDay.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3)
                .replaceFirstChar { it.uppercaseChar() });
        }
        return wholeweek
    }

}