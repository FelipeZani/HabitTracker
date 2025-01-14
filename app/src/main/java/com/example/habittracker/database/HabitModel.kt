package com.example.habittracker.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class HabitModel (
    val habitName : String,
    val defaultHabit : Boolean,
    val creationDate: String,
    var recalDate : String?,
    var pickedUp : MutableState<Boolean> = mutableStateOf(false)
){
    override fun hashCode(): Int {
        super.hashCode()

        var result = habitName.hashCode()
        result += 31 * (defaultHabit.hashCode() + creationDate.hashCode()+creationDate.hashCode())

        return result

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HabitModel

        if (habitName != other.habitName) return false
        if (defaultHabit != other.defaultHabit) return false
        if (creationDate != other.creationDate) return false
        if (recalDate != other.recalDate) return false

        return true
    }
}