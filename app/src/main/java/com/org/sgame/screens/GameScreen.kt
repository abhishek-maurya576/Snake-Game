package com.org.sgame.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.org.sgame.game.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    modifier: Modifier = Modifier
) {
    val gameModel = remember { SnakeGameModel() }
    val coroutineScope = rememberCoroutineScope()
    
    // Game update logic with timer
    LaunchedEffect(key1 = gameModel) {
        while (true) {
            delay(200) // Update every 200ms
            if (!gameModel.isPaused && !gameModel.isGameOver) {
                gameModel.update()
                
                // Play sound when food is eaten
                if (gameModel.score > 0 && gameModel.snake.size == gameModel.score + 4) {
                    soundManager.playEatFood()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Snake Game") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Game board
            GameBoard(
                gameModel = gameModel,
                modifier = Modifier.fillMaxSize()
            )
            
            // Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Direction controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { gameModel.setDirection(Direction.LEFT) },
                        modifier = Modifier.size(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400)
                        )
                    ) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { gameModel.setDirection(Direction.UP) },
                        modifier = Modifier.size(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400)
                        )
                    ) {
                        Text(
                            text = "↑",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { gameModel.setDirection(Direction.DOWN) },
                        modifier = Modifier.size(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400)
                        )
                    ) {
                        Text(
                            text = "↓",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { gameModel.setDirection(Direction.RIGHT) },
                        modifier = Modifier.size(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400)
                        )
                    ) {
                        Text(
                            text = "→",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Game controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Pause button
                    Button(
                        onClick = { 
                            gameModel.togglePause()
                            soundManager.playClick()
                        },
                        modifier = Modifier.size(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gameModel.isPaused) Color(0xFF8B0000) else Color(0xFF006400)
                        )
                    ) {
                        Text(
                            text = if (gameModel.isPaused) "▶" else "II",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Restart button
                    Button(
                        onClick = { 
                            gameModel.resetGame()
                            soundManager.playClick()
                        },
                        enabled = gameModel.isGameOver || gameModel.isPaused,
                        modifier = Modifier.size(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400),
                            disabledContainerColor = Color(0xFF006400).copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "↺",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Score display
            Text(
                text = "Score: ${gameModel.score}",
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                color = Color(0xFF006400),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Game over overlay
            if (gameModel.isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GAME OVER",
                            color = Color(0xFF8B0000),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Score: ${gameModel.score}",
                            color = Color(0xFF006400),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { gameModel.resetGame() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF006400)
                            )
                        ) {
                            Text(
                                text = "Play Again",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}