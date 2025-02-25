package com.org.sgame.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun GameBoard(
    gameModel: SnakeGameModel,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw background
        drawRect(
            color = Color(0xFF9ACD32), // Light green background
            size = size
        )
        
        // Draw grid lines (optional)
        drawGrid()
        
        // Draw snake
        drawSnake(gameModel)
        
        // Draw food
        drawFood(gameModel)
    }
}

private fun DrawScope.drawSnake(gameModel: SnakeGameModel) {
    val cellSize = size.width / GRID_SIZE
    
    // Draw each segment of the snake
    gameModel.snake.forEachIndexed { index, segment ->
        val x = segment.x * cellSize
        val y = segment.y * cellSize
        
        // Head is darker green, body is lighter
        val color = if (index == 0) Color(0xFF006400) else Color(0xFF228B22)
        
        drawRect(
            color = color,
            topLeft = Offset(x, y),
            size = Size(cellSize, cellSize)
        )
    }
}

private fun DrawScope.drawFood(gameModel: SnakeGameModel) {
    val cellSize = size.width / GRID_SIZE
    
    // Access the food position from the Food object
    val foodPosition = gameModel.food.position
    val x = foodPosition.x * cellSize
    val y = foodPosition.y * cellSize
    
    // Draw food as red circle or golden circle based on type
    val foodColor = when (gameModel.food.type) {
        FoodType.REGULAR -> Color.Red
        FoodType.BOOSTER -> Color(0xFFFFD700) // Gold color for booster
    }
    
    drawCircle(
        color = foodColor,
        radius = cellSize / 2,
        center = Offset(x + cellSize / 2, y + cellSize / 2)
    )
}

private fun DrawScope.drawGrid() {
    val cellSize = size.width / GRID_SIZE
    val gridColor = Color.Black.copy(alpha = 0.1f)
    
    // Draw vertical lines
    for (i in 1 until GRID_SIZE) {
        drawLine(
            color = gridColor,
            start = Offset(i * cellSize, 0f),
            end = Offset(i * cellSize, size.height),
            strokeWidth = 1.dp.toPx()
        )
    }
    
    // Draw horizontal lines
    for (i in 1 until GRID_SIZE) {
        drawLine(
            color = gridColor,
            start = Offset(0f, i * cellSize),
            end = Offset(size.width, i * cellSize),
            strokeWidth = 1.dp.toPx()
        )
    }
}