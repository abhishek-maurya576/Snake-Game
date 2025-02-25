package com.org.sgame.game

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import com.org.sgame.ui.theme.NokiaScreenPixels
import kotlin.math.max
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.atan2
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

@Composable
fun GameCanvas(
    gameModel: SnakeGameModel,
    gameType: GameType = GameType.STANDARD,
    gameLevel: GameLevel = GameLevel.LEVEL_1,
    onPauseClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Animation state for food pulsing - moved outside Canvas
    val animationState = rememberInfiniteTransition()
    val pulseSizeMultiplier by animationState.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(modifier = modifier.fillMaxSize()) {
        // Score display with level indicator and pause button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score 
            Text(
                text = String.format("%04d", gameModel.score),
                color = NokiaScreenPixels,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Pause button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (gameModel.isPaused) NokiaScreenPixels else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = NokiaScreenPixels,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onPauseClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (gameModel.isPaused) "▶" else "II",
                    color = if (gameModel.isPaused) Color(0xFF9ACD32) else NokiaScreenPixels,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Level indicator
            Text(
                text = "L${gameLevel.ordinal + 1}",
                color = NokiaScreenPixels,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Game canvas
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp) // Make room for score display
        ) {
            val cellWidth = size.width / GRID_SIZE
            val cellHeight = size.height / GRID_SIZE
            
            // Draw different border styles based on game type
            when (gameType) {
                GameType.STANDARD -> {
                    // Standard border
                    drawRect(
                        color = NokiaScreenPixels,
                        style = Stroke(width = cellWidth / 8f)
                    )
                }
                GameType.NO_WALLS -> {
                    // Dotted border for no walls mode
                    val dashLength = 5.dp.toPx()
                    val gapLength = 3.dp.toPx()
                    val strokeWidth = cellWidth / 10f
                    
                    val path = Path()
                    path.moveTo(0f, 0f)
                    path.lineTo(size.width, 0f)
                    path.lineTo(size.width, size.height)
                    path.lineTo(0f, size.height)
                    path.close()
                    
                    drawPath(
                        path = path,
                        color = NokiaScreenPixels,
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(dashLength, gapLength), 0f
                            )
                        )
                    )
                }
                GameType.MAZE -> {
                    // Draw maze walls
                    for (x in 0 until GRID_SIZE step 3) {
                        for (y in 0 until GRID_SIZE step 4) {
                            if ((x + y) % 2 == 0) {
                                drawRect(
                                    color = NokiaScreenPixels,
                                    topLeft = Offset(
                                        x * cellWidth,
                                        y * cellHeight
                                    ),
                                    size = Size(
                                        cellWidth * 2f,
                                        cellHeight
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Draw snake with realistic features
            drawRealisticSnake(gameModel.snake, cellWidth, cellHeight, gameModel.currentDirection, gameModel.animationTick)
            
            // Draw food
            drawFood(gameModel.food, cellWidth, cellHeight, gameModel.animationTick)
            
            // Draw progress bar based on score
            if (gameModel.score > 0) {
                val progressWidth = min(size.width * gameModel.score / 50f, size.width)
                drawRect(
                    color = NokiaScreenPixels,
                    topLeft = Offset(0f, size.height - 4.dp.toPx()),
                    size = Size(progressWidth, 4.dp.toPx())
                )
            }
        }
        
        // Game over overlay
        if (gameModel.isGameOver) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GAME OVER\nScore: ${gameModel.score}\nPress C to restart",
                    color = NokiaScreenPixels,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        
        // Pause overlay
        if (gameModel.isPaused && !gameModel.isGameOver) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PAUSED\nPress center to continue",
                    color = NokiaScreenPixels,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawFood(food: Food, cellWidth: Float, cellHeight: Float, animationTick: Int) {
    // Create pulsating effect for food
    val pulseFactor = 0.2f * (Math.sin(animationTick * 0.2).toFloat() + 1f)
    val foodSize = cellWidth * (0.8f + pulseFactor * 0.2f)
    val offset = (cellWidth - foodSize) / 2
    
    val position = food.position
    
    when (food.type) {
        FoodType.REGULAR -> {
            // Regular apple-like food
            val foodColor = Color(0xFF8B0000) // Dark red
            
            // Apple body
            drawCircle(
                color = foodColor,
                radius = foodSize / 2,
                center = Offset(
                    position.x * cellWidth + cellWidth/2,
                    position.y * cellHeight + cellHeight/2
                )
            )
            
            // Apple stem
            drawLine(
                color = Color(0xFF654321),
                start = Offset(
                    position.x * cellWidth + cellWidth / 2,
                    position.y * cellHeight + offset / 2
                ),
                end = Offset(
                    position.x * cellWidth + cellWidth / 2 + foodSize * 0.1f,
                    position.y * cellHeight + offset / 4
                ),
                strokeWidth = cellWidth * 0.1f
            )
            
            // Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = foodSize / 5,
                center = Offset(
                    position.x * cellWidth + cellWidth / 2 - foodSize * 0.2f,
                    position.y * cellHeight + cellHeight / 2 - foodSize * 0.2f
                )
            )
        }
        
        FoodType.BOOSTER -> {
            // Booster food - golden apple with sparkle effect
            val boosterColor = Color(0xFFFFD700) // Gold
            
            // Golden apple body
            drawCircle(
                color = boosterColor,
                radius = foodSize / 2,
                center = Offset(
                    position.x * cellWidth + cellWidth/2,
                    position.y * cellHeight + cellHeight/2
                )
            )
            
            // Apple stem
            drawLine(
                color = Color(0xFF654321),
                start = Offset(
                    position.x * cellWidth + cellWidth / 2,
                    position.y * cellHeight + offset / 2
                ),
                end = Offset(
                    position.x * cellWidth + cellWidth / 2 + foodSize * 0.1f,
                    position.y * cellHeight + offset / 4
                ),
                strokeWidth = cellWidth * 0.1f
            )
            
            // Sparkle effect (animated)
            val sparklePhase = (animationTick % 20) / 20f
            val sparkleSize = foodSize * 0.7f * sparklePhase
            
            // Draw 4 sparkle lines
            for (i in 0 until 4) {
                val angle = i * Math.PI / 2 + sparklePhase * Math.PI / 4
                val startX = position.x * cellWidth + cellWidth / 2 + (foodSize / 2) * Math.cos(angle).toFloat()
                val startY = position.y * cellHeight + cellHeight / 2 + (foodSize / 2) * Math.sin(angle).toFloat()
                val endX = position.x * cellWidth + cellWidth / 2 + (foodSize / 2 + sparkleSize) * Math.cos(angle).toFloat()
                val endY = position.y * cellHeight + cellHeight / 2 + (foodSize / 2 + sparkleSize) * Math.sin(angle).toFloat()
                
                drawLine(
                    color = Color.White,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = cellWidth * 0.05f
                )
            }
            
            // Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = foodSize / 4,
                center = Offset(
                    position.x * cellWidth + cellWidth / 2 - foodSize * 0.1f,
                    position.y * cellHeight + cellHeight / 2 - foodSize * 0.1f
                )
            )
        }
    }
}

private fun DrawScope.drawRealisticSnake(snake: List<Point>, cellWidth: Float, cellHeight: Float, direction: Direction, animationTick: Int) {
    if (snake.isEmpty()) return
    
    // Snake colors
    val snakeHeadColor = Color(0xFF006400) // Dark green for head
    val snakeBodyColor = Color(0xFF008000) // Medium green for body
    val snakePatternColor = Color(0xFF004D00) // Darker green for patterns
    
    // Draw snake body segments first (in reverse to get proper overlap)
    for (i in snake.size - 1 downTo 1) {
        val current = snake[i]
        val prev = snake[i - 1]
        
        // Calculate segment direction for proper connection
        val segmentDirection = getSegmentDirection(prev, current)
        
        // Draw body segment with pattern
        drawSnakeBodySegment(
            current.x * cellWidth,
            current.y * cellHeight,
            cellWidth,
            cellHeight,
            snakeBodyColor,
            snakePatternColor,
            segmentDirection,
            i,
            animationTick
        )
    }
    
    // Draw snake head
    val head = snake.first()
    drawSnakeHead(
        head.x * cellWidth,
        head.y * cellHeight,
        cellWidth,
        cellHeight,
        snakeHeadColor,
        direction,
        animationTick
    )
}

private fun DrawScope.drawSnakeHead(x: Float, y: Float, cellWidth: Float, cellHeight: Float, color: Color, direction: Direction, animationTick: Int) {
    // Determine rotation angle based on direction
    val rotationAngle = when (direction) {
        Direction.UP -> 270f
        Direction.RIGHT -> 0f
        Direction.DOWN -> 90f
        Direction.LEFT -> 180f
    }
    
    // Tongue animation
    val tongueExtension = (Math.sin(animationTick * 0.3) * 0.5 + 0.5).toFloat() * cellWidth * 0.3f
    
    // Draw head with rotation
    rotate(rotationAngle, pivot = Offset(x + cellWidth / 2, y + cellHeight / 2)) {
        // Head shape (slightly oval)
        drawOval(
            color = color,
            topLeft = Offset(x, y),
            size = Size(cellWidth, cellHeight * 1.1f)
        )
        
        // Eyes (white with black pupils)
        // Left eye
        drawCircle(
            color = Color.White,
            radius = cellWidth * 0.15f,
            center = Offset(x + cellWidth * 0.7f, y + cellHeight * 0.3f)
        )
        drawCircle(
            color = Color.Black,
            radius = cellWidth * 0.07f,
            center = Offset(x + cellWidth * 0.7f, y + cellHeight * 0.3f)
        )
        
        // Right eye
        drawCircle(
            color = Color.White,
            radius = cellWidth * 0.15f,
            center = Offset(x + cellWidth * 0.7f, y + cellHeight * 0.7f)
        )
        drawCircle(
            color = Color.Black,
            radius = cellWidth * 0.07f,
            center = Offset(x + cellWidth * 0.7f, y + cellHeight * 0.7f)
        )
        
        // Forked tongue (animated)
        val tongueColor = Color(0xFFFF0000) // Red
        
        // Tongue base
        drawRect(
            color = tongueColor,
            topLeft = Offset(x + cellWidth, y + cellHeight * 0.45f),
            size = Size(tongueExtension, cellHeight * 0.1f)
        )
        
        // Tongue forks
        drawLine(
            color = tongueColor,
            start = Offset(x + cellWidth + tongueExtension, y + cellHeight * 0.5f),
            end = Offset(x + cellWidth + tongueExtension + cellWidth * 0.15f, y + cellHeight * 0.4f),
            strokeWidth = cellWidth * 0.08f
        )
        
        drawLine(
            color = tongueColor,
            start = Offset(x + cellWidth + tongueExtension, y + cellHeight * 0.5f),
            end = Offset(x + cellWidth + tongueExtension + cellWidth * 0.15f, y + cellHeight * 0.6f),
            strokeWidth = cellWidth * 0.08f
        )
    }
}

private fun DrawScope.drawSnakeBodySegment(
    x: Float, 
    y: Float, 
    cellWidth: Float, 
    cellHeight: Float, 
    bodyColor: Color, 
    patternColor: Color,
    direction: Direction,
    segmentIndex: Int,
    animationTick: Int
) {
    // Base body segment
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(x, y),
        size = Size(cellWidth, cellHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellWidth * 0.2f)
    )
    
    // Add scale pattern based on segment index
    val patternOffset = (segmentIndex + animationTick / 10) % 4
    
    when (patternOffset) {
        0 -> {
            // Diamond pattern
            val path = Path().apply {
                moveTo(x + cellWidth * 0.5f, y + cellHeight * 0.2f)
                lineTo(x + cellWidth * 0.8f, y + cellHeight * 0.5f)
                lineTo(x + cellWidth * 0.5f, y + cellHeight * 0.8f)
                lineTo(x + cellWidth * 0.2f, y + cellHeight * 0.5f)
                close()
            }
            drawPath(path, patternColor.copy(alpha = 0.5f))
        }
        1 -> {
            // Horizontal stripe
            drawRect(
                color = patternColor.copy(alpha = 0.5f),
                topLeft = Offset(x + cellWidth * 0.1f, y + cellHeight * 0.4f),
                size = Size(cellWidth * 0.8f, cellHeight * 0.2f)
            )
        }
        2 -> {
            // Dot pattern
            drawCircle(
                color = patternColor.copy(alpha = 0.5f),
                radius = cellWidth * 0.15f,
                center = Offset(x + cellWidth * 0.5f, y + cellHeight * 0.5f)
            )
        }
        3 -> {
            // Cross pattern
            drawRect(
                color = patternColor.copy(alpha = 0.5f),
                topLeft = Offset(x + cellWidth * 0.4f, y + cellHeight * 0.1f),
                size = Size(cellWidth * 0.2f, cellHeight * 0.8f)
            )
            drawRect(
                color = patternColor.copy(alpha = 0.5f),
                topLeft = Offset(x + cellWidth * 0.1f, y + cellHeight * 0.4f),
                size = Size(cellWidth * 0.8f, cellHeight * 0.2f)
            )
        }
    }
}

private fun getSegmentDirection(from: Point, to: Point): Direction {
    return when {
        from.x < to.x -> Direction.LEFT
        from.x > to.x -> Direction.RIGHT
        from.y < to.y -> Direction.UP
        else -> Direction.DOWN
    }
}

private fun DrawScope.drawScoreAndLevel(score: Int, level: GameLevel, cellWidth: Float) {
    // Format score with leading zeros
    val formattedScore = score.toString().padStart(4, '0')
    
    // Draw score at top left
    drawText(
        text = formattedScore,
        x = cellWidth * 0.5f,
        y = cellWidth * 0.8f,
        color = Color(0xFF006400),
        fontSize = cellWidth * 0.8f
    )
    
    // Draw level at top right
    drawText(
        text = "L${level.getLabel()}",
        x = size.width - cellWidth * 2.5f,
        y = cellWidth * 0.8f,
        color = Color(0xFF006400),
        fontSize = cellWidth * 0.8f
    )
}

private fun DrawScope.drawText(text: String, x: Float, y: Float, color: Color, fontSize: Float) {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = color.toArgb()
            this.textSize = fontSize
            this.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            this.isAntiAlias = true
        }
        
        canvas.nativeCanvas.drawText(
            text,
            x,
            y,
            paint
        )
    }
} 