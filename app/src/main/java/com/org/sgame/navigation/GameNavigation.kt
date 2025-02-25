package com.org.sgame.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.org.sgame.screens.GameScreen
import com.org.sgame.screens.MainMenuScreen
import com.org.sgame.game.SoundManager

@Composable
fun GameNavigation(soundManager: SoundManager) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            MainMenuScreen(onStartGame = {
                navController.navigate("game")
            })
        }
        
        composable("game") {
            GameScreen(
                onBack = { navController.navigateUp() },
                soundManager = soundManager
            )
        }
    }
}