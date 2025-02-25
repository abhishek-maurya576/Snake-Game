package com.org.sgame.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.org.sgame.R

class SoundManager(private val context: Context) {
    
    private val soundPool: SoundPool
    
    // Sound IDs
    private var moveSound: Int = 0
    private var eatFoodSound: Int = 0
    private var gameOverSound: Int = 0
    private var clickSound: Int = 0
    private var nokiaStartupSound: Int = 0
    
    init {
        // Initialize SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()
        
        try {
            // Load sounds using the existing files
            moveSound = soundPool.load(context, R.raw.snake_movement, 1)
            eatFoodSound = soundPool.load(context, R.raw.music_food, 1)
            gameOverSound = soundPool.load(context, R.raw.gameover, 1)
            clickSound = soundPool.load(context, R.raw.move, 1) // Using move.mp3 for button clicks
            nokiaStartupSound = soundPool.load(context, R.raw.app_startup_sound, 1)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error loading sounds: ${e.message}")
        }
    }
    
    fun playMove() {
        soundPool.play(moveSound, 0.5f, 0.5f, 1, 0, 1.0f)
    }
    
    fun playEatFood() {
        soundPool.play(eatFoodSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun playGameOver() {
        soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun playClick() {
        soundPool.play(clickSound, 0.8f, 0.8f, 1, 0, 1.0f)
    }
    
    fun playNokiaStartup() {
        soundPool.play(nokiaStartupSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun release() {
        soundPool.release()
    }
}