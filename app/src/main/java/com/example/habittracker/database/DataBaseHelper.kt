package com.example.habittracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.habittracker.CalendarManagement


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
        val habitCreationDate = CalendarManagement()
        addNewHabit("Drink Water",true,habitCreationDate.getCurentDate(),null,db)
        addNewHabit("Workout",true,habitCreationDate.getCurentDate(),null,db)
        addNewHabit("Read a book",true,habitCreationDate.getCurentDate(),null,db)
        addNewHabit("Review",true,habitCreationDate.getCurentDate(),null,db)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $HABITS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $STREAK_TABLE")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "HabitTrackerDB"
        const val DB_VERSION = 4

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
                    pickedUp : MutableState<Boolean> = mutableStateOf(false),

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

    fun removeHabit(habitName : String,
                    db: SQLiteDatabase?
                    ){
        if(db != null) {
            val whereClause = "$HABIT_NAME = ?"
            db.delete(HABITS_TABLE, whereClause, arrayOf(habitName))
        }
    }



    fun updateDb(habitsList : MutableList<HabitModel>){
        val db = this.writableDatabase ?: throw NullPointerException()

        try {
            val existingHabits = readCustomHabits()

            habitsList.forEach { newHabit ->
                val habitIndex = getHabitId(newHabit.habitName, db)
                if ( habitIndex < 1) { //an id starts at 1, therefore the 0 value means the habit doesn't exist in the Db, we create it
                    addNewHabit(
                        newHabit.habitName,
                        newHabit.defaultHabit,
                        newHabit.creationDate,
                        newHabit.recalDate,
                        db,
                        newHabit.pickedUp
                    )
                    Log.v("MainActivity","${newHabit.habitName} was created in your db, better to check")
                } else{//if a habit exists, we need to check if this habit was modified
                    if(newHabit.pickedUp.value != getOldPickedUpState(habitIndex,db) ){
                        updateHabitPickedValue(newHabit.pickedUp.value, habitIndex, db)
                    }
                }
            }
            existingHabits.forEach {
                oldHabit ->
                if (!habitsList.contains(oldHabit) && !oldHabit.defaultHabit) {
                    removeHabit(oldHabit.habitName, db)
                    Log.v("MainActivity","Habit deleted")
                }
            }


        } finally {
            db.close()
        }


    }

    fun updateHabitPickedValue(pickedUpValue : Boolean , habitIndex : Int , db: SQLiteDatabase?){
        val values = ContentValues().apply {
            put(PICKED_UP, pickedUpValue)
        }

        // Updating record
        val rowsAffected = db?.update(HABITS_TABLE, values, "$ID_COL = ?", arrayOf(habitIndex.toString())) ?: 0
        if(rowsAffected == 0){
            throw NullPointerException()
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
    fun getOldPickedUpState(index: Int, db: SQLiteDatabase?) : Boolean {

        val query = "SELECT $PICKED_UP FROM $HABITS_TABLE WHERE $ID_COL = ?"

        val cursorHabit = db?.rawQuery(query, arrayOf( index.toString())) ?: throw NullPointerException()

        cursorHabit.use { cursor->
            return if (cursor.moveToFirst()){
                cursor.getInt(0) == 1
            }else{
                false
            }
        }

    }
    fun getHabitId(habitName : String, db: SQLiteDatabase?):Int{//this fonction returns the habit index?
        if(db == null) {
            throw NullPointerException()
        }
        val query = "SELECT $ID_COL FROM $HABITS_TABLE WHERE $HABIT_NAME=?"
        val cursor = db.rawQuery(query, arrayOf(habitName))
        cursor.use {

            return if(it.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    fun readCustomHabits() : MutableList<HabitModel>{

        val habitList = mutableListOf<HabitModel>()

        val db = this.readableDatabase ?: throw IllegalStateException("Database is not readable")

        val customDefaultVal = 0

        val query = "SELECT * FROM $HABITS_TABLE WHERE $DEFAULT_HABIT=?"

        val cursorHabit = db.rawQuery(query, arrayOf(customDefaultVal.toString()))
        try {
            if(cursorHabit.moveToFirst()) {
                do {
                    habitList.add(
                        HabitModel(
                            cursorHabit.getString(1),
                            cursorHabit.getInt(2) == 1,
                            cursorHabit.getString(3),
                            cursorHabit.getString(4),
                            mutableStateOf( cursorHabit.getInt(5)==1)
                        )
                    )
                }while (cursorHabit.moveToNext())
            }
        }catch (e: Exception) {
            Log.e("DatabaseError", "Error reading custom habits", e)
        }
        finally {
          cursorHabit.close()
        }
        return habitList
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


