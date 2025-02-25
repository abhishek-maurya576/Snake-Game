package com.org.sgame.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import kotlin.random.Random

// Grid size for the game
const val GRID_SIZE = 20

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

enum class Direction {
    UP, RIGHT, DOWN, LEFT;
    
    // Prevents 180-degree turns (can't go directly backward)
    fun isOpposite(other: Direction): Boolean = when (this) {
        UP -> other == DOWN
        DOWN -> other == UP
        LEFT -> other == RIGHT
        RIGHT -> other == LEFT
    }
    
    fun toPoint(): Point = when (this) {
        UP -> Point(0, -1)
        DOWN -> Point(0, 1)
        LEFT -> Point(-1, 0)
        RIGHT -> Point(1, 0)
    }
}

enum class FoodType {
    REGULAR,
    BOOSTER
}

data class Food(val position: Point, val type: FoodType)

class SnakeGameModel {
    // Game state
    var snake by mutableStateOf(listOf<Point>())
    var food by mutableStateOf(Food(Point(0, 0), FoodType.REGULAR))
    var obstacles by mutableStateOf(listOf<Point>())
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
    var gameType by mutableStateOf(GameType.STANDARD)
    
    // Direction state
    var currentDirection by mutableStateOf(Direction.RIGHT)
    private var nextDirection by mutableStateOf(Direction.RIGHT)
    
    // Animation state
    var animationTick by mutableStateOf(0)
    
    // Booster food timer
    private var boosterFoodCounter by mutableStateOf(0)
    
    init {
        resetGame()
    }
    
    fun resetGame() {
        // Initialize snake in the middle of the grid
        val startX = GRID_SIZE / 2
        val startY = GRID_SIZE / 2
        
        snake = listOf(
            Point(startX, startY),
            Point(startX - 1, startY),
            Point(startX - 2, startY),
            Point(startX - 3, startY)
        )
        
        currentDirection = Direction.RIGHT
        nextDirection = Direction.RIGHT
        score = 0
        isGameOver = false
        isPaused = false
        animationTick = 0
        boosterFoodCounter = 0
        
        // Clear obstacles if not in maze mode
        if (gameType != GameType.MAZE) {
            obstacles = emptyList()
        }
        
        // Place initial food
        placeFood()
    }
    
    fun addMazeObstacles(count: Int) {
        val newObstacles = mutableListOf<Point>()
        
        // Add random obstacles
        repeat(count) {
            // Create small clusters of obstacles
            val baseX = Random.nextInt(1, GRID_SIZE - 1)
            val baseY = Random.nextInt(1, GRID_SIZE - 1)
            
            // Add 3-5 obstacles in a cluster
            repeat(Random.nextInt(3, 6)) {
                val offsetX = Random.nextInt(-1, 2)
                val offsetY = Random.nextInt(-1, 2)
                val obstacleX = (baseX + offsetX).coerceIn(1, GRID_SIZE - 2)
                val obstacleY = (baseY + offsetY).coerceIn(1, GRID_SIZE - 2)
                
                val obstacle = Point(obstacleX, obstacleY)
                
                // Don't place obstacles on the snake or existing obstacles
                if (!snake.contains(obstacle) && !newObstacles.contains(obstacle)) {
                    newObstacles.add(obstacle)
                }
            }
        }
        
        obstacles = newObstacles
        
        // Make sure food isn't on an obstacle
        if (obstacles.contains(food.position)) {
            placeFood()
        }
    }
    
    fun togglePause() {
        isPaused = !isPaused
    }
    
    fun setDirection(direction: Direction) {
        // Prevent 180-degree turns
        val isOpposite = (currentDirection == Direction.UP && direction == Direction.DOWN) ||
                         (currentDirection == Direction.DOWN && direction == Direction.UP) ||
                         (currentDirection == Direction.LEFT && direction == Direction.RIGHT) ||
                         (currentDirection == Direction.RIGHT && direction == Direction.LEFT)
        
        if (!isOpposite) {
            nextDirection = direction
        }
    }
    
    fun update() {
        if (isGameOver || isPaused) return
        
        // Update animation tick
        animationTick++
        
        // Apply the next direction
        currentDirection = nextDirection
        
        // Get the current head position
        val head = snake.first()
        
        // Calculate new head position based on direction
        val newHead = when (currentDirection) {
            Direction.UP -> Point(head.x, (head.y - 1 + GRID_SIZE) % GRID_SIZE)
            Direction.RIGHT -> Point((head.x + 1) % GRID_SIZE, head.y)
            Direction.DOWN -> Point(head.x, (head.y + 1) % GRID_SIZE)
            Direction.LEFT -> Point((head.x - 1 + GRID_SIZE) % GRID_SIZE, head.y)
        }
        
        // Check for collisions with walls in standard mode
        if (gameType == GameType.STANDARD) {
            if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE) {
                isGameOver = true
                return
            }
        }
        
        // Check for collisions with obstacles
        if (obstacles.contains(newHead)) {
            isGameOver = true
            return
        }
        
        // Check for collisions with self (except in NO_WALLS mode where we allow passing through)
        if (snake.contains(newHead) && !(gameType == GameType.NO_WALLS && 
            (newHead.x == 0 || newHead.x == GRID_SIZE - 1 || newHead.y == 0 || newHead.y == GRID_SIZE - 1))) {
            isGameOver = true
            return
        }
        
        // Check if snake ate food
        val ate = newHead == food.position
        
        // Create new snake
        val newSnake = mutableListOf(newHead)
        newSnake.addAll(snake)
        
        // Remove tail if didn't eat
        if (!ate) {
            newSnake.removeAt(newSnake.size - 1)
        } else {
            // Increment score based on food type
            when (food.type) {
                FoodType.REGULAR -> score++
                FoodType.BOOSTER -> score += 3
            }
            
            // Place new food
            placeFood()
        }
        
        // Update snake
        snake = newSnake
        
        // Manage booster food appearance
        if (boosterFoodCounter > 0) {
            boosterFoodCounter--
        } else if (Random.nextInt(100) < 2 && food.type == FoodType.REGULAR) {
            // 2% chance to replace regular food with booster food
            food = Food(food.position, FoodType.BOOSTER)
            boosterFoodCounter = 50 // Booster food will stay for 50 updates (about 10 seconds)
        }
    }
    
    private fun placeFood() {
        // Find a position that's not occupied by the snake or obstacles
        var newFoodPosition: Point
        do {
            newFoodPosition = Point(
                Random.nextInt(GRID_SIZE),
                Random.nextInt(GRID_SIZE)
            )
        } while (snake.contains(newFoodPosition) || obstacles.contains(newFoodPosition))
        
        // Determine food type - 10% chance for booster food
        val foodType = if (Random.nextInt(100) < 10) FoodType.BOOSTER else FoodType.REGULAR
        
        food = Food(newFoodPosition, foodType)
        
        // Reset booster food counter if this is a booster food
        if (foodType == FoodType.BOOSTER) {
            boosterFoodCounter = 50 // About 10 seconds
        }
    }
} 