package com.example.habittracker.ui.theme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.ui.theme.ui.theme.HabitTrackerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            delay(3000) // Wait for 3 seconds
            // Navigate to MainActivity
            val intent = Intent(this@LoadingScreen, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
        setContent {
            HabitTrackerTheme {
                LoadingScreenUI()
                }
        }
    }
}

@Composable
fun LoadingScreenUI() {
    // Center the CircularProgressIndicator on the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlueF0,
                        BlueFF
                    )
                )
            )
        ,
        contentAlignment = Alignment.Center

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Open a menu to add a new habit",
                    tint = Color.Unspecified,

                )
                Text(
                    "Habit Tracker",
                    fontSize = 30.sp,
                    color = Color.White,
                )
            }
            Text("Create good habits with us",
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom=10.dp)
                )
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color= Color.White
            ) // Show a spinning progress indicator}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HabitTrackerTheme {
        LoadingScreenUI()
    }
}