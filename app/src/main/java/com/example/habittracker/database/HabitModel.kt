package com.example.habittracker.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class HabitModel (
    val habitName : String,
    val defaultHabit : Boolean,
    val creationDate: String,
    var recalDate : String?,
    var pickedUp : MutableState<Boolean> = mutableStateOf(false)
)