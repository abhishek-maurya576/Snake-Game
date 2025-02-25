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
        LEVEL_2 -> 0.85f
        LEVEL_3 -> 0.7f
        LEVEL_4 -> 0.6f
        LEVEL_5 -> 0.5f
        LEVEL_6 -> 0.4f
        LEVEL_7 -> 0.3f
        LEVEL_8 -> 0.25f
        LEVEL_9 -> 0.2f
    }
    
    fun getScoreMultiplier(): Float = when (this) {
        LEVEL_1 -> 1.0f
        LEVEL_2 -> 1.2f
        LEVEL_3 -> 1.5f
        LEVEL_4 -> 1.8f
        LEVEL_5 -> 2.0f
        LEVEL_6 -> 2.5f
        LEVEL_7 -> 3.0f
        LEVEL_8 -> 4.0f
        LEVEL_9 -> 5.0f
    }
    
    fun getFoodFrequency(): Int = when (this) {
        LEVEL_1 -> 1
        LEVEL_2, LEVEL_3 -> 2
        LEVEL_4, LEVEL_5 -> 3 
        LEVEL_6, LEVEL_7 -> 4
        LEVEL_8, LEVEL_9 -> 5
    }
    
    fun getLabel(): String = when (this) {
        LEVEL_1 -> "1 (Easy)"
        LEVEL_2 -> "2"
        LEVEL_3 -> "3"
        LEVEL_4 -> "4"
        LEVEL_5 -> "5 (Medium)"
        LEVEL_6 -> "6"
        LEVEL_7 -> "7"
        LEVEL_8 -> "8"
        LEVEL_9 -> "9 (Hard)"
    }
} 