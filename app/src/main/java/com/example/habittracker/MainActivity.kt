package com.example.habittracker

import android.os.Bundle
import android.service.autofill.OnClickAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                ContentApp()

            }
        }
    }
}

@Composable
fun ContentApp(){ //Whole content in the scaffold is stored here, Ui and Ux
    var isAddHabitsMenuVisible  by remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize(), //main container of the app
        floatingActionButton = {
            FloatingActionButton(
            onClick = {isAddHabitsMenuVisible = !isAddHabitsMenuVisible}
            ){
                Icon(imageVector = Icons.Default.Add, contentDescription = "Open a menu to add an new habit")
            }
        }

    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            HabitStreak()


        }
    }
    if(isAddHabitsMenuVisible){
        HandleHabitsMenu(onDismiss = {isAddHabitsMenuVisible = false})

    }
}
@Composable
fun HabitStreak() {
    val calendar = remember { CalendarManagement() }
    var dayOffset = remember { mutableStateOf(0) } // Track offset of days

    // Update the state when arrows are clicked
    fun updateOffset(value: Int) {
        dayOffset.value += value   //if value < 0 : goes back in the offset; if > 0 : goes foward
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

        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 30.dp)
        ) { //handle the UI of the calendar
            items(count = 7) { index ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 4.dp),
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
                            text = calendar.getWeek(dayOffset.value)[index],
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${calendar.getDay(dayOffset.value)[index]}",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HandleHabitsMenu(onDismiss: () -> Unit){ // UI of the habits list available to add to the user's habit list

        Box(
            modifier = Modifier
                .background(Color.Transparent)
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
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),

                shape = RoundedCornerShape(35.dp, 35.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )

            ) {
                LazyColumn(modifier = Modifier.padding(top = 30.dp).fillMaxWidth()
                    ) {
                    items(100){
                        Row(
                            modifier = Modifier.wrapContentWidth()
                                .padding(start = 20.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() }, // Prevent ripple effect
                                    indication = null, // Remove visual feedback
                                    onClick = { /* Do nothing, just consume the click */ }
                                ),
                            horizontalArrangement = Arrangement.spacedBy(90.dp)
                        ) {
                            Text(
                                "Hello",
                                fontSize = 30.sp
                            )
                            Button(onClick = {}) { Text("Add") }
                        }
                    }
                }

            }
        }



}

@Preview(showSystemUi = true)
@Composable
fun ShowContent() {
    ContentApp()
}