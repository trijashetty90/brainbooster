package com.example.ui.screens.minigames

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SpeedMathGameScreen(
    onBack: () -> Unit,
    onGameComplete: (score: Int, stars: Int, xp: Int) -> Unit
) {
    var timeLeft by remember { mutableIntStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var currentProblem by remember { mutableStateOf(generateMathProblem()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var feedbackColor by remember { mutableStateOf<Color?>(null) }

    // Timer countdown loop
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isGameOver = true
            val stars = when {
                score >= 12 -> 3
                score >= 7 -> 2
                score >= 3 -> 1
                else -> 0
            }
            val xpEarned = (score * 20) + (maxStreak * 10)
            onGameComplete(score, stars, xpEarned)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(20.dp)
    ) {
        if (isGameOver) {
            // Game Over Result Card
            val stars = when {
                score >= 12 -> 3
                score >= 7 -> 2
                score >= 3 -> 1
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
                        text = "Time's Up!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRatingRow(starsEarned = stars, starSize = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$score Correct",
                        fontSize = 36.sp,
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
                        text = "+${(score * 20) + (maxStreak * 10)} XP Earned",
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
                                timeLeft = 30
                                score = 0
                                streak = 0
                                maxStreak = 0
                                isGameOver = false
                                currentProblem = generateMathProblem()
                                selectedOption = null
                                feedbackColor = null
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
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
                        text = "Speed Math",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    StreakBadge(streakCount = streak)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Time Left",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${timeLeft}s",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft <= 5) CrimsonError else CyanAccent
                    )
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

                Spacer(modifier = Modifier.height(28.dp))

                // Problem Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = feedbackColor ?: CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = currentProblem.expression,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4 Choices in 2x2 grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OptionButton(
                            text = currentProblem.options[0].toString(),
                            modifier = Modifier.weight(1f)
                        ) {
                            handleChoice(
                                chosen = currentProblem.options[0],
                                answer = currentProblem.answer,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    feedbackColor = EmeraldSuccess.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                },
                                onIncorrect = {
                                    streak = 0
                                    feedbackColor = CrimsonError.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                }
                            )
                        }
                        OptionButton(
                            text = currentProblem.options[1].toString(),
                            modifier = Modifier.weight(1f)
                        ) {
                            handleChoice(
                                chosen = currentProblem.options[1],
                                answer = currentProblem.answer,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    feedbackColor = EmeraldSuccess.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                },
                                onIncorrect = {
                                    streak = 0
                                    feedbackColor = CrimsonError.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OptionButton(
                            text = currentProblem.options[2].toString(),
                            modifier = Modifier.weight(1f)
                        ) {
                            handleChoice(
                                chosen = currentProblem.options[2],
                                answer = currentProblem.answer,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    feedbackColor = EmeraldSuccess.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                },
                                onIncorrect = {
                                    streak = 0
                                    feedbackColor = CrimsonError.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                }
                            )
                        }
                        OptionButton(
                            text = currentProblem.options[3].toString(),
                            modifier = Modifier.weight(1f)
                        ) {
                            handleChoice(
                                chosen = currentProblem.options[3],
                                answer = currentProblem.answer,
                                onCorrect = {
                                    score += 1
                                    streak += 1
                                    if (streak > maxStreak) maxStreak = streak
                                    feedbackColor = EmeraldSuccess.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
                                },
                                onIncorrect = {
                                    streak = 0
                                    feedbackColor = CrimsonError.copy(alpha = 0.25f)
                                    currentProblem = generateMathProblem()
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
private fun OptionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

private fun handleChoice(
    chosen: Int,
    answer: Int,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit
) {
    if (chosen == answer) {
        com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.CORRECT_ANSWER)
        onCorrect()
    } else {
        com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.INCORRECT_ANSWER)
        onIncorrect()
    }
}

private data class MathProblem(
    val expression: String,
    val answer: Int,
    val options: List<Int>
)

private fun generateMathProblem(): MathProblem {
    val operations = listOf("+", "-", "×")
    val op = operations.random()
    val a: Int
    val b: Int
    val answer: Int

    when (op) {
        "+" -> {
            a = Random.nextInt(5, 50)
            b = Random.nextInt(5, 50)
            answer = a + b
        }
        "-" -> {
            a = Random.nextInt(15, 60)
            b = Random.nextInt(5, a)
            answer = a - b
        }
        else -> {
            a = Random.nextInt(3, 12)
            b = Random.nextInt(3, 12)
            answer = a * b
        }
    }

    val distractors = mutableSetOf(answer)
    while (distractors.size < 4) {
        val delta = listOf(-10, -2, -1, 1, 2, 10, 5, -5).random()
        val candidate = answer + delta
        if (candidate > 0) distractors.add(candidate)
    }

    return MathProblem(
        expression = "$a $op $b",
        answer = answer,
        options = distractors.toList().shuffled()
    )
}
