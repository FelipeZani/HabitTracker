package com.example.habittracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val habitsQuery = """
            CREATE TABLE $HABITS_TABLE (
                $ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $HABIT_NAME TEXT,
                $DEFAULT_HABIT BOOLEAN,
                $CREATE_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                $RECALLTIME TIME
            )
        """
        val streakQuery = """ 
            CREATE TABLE $STREAK_TABLE (
                $STREAK_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $HABIT_ID INTEGER,
                $DATE_ACCOMPLISHED DATE,
                FOREIGN KEY ($HABIT_ID) REFERENCES $HABITS_TABLE($ID_COL)
            )
        """

        db?.execSQL(habitsQuery)
        db?.execSQL(streakQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $HABITS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $STREAK_TABLE")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "HabitTrackerDB"
        const val DB_VERSION = 1

        // Habits Table Columns
        const val HABITS_TABLE = "habitsTable"
        const val ID_COL = "id"
        const val HABIT_NAME = "habitName"
        const val DEFAULT_HABIT = "defaultHabit"
        const val CREATE_AT = "createdAt"
        const val RECALLTIME = "recallTime"

        // Streaks Table Columns
        const val STREAK_TABLE = "streakTable"
        const val STREAK_ID_COL = "streakId"
        const val HABIT_ID = "habitId"
        const val DATE_ACCOMPLISHED = "dateAccomplished"
    }
    fun addNewHabit(habitsName : String,
                     defaultHabit : Boolean = true,
                     creationDate: String,
                     recalDate : String?
    ){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(HABITS_TABLE, habitsName)
        values.put(DEFAULT_HABIT, defaultHabit)
        values.put(CREATE_AT, creationDate)
        values.put(RECALLTIME, recalDate)

        db.insert(HABITS_TABLE,null, values)
        db.close()
    }
    fun addNewStreak(
        dateAcomplieshed : String?
    ){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(DATE_ACCOMPLISHED,dateAcomplieshed)

        db.insert(STREAK_TABLE, null, values)

        db.close()

    }
}
