# Snake Game for Android

A modern implementation of the classic Snake game for Android, built with Jetpack Compose and Kotlin. This project recreates the nostalgic Nokia-style Snake game with enhanced graphics, multiple game modes, and a retro Nokia phone UI.

## Features

- **Multiple Game Modes**:
  - Standard Mode: Classic snake gameplay with walls
  - No Walls Mode: Snake can pass through walls and appear on the opposite side
  - Maze Mode: Navigate through obstacle-filled levels

- **Difficulty Levels**: 9 different speed levels to challenge players of all skill levels

- **Special Food Types**:
  - Regular Food: Increases score by 1 point
  - Booster Food: Special golden food that gives 3 points

- **Retro Nokia Phone UI**:
  - Authentic Nokia 3310-style interface
  - D-pad controls with responsive buttons
  - LCD-style screen with pixel graphics

- **Game Elements**:
  - Score tracking
  - Pause functionality
  - Game over screen
  - High score tracking

## Screenshots

[Insert screenshots here]

## How to Play

1. Use the directional buttons to control the snake's movement
2. Collect food to grow the snake and increase your score
3. Avoid collisions with walls, obstacles, and the snake's own body
4. Special golden food appears occasionally for bonus points
5. Press the pause button to pause the game
6. Press the reset button to restart after game over

## Controls

- **Arrow Buttons**: Change the snake's direction
- **Pause Button**: Pause/resume the game
- **Reset Button**: Restart the game
- **C Button**: Return to menu (on Nokia phone UI)

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM pattern
- **Graphics**: Custom Canvas drawing
- **Sound**: SoundPool for retro sound effects

## Project Structure

- `game/`: Core game logic and models
  - `SnakeGameModel.kt`: Main game model with snake movement logic
  - `GameBoard.kt`: Canvas rendering of the game board
  - `NokiaPhoneUI.kt`: Nokia phone UI implementation
  - `GameCanvas.kt`: Enhanced game rendering with animations
  - `GameMenu.kt`: Game menu system
  
- `screens/`: Compose UI screens
  - `GameScreen.kt`: Main game screen with controls
  
- `ui/theme/`: Theme definitions and colors

## Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Build and run on an Android device or emulator

## Requirements

- Android 6.0 (API level 23) or higher
- Android Studio Arctic Fox or newer

## Future Enhancements

- Multiplayer mode
- More food types with special effects
- Additional game modes
- Customizable snake appearance
- Leaderboards

## Credits

- Original Snake game concept by Nokia
- Sound effects created with [Sound tool]
- Icons and graphics designed with [Design tool]

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

Feel free to contribute to this project by submitting pull requests or reporting issues!
