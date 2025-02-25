package com.org.sgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.org.sgame.game.GameScreen
import com.org.sgame.game.SoundManager
import com.org.sgame.ui.theme.NokiaScreenBackground
import com.org.sgame.ui.theme.NokiaScreenPixels
import com.org.sgame.ui.theme.SGameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        soundManager = SoundManager(this)
        
        setContent {
            SGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showStartup by remember { mutableStateOf(true) }
                    
                    if (showStartup) {
                        NokiaStartupScreen(
                            soundManager = soundManager,
                            onStartupComplete = {
                                showStartup = false
                            }
                        )
                    } else {
                        GameScreen(soundManager = soundManager)
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun NokiaStartupScreen(
    soundManager: SoundManager,
    onStartupComplete: () -> Unit
) {
    var showNokiaLogo by remember { mutableStateOf(true) }
    
    LaunchedEffect(key1 = true) {
        // Play Nokia startup sound
        soundManager.playNokiaStartup()
        
        delay(2000) // Show Nokia logo for 2 seconds
        showNokiaLogo = false
        delay(1000) // Show Snake animation for 1 second
        onStartupComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NokiaScreenBackground),
        contentAlignment = Alignment.Center
    ) {
        if (showNokiaLogo) {
            // Nokia startup screen
            Text(
                text = "S Game",
                color = NokiaScreenPixels,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            // Snake loading animation
            Text(
                text = "Snake\nLoading...",
                color = NokiaScreenPixels,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SGameTheme {
        Greeting("Android")
    }
}