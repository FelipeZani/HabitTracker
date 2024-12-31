package com.example.habittracker
import android.content.Context
import com.example.habittracker.database.DataBaseHelper.*

import androidx.compose.ui.res.painterResource
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.database.DataBaseHelper
import com.example.habittracker.database.DataBaseHelper.Companion.DB_NAME
import com.example.habittracker.database.DataBaseHelper.Companion.DB_VERSION
import com.example.habittracker.database.HabitModel
import com.example.habittracker.ui.theme.BlueRoyal
import com.example.habittracker.ui.theme.DarkTransparent
import com.example.habittracker.ui.theme.GoldenBell
import com.example.habittracker.ui.theme.HabitTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    private val databaseHabits : DataBaseHelper = DataBaseHelper(context=this)
    private lateinit var habitsList :MutableList<HabitModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        habitsList = databaseHabits.readHabits()

        setContent {
            HabitTrackerTheme {
                ContentApp(habitsList)

            }
        }

    }

    override fun onPause() {
        super.onPause()
        databaseHabits.updateDb(habitsList)
    }

    override fun onResume() {
        super.onResume()
        habitsList = databaseHabits.readHabits()

    }
}

@Composable
fun ContentApp(habitsData: MutableList<HabitModel>?){ //Whole content in the scaffold is stored here, Ui and Ux

    var isAddHabitsMenuVisible  by remember { mutableStateOf(true) }

    val habitsOnMenu  = remember { mutableStateListOf<HabitModel>()}

    val snackBarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize(), //main container of the app
        floatingActionButton = {
            FloatingActionButton(
            onClick = {isAddHabitsMenuVisible = !isAddHabitsMenuVisible},
            modifier = Modifier.alpha(if(isAddHabitsMenuVisible) 0f else 1f),
            containerColor = BlueRoyal

            ){
                Icon(imageVector = Icons.Default.Add, contentDescription = "Open a menu to add an new habit")
            }
        },

    ) {
        innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxWidth(),
        ) {
            HabitStreak()
            HabitMenu(habitsOnMenu)

        }
        if(isAddHabitsMenuVisible){
            AddHabitsToMenu(habitsData, onDismiss = {isAddHabitsMenuVisible = false}, habitsOnMenu,scope,snackBarHostState)

        }
    }

}
@Composable
fun HabitStreak() {
    val calendar = remember { CalendarManagement() }
    var dayOffset by remember { mutableIntStateOf(0) } // Track offset of days

    // Update the state when arrows are clicked
    fun updateOffset(value: Int) {
        dayOffset += value   //if value < 0 : goes back in the offset; if > 0 : goes foward
        calendar.updateDate(value)
    }

    Column(
        modifier = Modifier.padding(top = 30.dp)
    ) {
        Row( //arrow's logic to navigate through the calendar
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Arrow back to come back the list of days",
                modifier = Modifier.clickable {
                    updateOffset(-3) // Go back 3 days
                }
            )
            Icon(
                Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = "Arrow forward to move forward the list of days",
                modifier = Modifier.clickable {
                    updateOffset(3) // Move forward 3 days
                }
            )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(space = 4.dp,
                alignment = Alignment.CenterHorizontally),
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth()
        ) { //handle the UI of the calendar
            items(count = 7) { index ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                       ,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .width(40.dp)
                            .height(48.dp)
                            .padding(4.dp)
                    ) {
                        // Display updated weeks and days based on current offset
                        Text(
                            text = calendar.getWeek(dayOffset)[index],
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${calendar.getDay(dayOffset)[index]}",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddHabitsToMenu(habitsList:MutableList<HabitModel>?,onDismiss: () -> Unit, habitsOnMenu : MutableList<HabitModel>, scope: CoroutineScope,snackBarHostState : SnackbarHostState){
    // UI of the habits list available to add to the user's habit list
    if(habitsList == null){
        Toast.makeText(LocalContext.current,"Empty database",Toast.LENGTH_LONG).show()
        return
    }

    var habitsStatusText by remember { mutableStateOf("Add")}
    var displayRemoveOption = remember {
        mutableStateMapOf<HabitModel, Boolean>().apply {
            habitsOnMenu.forEach { habit ->
                 put(habit, false)

            }
        }

    }
    Box(
        modifier = Modifier
            .background(DarkTransparent)
            .fillMaxSize()
            .clickable(
                onClick = { onDismiss() }
            )
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.8f),

            shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )

        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth()
                ) {
                items(habitsList.size){ index->
                    val habitItem = habitsList[index]
                    Box(
                    ) {
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 20.dp)
                                .combinedClickable(
                                    onClick = {
                                        // Handle click
                                    },
                                    onLongClick = {
                                        if(!habitItem.defaultHabit){
                                            displayRemoveOption[habitItem]=true
                                        }
                                    }
                                ),
                        ) {
                            Text(
                                habitItem.habitName,
                                fontSize = 20.sp,
                                modifier = Modifier.weight(0.5f)
                            )
                            Button(
                                onClick = {
                                    if (habitItem.pickedUp.value) {

                                        habitItem.pickedUp.value = !habitItem.pickedUp.value
                                        habitsOnMenu.remove(habitItem)
                                        habitsStatusText = "Add"

                                    } else {

                                        habitItem.pickedUp.value = !habitItem.pickedUp.value
                                        habitsOnMenu.add(habitItem)
                                        habitsStatusText = "Remove"

                                    }
                                },
                                modifier = Modifier
                                    .padding(end = 20.dp)
                                    .weight(0.24f)


                            ) {
                                habitsStatusText = if (habitItem.pickedUp.value) "Remove" else "Add"
                                Text(
                                    text = habitsStatusText,
                                )

                            }

                        }
                        if(displayRemoveOption[habitItem]==true){
                            Surface(
                                color= BlueRoyal,
                                modifier = Modifier.wrapContentWidth()
                                    .padding(start=150.dp)
                                    .clickable {
                                        habitsList.remove(habitItem)
                                    },
                                shape= RoundedCornerShape(5.dp)
                                ,
                                contentColor = Color.White,
                            ) {
                                Text(
                                    text="Remove",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(1.dp)

                                )
                            }
                        }
                    }
                }

            item{
                CreateCustomHabit( habitsList,scope,snackBarHostState)
            }

            }

        }
    }
}
@Composable
fun HabitMenu(habitsOnMenu: MutableList<HabitModel>?) {
    if (!habitsOnMenu.isNullOrEmpty()) {
        // Maintain a map of each habit's ID to its tint color
        val accomplishedTintValues = remember {
            mutableStateMapOf<HabitModel, Color>().apply {
                habitsOnMenu.forEach { habit ->
                    put(habit, Color.Black) // Initialize all items with black color
                }
            }
        }
        val notificationBellTint = remember {
            mutableStateMapOf<HabitModel,Color>().apply {
                habitsOnMenu.forEach {
                    habit->
                    put(habit, Color.Black)
                }

            }

        }
        LazyColumn(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(habitsOnMenu.size) { index ->
                val habit = habitsOnMenu[index]
                Card(
                    modifier = Modifier
                        .height(75.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .weight(0.5f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontSize = 20.sp,
                            text = habit.habitName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.weight(0.3f))
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Turn notifications On/Off",
                            tint = notificationBellTint[habit] ?: Color.Black,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    notificationBellTint[habit] =
                                        if (notificationBellTint[habit] == Color.Black) {
                                            GoldenBell
                                        } else {
                                            Color.Black
                                        }
                                }

                        )
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Controlling Habit",
                            tint = accomplishedTintValues[habit] ?: Color.Black,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    accomplishedTintValues[habit] =
                                        if (accomplishedTintValues[habit] == Color.Black) {
                                            Color.Green
                                        } else {
                                            Color.Black
                                        }
                                }
                        )
                    }
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.iconemptyscreen),
                contentDescription = "Open a menu to add a new habit",
                tint = Color.Unspecified
            )
            Text("Start your journey by adding a Habit",
                fontSize = 20.sp
            )
        }
    }
}

fun checkNameValue(hName : String, scope: CoroutineScope, snackBarHostState: SnackbarHostState): Boolean{

    val regex = Regex("[a-zA-Z0-9 ]{2,20}")

    val isHabitNameValid = regex.matches(hName)

    if(isHabitNameValid){
        scope.launch { snackBarHostState.showSnackbar("Your new habit was added successfully") }
    }else if(hName.isEmpty()){
        scope.launch { snackBarHostState.showSnackbar("Habit's name can't be empty") }

    }else if (hName.length>20){
        scope.launch { snackBarHostState.showSnackbar("Habit's name can't exceed 20 characters") }

    }
    else
    {
        scope.launch{snackBarHostState.showSnackbar("Only alpha numeric values are excepted")}
    }
    return isHabitNameValid

}

@Composable
fun CreateCustomHabit(habitsList: MutableList<HabitModel>?, scope: CoroutineScope, snackBarHostState:SnackbarHostState){
    var isAddCustomHabitButtonOn by remember { mutableStateOf(false) }
    var isHabitNameValid by remember { mutableStateOf(false) }
    if(!habitsList.isNullOrEmpty()) {
        if (isAddCustomHabitButtonOn) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center

            ) {
                Button(
                    onClick = {
                        isAddCustomHabitButtonOn = !isAddCustomHabitButtonOn
                    }
                ) {
                    Text(text = "Add a new Habit")
                }
            }
        } else {
            var hName by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() }, // Prevent ripple effect
                        indication = null, // Remove visual feedback
                        onClick = { /* Do nothing, just consume the click */ }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = hName,
                    onValueChange = { hName = it },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .padding(top = 10.dp),

                    label = { Text(text = "Your Habit name") },
                )
                Icon(Icons.Default.Close, contentDescription = "Cancel bottom",
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .clickable { isAddCustomHabitButtonOn = !isAddCustomHabitButtonOn })
                Icon(Icons.Default.Check, contentDescription = "Confirm bottom",
                    modifier = Modifier.clickable {
                        isHabitNameValid = checkNameValue(hName, scope, snackBarHostState)
                        if (isHabitNameValid) {
                            val currDate = CalendarManagement()
                            val newHabit = HabitModel(hName, false, currDate.getCurentDate(), null)
                            hName=""
                            habitsList.add(newHabit)

                        }

                    }

                )

            }


        }
    }else{
        Toast.makeText(LocalContext.current,"HabitsList null or empty", Toast.LENGTH_LONG).show()
        exitProcess(70)

    }
}




@Preview(showSystemUi = true)
@Composable
fun ShowContentPreview() {
    // Create a mock or default instance of DataBaseHelper if needed

    HabitTrackerTheme {
        ContentApp(MockDataProvider.getMockHabits())

        //HabitMenu(MockPUPHabits.getPUPMockHabits())
    }
}


object MockDataProvider {
    fun getMockHabits(): ArrayList<HabitModel> = arrayListOf(
        HabitModel(
            habitName = "Drink Water",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp= mutableStateOf(false)
        ),
        HabitModel(
            habitName = "Workout",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp=mutableStateOf(false)

        ),
        HabitModel(
            habitName = "Read a book",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp=mutableStateOf(true)

        ),
        HabitModel(
            habitName = "Review",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp=mutableStateOf(true)

        )
    )
}
object MockPUPHabits{
    fun getPUPMockHabits(): ArrayList<HabitModel> = arrayListOf(
        HabitModel(
            habitName = "Review",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp=mutableStateOf(true)

        ),
        HabitModel(
            habitName = "Workout",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp=mutableStateOf(true)

        ),
        HabitModel(
            habitName = "Eat healthy",
            defaultHabit = true,
            creationDate = "2024-03-11",
            recalDate = null,
            pickedUp= mutableStateOf(true)

        )


    )

}