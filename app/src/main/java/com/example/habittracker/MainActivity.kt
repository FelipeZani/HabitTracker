package com.example.habittracker

import android.os.Bundle
import android.service.autofill.OnClickAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), //main container of the app
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {}
                            ){
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Open a menu to add an new habit")
                            }
                        }

                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)
                        .padding(16.dp)
                    ) {
                        HabitStreak()
                    }
                }
            }
        }
    }
}

@Composable
fun HabitStreak() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)){
        items(count = 7){
            Button(onClick = {}) { }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowContent() {
    HabitStreak()
}