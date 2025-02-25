package com.org.sgame.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.org.sgame.ui.theme.MenuSelectionBackground
import com.org.sgame.ui.theme.MenuSelectionText
import com.org.sgame.ui.theme.NokiaScreenBackground
import com.org.sgame.ui.theme.NokiaScreenPixels
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

enum class GameScreen {
    MAIN_MENU,
    LEVEL_SELECT,
    GAME_TYPE,
    PLAYING,
    PAUSED,
    GAME_OVER
}

enum class GameLevel {
    LEVEL_1,
    LEVEL_2,
    LEVEL_3,
    LEVEL_4,
    LEVEL_5,
    LEVEL_6,
    LEVEL_7,
    LEVEL_8,
    LEVEL_9;
    
    fun getSpeedMultiplier(): Float = when (this) {
        LEVEL_1 -> 1.0f
        LEVEL_2 -> 0.9f
        LEVEL_3 -> 0.8f
        LEVEL_4 -> 0.7f
        LEVEL_5 -> 0.6f
        LEVEL_6 -> 0.5f
        LEVEL_7 -> 0.4f
        LEVEL_8 -> 0.3f
        LEVEL_9 -> 0.2f
    }
    
    fun getLabel(): String = when (this) {
        LEVEL_1 -> "1"
        LEVEL_2 -> "2"
        LEVEL_3 -> "3"
        LEVEL_4 -> "4"
        LEVEL_5 -> "5"
        LEVEL_6 -> "6"
        LEVEL_7 -> "7"
        LEVEL_8 -> "8"
        LEVEL_9 -> "9"
    }

    fun getFoodFrequency(): Int = ordinal + 1
}

enum class GameType {
    STANDARD,
    NO_WALLS,
    MAZE;
    
    fun getLabel(): String = when (this) {
        STANDARD -> "Standard"
        NO_WALLS -> "No Walls"
        MAZE -> "Maze"
    }
}

class GameState {
    var currentScreen by mutableStateOf(GameScreen.MAIN_MENU)
    var selectedMenuIndex by mutableStateOf(0)
    var level by mutableStateOf(GameLevel.LEVEL_1)
    var gameType by mutableStateOf(GameType.STANDARD)
    var hasSavedGame by mutableStateOf(false)
    var highScore by mutableStateOf(0)
    
    // Menu options for each screen
    fun getCurrentMenuOptions(): List<String> = when (currentScreen) {
        GameScreen.MAIN_MENU -> listOf("New game", "Continue", "Level", "Game type")
        GameScreen.LEVEL_SELECT -> GameLevel.values().map { "Level ${it.getLabel()}" }
        GameScreen.GAME_TYPE -> GameType.values().map { it.getLabel() }
        else -> emptyList()
    }
    
    // Handle menu navigation
    fun navigateMenu(isUp: Boolean) {
        val options = getCurrentMenuOptions()
        if (options.isEmpty()) return
        
        selectedMenuIndex = if (isUp) {
            (selectedMenuIndex - 1 + options.size) % options.size
        } else {
            (selectedMenuIndex + 1) % options.size
        }
    }
    
    // Handle select action
    fun handleSelectAction() {
        when (currentScreen) {
            GameScreen.MAIN_MENU -> {
                when (selectedMenuIndex) {
                    0 -> { // New game
                        hasSavedGame = false
                        currentScreen = GameScreen.PLAYING
                    }
                    1 -> { // Continue
                        hasSavedGame = true
                        currentScreen = GameScreen.PLAYING
                    }
                    2 -> { // Level
                        currentScreen = GameScreen.LEVEL_SELECT
                        selectedMenuIndex = level.ordinal
                    }
                    3 -> { // Game type
                        currentScreen = GameScreen.GAME_TYPE
                        selectedMenuIndex = gameType.ordinal
                    }
                }
            }
            GameScreen.LEVEL_SELECT -> {
                level = GameLevel.values()[selectedMenuIndex]
                currentScreen = GameScreen.MAIN_MENU
                selectedMenuIndex = 0
            }
            GameScreen.GAME_TYPE -> {
                gameType = GameType.values()[selectedMenuIndex]
                currentScreen = GameScreen.MAIN_MENU
                selectedMenuIndex = 0
            }
            GameScreen.PLAYING -> {
                // Toggle pause
                currentScreen = GameScreen.PAUSED
            }
            GameScreen.PAUSED -> {
                // Resume game
                currentScreen = GameScreen.PLAYING
            }
            GameScreen.GAME_OVER -> {
                // Start new game
                hasSavedGame = false
                currentScreen = GameScreen.PLAYING
            }
        }
    }
    
    // Handle back action
    fun handleBackAction() {
        when (currentScreen) {
            GameScreen.LEVEL_SELECT, GameScreen.GAME_TYPE -> {
                currentScreen = GameScreen.MAIN_MENU
                selectedMenuIndex = 0
            }
            GameScreen.PAUSED -> {
                // Save game state and go back to main menu
                hasSavedGame = true
                currentScreen = GameScreen.MAIN_MENU
                selectedMenuIndex = 1 // Position at 'Continue'
            }
            GameScreen.GAME_OVER -> {
                currentScreen = GameScreen.MAIN_MENU
                selectedMenuIndex = 0
            }
            else -> {}
        }
    }
}

@Composable
fun GameMenuUI(
    gameState: GameState,
    onSelect: () -> Unit,
    onBack: () -> Unit,
    onNavigate: (Boolean) -> Unit
) {
    val menuOptions = gameState.getCurrentMenuOptions()
    val coroutineScope = rememberCoroutineScope()
    
    // Use LazyListState for scrolling
    val scrollState = rememberLazyListState()
    
    // Auto-scroll to selected item when navigating with d-pad
    LaunchedEffect(gameState.selectedMenuIndex) {
        if (menuOptions.isNotEmpty()) {
            scrollState.animateScrollToItem(gameState.selectedMenuIndex)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Game title
        Text(
            text = "S Game",
            color = NokiaScreenPixels,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        // Determine if we need scroll indicators
        // Show scroll indicators when there are items off-screen
        val showTopIndicator by remember {
            derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
        }
        
        val showBottomIndicator by remember {
            derivedStateOf {
                val lastVisibleItem = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItem < menuOptions.size - 1
            }
        }
        
        // Top scroll indicator
        if (showTopIndicator) {
            Text(
                text = "▲",
                color = NokiaScreenPixels,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }
        
        // Menu options in scrollable list
        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(menuOptions) { index, option ->
                val isSelected = index == gameState.selectedMenuIndex
                
                // Calculate distance from selected item for visual effect
                val distanceFromSelected = abs(index - gameState.selectedMenuIndex)
                val alpha = if (isSelected) 1f else max(1f - (distanceFromSelected * 0.2f), 0.5f)

                // Animate text color and background changes
                val backgroundColor by animateColorAsState(
                    if (isSelected) MenuSelectionBackground else Color.Transparent,
                    animationSpec = tween(150)
                )
                
                val textColor by animateColorAsState(
                    if (isSelected) MenuSelectionText else NokiaScreenPixels,
                    animationSpec = tween(150)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .alpha(alpha)
                        .background(backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            gameState.selectedMenuIndex = index
                            onSelect()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = if (isSelected) FontStyle.Italic else FontStyle.Normal
                    )
                }
            }
        }
        
        // Bottom scroll indicator
        if (showBottomIndicator) {
            Text(
                text = "▼",
                color = NokiaScreenPixels,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
        
        // Bottom navigation elements
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Select button
            Text(
                text = "Select",
                color = NokiaScreenPixels,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSelect() }
            )
            
            // Back button
            Text(
                text = "Back",
                color = NokiaScreenPixels,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
} 