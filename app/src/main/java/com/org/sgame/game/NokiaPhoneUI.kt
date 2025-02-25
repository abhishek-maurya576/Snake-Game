package com.org.sgame.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.org.sgame.ui.theme.*
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun NokiaPhone(
    modifier: Modifier = Modifier,
    gameContent: @Composable () -> Unit,
    onDirectionPressed: (Direction) -> Unit,
    onCenterPressed: () -> Unit,
    onResetPressed: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Adjust phone width based on screen dimensions
    val phoneWidth = screenWidth * 0.9f
    // Ensure proper aspect ratio for the phone body
    val phoneHeight = phoneWidth * 1.8f
    
    // Make sure phone fits on screen
    val finalWidth = if (phoneHeight > screenHeight * 0.95f) {
        (screenHeight * 0.95f / 1.8f)
    } else {
        phoneWidth
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Nokia Phone Body with metallic gradient
        Box(
            modifier = Modifier
                .width(finalWidth)
                .aspectRatio(1f/1.8f)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF353638),
                            Color(0xFF272829),
                            Color(0xFF1E1F20)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nokia Brand - metallic text
                Text(
                    text = "NOKIA",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .graphicsLayer {
                            shadowElevation = 4f
                        }
                )
                
                // Small dots above the screen with glow effect
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .padding(horizontal = 2.dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.LightGray, CircleShape)
                        ) {}
                    }
                }
                
                // Game Screen with glossy effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 12.dp)
                        .shadow(8.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF9ACD32))
                        .border(
                            BorderStroke(1.dp, Color(0xFF333333)),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    // Subtle glass reflection
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    gameContent()
                }
                
                // Controls Section - 3D D-Pad with arrows
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Green LED indicator with glow
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 26.dp, y = 26.dp)
                            .size(8.dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00FF00),
                                        Color(0xFF00AA00)
                                    )
                                ),
                                shape = CircleShape
                            )
                    ) {}
                    
                    // Blue indicator with glow
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = 4.dp)
                            .width(12.dp)
                            .height(3.dp)
                            .shadow(2.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0099FF),
                                        Color(0xFF0066CC)
                                    )
                                )
                            )
                    ) {}
                    
                    // Red indicator with glow
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = (-4).dp)
                            .width(12.dp)
                            .height(3.dp)
                            .shadow(2.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFCC0000),
                                        Color(0xFFFF0000)
                                    )
                                )
                            )
                    ) {}
                    
                    // Up button with arrow
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.TopCenter)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF5F5F5),
                                        Color(0xFFE0E0E0)
                                    )
                                )
                            )
                            .border(BorderStroke(1.dp, Color(0xFFDDDDDD)), CircleShape)
                            .clickable { onDirectionPressed(Direction.UP) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Up arrow
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val path = Path().apply {
                                moveTo(size.width / 2, 0f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF555555)
                            )
                        }
                    }
                    
                    // Left button with arrow
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.CenterStart)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF5F5F5),
                                        Color(0xFFE0E0E0)
                                    )
                                )
                            )
                            .border(BorderStroke(1.dp, Color(0xFFDDDDDD)), CircleShape)
                            .clickable { onDirectionPressed(Direction.LEFT) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Left arrow
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val path = Path().apply {
                                moveTo(0f, size.height / 2)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF555555)
                            )
                        }
                    }
                    
                    // Center button - square with metallic gradient
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                            .shadow(4.dp, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFDDDDDD),
                                        Color(0xFFCCCCCC),
                                        Color(0xFFBBBBBB)
                                    )
                                )
                            )
                            .border(
                                BorderStroke(1.dp, Color(0xFFCCCCCC)),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onCenterPressed() },
                        contentAlignment = Alignment.Center
                    ) {
                        // Center dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF555555), CircleShape)
                        ) {}
                    }
                    
                    // Right button with arrow
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.CenterEnd)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF5F5F5),
                                        Color(0xFFE0E0E0)
                                    )
                                )
                            )
                            .border(BorderStroke(1.dp, Color(0xFFDDDDDD)), CircleShape)
                            .clickable { onDirectionPressed(Direction.RIGHT) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Right arrow
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val path = Path().apply {
                                moveTo(size.width, size.height / 2)
                                lineTo(0f, 0f)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF555555)
                            )
                        }
                    }
                    
                    // Down button with arrow
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.BottomCenter)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF5F5F5),
                                        Color(0xFFE0E0E0)
                                    )
                                )
                            )
                            .border(BorderStroke(1.dp, Color(0xFFDDDDDD)), CircleShape)
                            .clickable { onDirectionPressed(Direction.DOWN) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Down arrow
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val path = Path().apply {
                                moveTo(size.width / 2, size.height)
                                lineTo(size.width, 0f)
                                lineTo(0f, 0f)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF555555)
                            )
                        }
                    }
                }
                
                // Modern C button with gloss effect
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 80.dp, height = 36.dp)
                        .shadow(8.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF555555),
                                    Color(0xFF444444),
                                    Color(0xFF333333)
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, Color(0xFF666666)),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { onResetPressed() },
                    contentAlignment = Alignment.Center
                ) {
                    // Add glass reflection effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    
                    Text(
                        text = "C",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer {
                            shadowElevation = 4f
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DirectionalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White) // Just white background
            .clickable(onClick = onClick)
            .shadow(2.dp, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Empty content
    }
}

@Composable
fun NumpadButton(
    number: String,
    letters: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {} // Most numpad buttons do nothing
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Number text - larger
            Text(
                text = number,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            // Letters text - smaller
            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 