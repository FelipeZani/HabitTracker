package com.example.habittracker.database

class HabitModel (
    val habitName : String,
    val defaultHabit : Boolean,
    val creationDate: String,
    var recalDate : String?,
    var pickedUp:Boolean
)