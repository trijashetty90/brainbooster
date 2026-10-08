package com.example.ui.screens.minigames

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordUnscrambleScreen(
    onBack: () -> Unit,
    onGameComplete: (score: Int, stars: Int, xp: Int) -> Unit
) {
    val wordList = listOf(
        Pair("BRAIN", "Cognitive organ"),
        Pair("LOGIC", "Systematic reasoning"),
        Pair("MEMORY", "Information retention"),
        Pair("VECTOR", "Directional magnitude"),
        Pair("CIRCUIT", "Path for electric current"),
        Pair("SYNAPSE", "Neuronal junction"),
        Pair("MATRIX", "Rectangular mathematical array"),
        Pair("ALGORITHM", "Step-by-step problem solver")
    )

    var currentWordIndex by remember { mutableIntStateOf(0) }
    val currentTarget = wordList[currentWordIndex % wordList.size].first
    val currentHint = wordList[currentWordIndex % wordList.size].second

    var availableLetters by remember(currentTarget) {
        mutableStateOf(currentTarget.toList().shuffled().mapIndexed { i, c -> LetterTile(i, c.toString(), isUsed = false) })
    }
    var constructedWord by remember(currentTarget) { mutableStateOf<List<LetterTile>>(emptyList()) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var isGameOver by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isGameOver = true
            val stars = when {
                score >= 5 -> 3
                score >= 3 -> 2
                score >= 1 -> 1
                else -> 0
            }
            onGameComplete(score, stars, (score * 30) + (maxStreak * 10))
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
                score >= 5 -> 3
                score >= 3 -> 2
                score >= 1 -> 1
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
                        text = "Word Anagram Round Complete!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRatingRow(starsEarned = stars, starSize = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$score Words Solved",
                        fontSize = 28.sp,
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
                        text = "+${(score * 30) + (maxStreak * 10)} XP Earned",
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
                                currentWordIndex = 0
                                score = 0
                                streak = 0
                                maxStreak = 0
                                timeLeft = 45
                                isGameOver = false
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
                        text = "Word Unscramble",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    StreakBadge(streakCount = streak)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Time Left: ${timeLeft}s", color = TextSecondary, fontSize = 13.sp)
                    Text("Solved: $score", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { timeLeft / 45f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (timeLeft <= 8) CrimsonError else VioletAccent,
                    trackColor = CardBorder
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Hint
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CLUE / HINT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentHint,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Assembled Word Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBorder.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanAccent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (constructedWord.isEmpty()) {
                            Text(
                                text = "Tap letters below to unscramble",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        } else {
                            constructedWord.forEach { tile ->
                                Surface(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = CyanAccent
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = tile.letter,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MidnightBg
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Clickable Scrambled Letter Tiles
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    availableLetters.forEach { tile ->
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(50.dp)
                                .clickable(enabled = !tile.isUsed) {
                                    constructedWord = constructedWord + tile
                                    availableLetters = availableLetters.map {
                                        if (it.id == tile.id) it.copy(isUsed = true) else it
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (tile.isUsed) CardBorder.copy(alpha = 0.3f) else CardDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (tile.isUsed) CardBorder.copy(alpha = 0.3f) else VioletAccent
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tile.letter,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tile.isUsed) TextSecondary.copy(alpha = 0.4f) else TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Action Buttons: Backspace & Submit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (constructedWord.isNotEmpty()) {
                                val lastTile = constructedWord.last()
                                constructedWord = constructedWord.dropLast(1)
                                availableLetters = availableLetters.map {
                                    if (it.id == lastTile.id) it.copy(isUsed = false) else it
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace", tint = TextPrimary)
                        Text(" Delete", color = TextPrimary)
                    }

                    Button(
                        onClick = {
                            val candidate = constructedWord.joinToString("") { it.letter }
                            if (candidate == currentTarget) {
                                com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.CORRECT_ANSWER)
                                score += 1
                                streak += 1
                                if (streak > maxStreak) maxStreak = streak
                                currentWordIndex += 1
                            } else {
                                com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.INCORRECT_ANSWER)
                                streak = 0
                                // Reset constructed
                                constructedWord = emptyList()
                                availableLetters = availableLetters.map { it.copy(isUsed = false) }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Submit", tint = MidnightBg)
                        Text(" Check", color = MidnightBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private data class LetterTile(
    val id: Int,
    val letter: String,
    val isUsed: Boolean = false
)
