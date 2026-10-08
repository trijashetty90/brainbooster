package com.example.ui.screens.minigames

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun NumberMemoryFlashScreen(
    onBack: () -> Unit,
    onGameComplete: (score: Int, stars: Int, xp: Int) -> Unit
) {
    var digitCount by remember { mutableIntStateOf(3) }
    var currentNumber by remember { mutableStateOf(generateRandomDigits(digitCount)) }
    var phase by remember { mutableStateOf(MemoryPhase.FLASH) } // FLASH, RECALL, RESULT, GAME_OVER
    var enteredDigits by remember { mutableStateOf("") }
    var bestDigitLength by remember { mutableIntStateOf(3) }
    var flashProgress by remember { mutableStateOf(1f) }

    // Flash countdown effect
    LaunchedEffect(currentNumber, phase) {
        if (phase == MemoryPhase.FLASH) {
            flashProgress = 1f
            val totalMs = 1800L
            val stepMs = 50L
            var elapsed = 0L
            while (elapsed < totalMs) {
                delay(stepMs)
                elapsed += stepMs
                flashProgress = 1f - (elapsed.toFloat() / totalMs.toFloat())
            }
            phase = MemoryPhase.RECALL
            enteredDigits = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(18.dp)
    ) {
        if (phase == MemoryPhase.GAME_OVER) {
            val stars = when {
                bestDigitLength >= 7 -> 3
                bestDigitLength >= 5 -> 2
                bestDigitLength >= 4 -> 1
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
                        text = "Working Memory Limit",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRatingRow(starsEarned = stars, starSize = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$bestDigitLength Digits Remembered",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Target was: $currentNumber",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${bestDigitLength * 35} XP Earned",
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
                                digitCount = 3
                                bestDigitLength = 3
                                currentNumber = generateRandomDigits(digitCount)
                                phase = MemoryPhase.FLASH
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
                        text = "Number Memory Flash",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Span: $digitCount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Phase Status Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (phase == MemoryPhase.FLASH) "MEMORIZE THE SEQUENCE" else "ENTER RECALLED NUMBER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (phase == MemoryPhase.FLASH) AmberGold else CyanAccent
                    )
                    Text(
                        text = "Level $digitCount",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                if (phase == MemoryPhase.FLASH) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { flashProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AmberGold,
                        trackColor = CardBorder
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display Area
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (phase == MemoryPhase.FLASH) AmberGold else CyanAccent)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (phase == MemoryPhase.FLASH) {
                            Text(
                                text = currentNumber,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberGold,
                                letterSpacing = 6.sp
                            )
                        } else {
                            Text(
                                text = if (enteredDigits.isEmpty()) "_ ".repeat(digitCount).trim() else enteredDigits,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (enteredDigits.isEmpty()) TextSecondary.copy(alpha = 0.4f) else TextPrimary,
                                letterSpacing = 4.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Numeric Keypad (only in RECALL phase)
                if (phase == MemoryPhase.RECALL) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val rows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("⌫", "0", "✓")
                        )

                        rows.forEach { rowKeys ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowKeys.forEach { key ->
                                    KeypadButton(
                                        text = key,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.UI_INTERACTION)
                                        when (key) {
                                            "⌫" -> {
                                                if (enteredDigits.isNotEmpty()) {
                                                    enteredDigits = enteredDigits.dropLast(1)
                                                }
                                            }
                                            "✓" -> {
                                                if (enteredDigits == currentNumber) {
                                                    // Correct!
                                                    com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.CORRECT_ANSWER)
                                                    bestDigitLength = digitCount
                                                    digitCount += 1
                                                    currentNumber = generateRandomDigits(digitCount)
                                                    phase = MemoryPhase.FLASH
                                                } else {
                                                    // Game Over
                                                    com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.INCORRECT_ANSWER)
                                                    phase = MemoryPhase.GAME_OVER
                                                    val stars = when {
                                                        bestDigitLength >= 7 -> 3
                                                        bestDigitLength >= 5 -> 2
                                                        bestDigitLength >= 4 -> 1
                                                        else -> 0
                                                    }
                                                    onGameComplete(bestDigitLength, stars, bestDigitLength * 35)
                                                }
                                            }
                                            else -> {
                                                if (enteredDigits.length < digitCount) {
                                                    enteredDigits += key
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isAction = text == "✓" || text == "⌫"
    Surface(
        modifier = modifier
            .height(54.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (text == "✓") CyanAccent else CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                fontSize = if (isAction) 20.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (text == "✓") MidnightBg else TextPrimary
            )
        }
    }
}

private enum class MemoryPhase {
    FLASH, RECALL, GAME_OVER
}

private fun generateRandomDigits(length: Int): String {
    val sb = java.lang.StringBuilder()
    repeat(length) {
        sb.append(Random.nextInt(0, 10))
    }
    return sb.toString()
}
