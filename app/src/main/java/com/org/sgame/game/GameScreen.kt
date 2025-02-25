package com.org.sgame.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun GameScreen(
    soundManager: SoundManager,
    modifier: Modifier = Modifier
) {
    val gameModel = remember { SnakeGameModel() }
    val gameState = remember { GameState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Handle level-based settings
    LaunchedEffect(gameState.level) {
        // Update game model with level-specific settings
        gameModel.gameType = gameState.gameType
        when (gameState.level) {
            GameLevel.LEVEL_6, GameLevel.LEVEL_7, GameLevel.LEVEL_8, GameLevel.LEVEL_9 -> {
                // Higher levels get more obstacles in maze mode
                if (gameState.gameType == GameType.MAZE) {
                    gameModel.addMazeObstacles((gameState.level.ordinal - 5) * 2)
                }
            }
            else -> {}
        }
    }
    
    // Handle navigation from game state to actual gameplay
    LaunchedEffect(gameState.currentScreen) {
        when (gameState.currentScreen) {
            GameScreen.PLAYING -> {
                if (!gameState.hasSavedGame) {
                    gameModel.resetGame()
                }
                gameState.hasSavedGame = false
            }
            GameScreen.PAUSED -> {
                gameModel.togglePause()
            }
            else -> {}
        }
    }
    
    // Adjust game speed based on level and score
    val baseSpeed = 300
    val levelMultiplier = gameState.level.getSpeedMultiplier()
    val scoreMultiplier = maxOf(0.5f, 1f - (gameModel.score * 0.01f))
    val gameSpeed by remember(gameModel.score, gameState.level) {
        mutableStateOf((baseSpeed * levelMultiplier * scoreMultiplier).toLong())
    }
    
    // Game update logic with timer - only active during gameplay
    LaunchedEffect(key1 = gameModel, key2 = gameSpeed, key3 = gameState.currentScreen) {
        while (gameState.currentScreen == GameScreen.PLAYING) {
            delay(gameSpeed) // Update speed based on level and score
            if (!gameModel.isPaused && !gameModel.isGameOver) {
                gameModel.update()
                
                // Only play move sound occasionally to avoid sound spam
                if (gameModel.score % 3 == 0) {
                    soundManager.playMove()
                }
            }
        }
    }
    
    // Listen for score changes to play sound
    LaunchedEffect(key1 = gameModel.score) {
        if (gameModel.score > 0 && gameState.currentScreen == GameScreen.PLAYING) {
            soundManager.playEatFood()
            
            // Apply score multiplier based on level
            val scoreMultiplier = when (gameState.level) {
                GameLevel.LEVEL_1 -> 1.0f
                GameLevel.LEVEL_2 -> 1.2f
                GameLevel.LEVEL_3 -> 1.5f
                GameLevel.LEVEL_4 -> 1.8f
                GameLevel.LEVEL_5 -> 2.0f
                GameLevel.LEVEL_6 -> 2.5f
                GameLevel.LEVEL_7 -> 3.0f
                GameLevel.LEVEL_8 -> 4.0f
                GameLevel.LEVEL_9 -> 5.0f
            }
            
            val actualScore = (gameModel.score.toFloat() * scoreMultiplier).toInt()
            
            if (actualScore > gameState.highScore) {
                gameState.highScore = actualScore
            }
        }
    }
    
    // Listen for game over to play sound and update state
    LaunchedEffect(key1 = gameModel.isGameOver) {
        if (gameModel.isGameOver && gameState.currentScreen == GameScreen.PLAYING) {
            soundManager.playGameOver()
            gameState.currentScreen = GameScreen.GAME_OVER
        }
    }
    
    // Touch controls for all screens
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Add touch controls based on current screen
            .pointerInput(gameState.currentScreen) {
                when (gameState.currentScreen) {
                    // Game playing - swipe to change direction
                    GameScreen.PLAYING -> {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            
                            // Determine swipe direction based on which axis had larger movement
                            val (x, y) = dragAmount
                            if (abs(x) > abs(y)) {
                                // Horizontal swipe
                                if (x > 0) {
                                    gameModel.setDirection(Direction.RIGHT)
                                } else {
                                    gameModel.setDirection(Direction.LEFT)
                                }
                            } else {
                                // Vertical swipe
                                if (y > 0) {
                                    gameModel.setDirection(Direction.DOWN)
                                } else {
                                    gameModel.setDirection(Direction.UP)
                                }
                            }
                            
                            soundManager.playClick()
                        }
                    }
                    
                    // Menu screens - tap handling
                    GameScreen.MAIN_MENU, GameScreen.LEVEL_SELECT, GameScreen.GAME_TYPE -> {
                        detectTapGestures { tapOffset ->
                            // Get the height of each menu item (approximation)
                            val menuItemHeight = size.height / (gameState.getCurrentMenuOptions().size + 2)
                            
                            // Calculate which menu item was tapped based on Y position
                            val index = (tapOffset.y / menuItemHeight).toInt() - 1
                            
                            // Check if tap is within valid menu items
                            if (index in 0 until gameState.getCurrentMenuOptions().size) {
                                gameState.selectedMenuIndex = index
                                gameState.handleSelectAction()
                                soundManager.playClick()
                            }
                            // Check for "Back" button tap (bottom of screen)
                            else if (tapOffset.y > size.height * 0.85f) {
                                gameState.handleBackAction()
                                soundManager.playClick()
                            }
                        }
                    }
                    
                    // Pause screen - tap to resume
                    GameScreen.PAUSED -> {
                        detectTapGestures { tapOffset ->
                            // Resume if tap in center area
                            if (tapOffset.y < size.height * 0.7f) {
                                gameState.handleSelectAction()
                                gameModel.togglePause()
                                soundManager.playClick()
                            }
                            // Quit if tap at bottom
                            else if (tapOffset.y > size.height * 0.7f) {
                                gameState.handleBackAction()
                                soundManager.playClick()
                            }
                        }
                    }
                    
                    // Game over screen - tap to play again
                    GameScreen.GAME_OVER -> {
                        detectTapGestures { tapOffset ->
                            // Play again if tap in center area
                            if (tapOffset.y < size.height * 0.7f) {
                                gameState.handleSelectAction()
                                soundManager.playClick()
                            }
                            // Menu if tap at bottom
                            else if (tapOffset.y > size.height * 0.7f) {
                                gameState.handleBackAction()
                                soundManager.playClick()
                            }
                        }
                    }
                }
            }
    ) {
        NokiaPhone(
                    modifier = Modifier.fillMaxSize(),
            gameContent = {
                // Show different content based on current screen
                when (gameState.currentScreen) {
                    GameScreen.MAIN_MENU, GameScreen.LEVEL_SELECT, GameScreen.GAME_TYPE -> {
                        GameMenuUI(
                            gameState = gameState,
                            onSelect = { 
                                gameState.handleSelectAction()
                                soundManager.playClick()
                            },
                            onBack = {
                                gameState.handleBackAction()
                                soundManager.playClick()
                            },
                            onNavigate = { isUp ->
                                gameState.navigateMenu(isUp)
                                soundManager.playClick()
                            }
                        )
                    }
                    GameScreen.PLAYING -> {
                        GameCanvas(
                            gameModel = gameModel,
                            gameType = gameState.gameType,
                            gameLevel = gameState.level,
                            onPauseClicked = {
                                gameModel.togglePause()
                                if (gameModel.isPaused) {
                                    gameState.currentScreen = GameScreen.PAUSED
                                }
                                soundManager.playClick()
                            }
                        )
                    }
                    GameScreen.PAUSED -> {
                        // Show paused screen with snake in background
                        Box(modifier = Modifier.fillMaxSize()) {
                            GameCanvas(
                                gameModel = gameModel,
                                gameType = gameState.gameType,
                                gameLevel = gameState.level
                            )
                            PauseOverlay(
                                score = gameModel.score,
                                level = gameState.level,
                                onContinue = {
                                    gameState.handleSelectAction()
                                    gameModel.togglePause()
                                }
                            )
                        }
                    }
                    GameScreen.GAME_OVER -> {
                        // Show game over screen with final score
                        GameOverScreen(
                            score = (gameModel.score.toFloat() * when (gameState.level) {
                                GameLevel.LEVEL_1 -> 1.0f
                                GameLevel.LEVEL_2 -> 1.2f
                                GameLevel.LEVEL_3 -> 1.5f
                                GameLevel.LEVEL_4 -> 1.8f
                                GameLevel.LEVEL_5 -> 2.0f
                                GameLevel.LEVEL_6 -> 2.5f
                                GameLevel.LEVEL_7 -> 3.0f
                                GameLevel.LEVEL_8 -> 4.0f
                                GameLevel.LEVEL_9 -> 5.0f
                            }).toInt(),
                            highScore = gameState.highScore,
                            level = gameState.level,
                            onRestart = {
                                gameState.handleSelectAction()
                            }
                        )
                    }
                }
            },
            onDirectionPressed = { direction ->
                if (gameState.currentScreen == GameScreen.PLAYING) {
                    gameModel.setDirection(direction)
                    soundManager.playClick()
                } else {
                    // Use direction for menu navigation
                    when (direction) {
                        Direction.UP -> gameState.navigateMenu(true)
                        Direction.DOWN -> gameState.navigateMenu(false)
                        Direction.LEFT -> {} // No left action in menus
                        Direction.RIGHT -> {} // No right action in menus
                    }
                    soundManager.playClick()
                }
            },
            onCenterPressed = {
                // Center is "Select" in menus and "Pause" in game
                if (gameState.currentScreen == GameScreen.PLAYING) {
                    gameState.currentScreen = GameScreen.PAUSED
                    gameModel.togglePause()
                } else {
                    gameState.handleSelectAction()
                }
                soundManager.playClick()
            },
            onResetPressed = {
                // C button acts as "Back" in Nokia UI
                gameState.handleBackAction()
                soundManager.playClick()
            }
        )
    }
}

private fun max(a: Int, b: Int): Int = if (a > b) a else b