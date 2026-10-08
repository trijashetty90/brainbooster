package com.example.ui.screens.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StarRatingRow
import com.example.ui.components.StreakBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.StreakFire
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun StroopColorGameScreen(
    onBack: () -> Unit,
    onGameComplete: (score: Int, stars: Int, xp: Int) -> Unit
) {
    val colorPalette = listOf(
        StroopItem("RED", Color(0xFFEF4444)),
        StroopItem("BLUE", Color(0xFF38BDF8)),
        StroopItem("GREEN", Color(0xFF10B981)),
        StroopItem("AMBER", Color(0xFFF59E0B))
    )

    var currentQuestion by remember { mutableStateOf(generateStroop(colorPalette)) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isGameOver = true
            val stars = when {
                score >= 18 -> 3
                score >= 11 -> 2
                score >= 5 -> 1
                else -> 0
            }
            onGameComplete(score, stars, (score * 15) + (maxStreak * 10))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(18.dp)
    ) {
        if (isGameOver) {
            val stars = when {
                score >= 18 -> 3
                score >= 11 -> 2
                score >= 5 -> 1
                else -> 0
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                shape = RoundedCornerShape(24.dp),
                color = CardDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Stroop Test Complete!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRatingRow(starsEarned = stars, starSize = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$score Correct Identifications",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "Peak Streak: 🔥 $maxStreak",
                        fontSize = 15.sp,
                        color = StreakFire
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${(score * 15) + (maxStreak * 10)} XP Earned",
                        fontSize = 14.sp,
                        color = EmeraldSuccess
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                score = 0
                                streak = 0
                                maxStreak = 0
                                timeLeft = 30
                                isGameOver = false
                                currentQuestion = generateStroop(colorPalette)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MidnightBg)
                            Text(" Retry", color = MidnightBg, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
                        ) {
                            Text("Back", color = TextPrimary)
                        }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "Stroop Challenge",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    StreakBadge(streakCount = streak)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rule reminder
                Text(
                    text = "SELECT THE INK COLOR (IGNORE THE WORD TEXT)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Timer Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Time Left: ${timeLeft}s", color = TextSecondary, fontSize = 13.sp)
                    Text("Score: $score", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { timeLeft / 30f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (timeLeft <= 5) CrimsonError else CyanAccent,
                    trackColor = CardBorder
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Stroop Target Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = currentQuestion.displayedWord,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = currentQuestion.inkColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 4 Color Selection Buttons in 2x2 Grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ColorChoiceButton(
                            label = colorPalette[0].name,
                            color = colorPalette[0].color,
                            modifier = Modifier.weight(1f)
                        ) {
                            handleColorSelect(
                                chosenName = colorPalette[0].name,
                                correctName = currentQuestion.correctInkColorName,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    currentQuestion = generateStroop(colorPalette)
                                },
                                onIncorrect = {
                                    streak = 0
                                    currentQuestion = generateStroop(colorPalette)
                                }
                            )
                        }

                        ColorChoiceButton(
                            label = colorPalette[1].name,
                            color = colorPalette[1].color,
                            modifier = Modifier.weight(1f)
                        ) {
                            handleColorSelect(
                                chosenName = colorPalette[1].name,
                                correctName = currentQuestion.correctInkColorName,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    currentQuestion = generateStroop(colorPalette)
                                },
                                onIncorrect = {
                                    streak = 0
                                    currentQuestion = generateStroop(colorPalette)
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ColorChoiceButton(
                            label = colorPalette[2].name,
                            color = colorPalette[2].color,
                            modifier = Modifier.weight(1f)
                        ) {
                            handleColorSelect(
                                chosenName = colorPalette[2].name,
                                correctName = currentQuestion.correctInkColorName,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    currentQuestion = generateStroop(colorPalette)
                                },
                                onIncorrect = {
                                    streak = 0
                                    currentQuestion = generateStroop(colorPalette)
                                }
                            )
                        }

                        ColorChoiceButton(
                            label = colorPalette[3].name,
                            color = colorPalette[3].color,
                            modifier = Modifier.weight(1f)
                        ) {
                            handleColorSelect(
                                chosenName = colorPalette[3].name,
                                correctName = currentQuestion.correctInkColorName,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    currentQuestion = generateStroop(colorPalette)
                                },
                                onIncorrect = {
                                    streak = 0
                                    currentQuestion = generateStroop(colorPalette)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorChoiceButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

private fun handleColorSelect(
    chosenName: String,
    correctName: String,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit
) {
    if (chosenName == correctName) {
        com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.CORRECT_ANSWER)
        onCorrect()
    } else {
        com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.INCORRECT_ANSWER)
        onIncorrect()
    }
}

private data class StroopItem(
    val name: String,
    val color: Color
)

private data class StroopQuestion(
    val displayedWord: String,
    val inkColor: Color,
    val correctInkColorName: String
)

private fun generateStroop(palette: List<StroopItem>): StroopQuestion {
    val wordItem = palette.random()
    // Intentionally incongruent 75% of the time
    val inkItem = if (kotlin.random.Random.nextFloat() > 0.25f) {
        palette.filter { it.name != wordItem.name }.random()
    } else {
        wordItem
    }

    return StroopQuestion(
        displayedWord = wordItem.name,
        inkColor = inkItem.color,
        correctInkColorName = inkItem.name
    )
}
