package com.example.habittracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val habitsQuery = """
            CREATE TABLE $HABITS_TABLE (
                $ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $HABIT_NAME TEXT,
                $DEFAULT_HABIT BOOLEAN,
                $CREATE_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                $RECALLTIME TIME,
                $PICKED_UP BOOLEAN
                
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

        //insert default habits
        insertDefaultHabits(db)
    }

    private fun insertDefaultHabits(db: SQLiteDatabase?) {
        addNewHabit("Drink Water",true,currentDate(),null,db)
        addNewHabit("Workout",true,currentDate(),null,db)
        addNewHabit("Read a book",true,currentDate(),null,db)
        addNewHabit("Review",true,currentDate(),null,db)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $HABITS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $STREAK_TABLE")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "HabitTrackerDB"
        const val DB_VERSION = 3

        // Habits Table Columns
        const val HABITS_TABLE = "habitsTable"
        const val ID_COL = "id"
        const val HABIT_NAME = "habitName"
        const val DEFAULT_HABIT = "defaultHabit"
        const val CREATE_AT = "createdAt"
        const val RECALLTIME = "recallTime"
        const val PICKED_UP ="pickedUp"

        // Streaks Table Columns
        const val STREAK_TABLE = "streakTable"
        const val STREAK_ID_COL = "streakId"
        const val HABIT_ID = "habitId"
        const val DATE_ACCOMPLISHED = "dateAccomplished"
    }
    fun addNewHabit(habitsName : String,
                    defaultHabit : Boolean = true,
                    creationDate: String,
                    recalDate : String?,
                    db: SQLiteDatabase?,
                    pickedUp : MutableState<Boolean> = mutableStateOf(false ),

    ){
        val values = ContentValues()
        if(db != null) {
            values.put(HABIT_NAME, habitsName)
            values.put(DEFAULT_HABIT, defaultHabit)
            values.put(CREATE_AT, creationDate)
            values.put(RECALLTIME, recalDate)
            values.put(PICKED_UP, pickedUp.value)

            db.insert(HABITS_TABLE, null, values)
        }
    }
    fun addNewStreak( db:SQLiteDatabase?,
        dateAcomplieshed : String?
    ){
        val values = ContentValues()
        if(db != null) {
            values.put(DATE_ACCOMPLISHED, dateAcomplieshed)

            db.insert(STREAK_TABLE, null, values)

        }

    }
    fun readHabits() : MutableList<HabitModel>{
        val db = this.readableDatabase

        val cursorHabits : Cursor = db.rawQuery("SELECT * FROM  $HABITS_TABLE",null)

        val habitModelList = mutableStateListOf<HabitModel>()
        if(cursorHabits.moveToFirst()){
            do {
                habitModelList.add(
                    HabitModel(
                        cursorHabits.getString(1),
                        cursorHabits.getInt(2) == 1,
                        cursorHabits.getString(3),
                        cursorHabits.getString(4),
                        mutableStateOf( cursorHabits.getInt(5)==1)

                    )

                )
            }while (cursorHabits.moveToNext())
        }

        cursorHabits.close()
        return habitModelList
    }

}

fun currentDate():String{
    val date = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = date.format(formatter).toString()

    return formattedDate

}

