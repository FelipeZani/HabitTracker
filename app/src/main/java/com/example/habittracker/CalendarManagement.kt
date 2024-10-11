package com.example.habittracker

import androidx.compose.ui.graphics.drawscope.Stroke
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarManagement {
    var today : Int? = getDay()
    var week : List<String>? = null // Store all the week days in order to display


    fun getDay():Int{ //returns the date of Today
        var now = LocalDate.now()
        var day = now.getDayOfMonth();
        return day
    }

}