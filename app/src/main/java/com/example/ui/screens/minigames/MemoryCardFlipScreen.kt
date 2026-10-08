package com.example.ui.screens.minigames

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.delay

@Composable
fun MemoryCardFlipScreen(
    onBack: () -> Unit,
    onGameComplete: (score: Int, stars: Int, xp: Int) -> Unit
) {
    val symbols = listOf("🧠", "⚡", "🚀", "💡", "🎯", "🔮")
    var cards by remember { mutableStateOf(generateCardDeck(symbols)) }
    var flippedIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(1000)
                secondsElapsed += 1
            }
        }
    }

    // Checking pairs match logic
    LaunchedEffect(flippedIndices) {
        if (flippedIndices.size == 2) {
            val first = flippedIndices[0]
            val second = flippedIndices[1]
            if (cards[first].symbol == cards[second].symbol) {
                // Match
                com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.CORRECT_ANSWER)
                delay(300)
                cards = cards.mapIndexed { idx, card ->
                    if (idx == first || idx == second) card.copy(isMatched = true) else card
                }
                matchedPairs += 1
                flippedIndices = emptyList()

                if (matchedPairs == symbols.size) {
                    isGameOver = true
                    com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.GAME_COMPLETION)
                    val stars = when {
                        moves <= 8 -> 3
                        moves <= 12 -> 2
                        else -> 1
                    }
                    val xp = (stars * 50) + (100 - secondsElapsed).coerceAtLeast(20)
                    onGameComplete(moves, stars, xp)
                }
            } else {
                // Mismatch: flip back after delay
                com.example.audio.SoundManager.playSound(com.example.audio.SoundEffect.INCORRECT_ANSWER)
                delay(800)
                cards = cards.mapIndexed { idx, card ->
                    if (idx == first || idx == second) card.copy(isFlipped = false) else card
                }
                flippedIndices = emptyList()
            }
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
                moves <= 8 -> 3
                moves <= 12 -> 2
                else -> 1
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
                        text = "Memory Master!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRatingRow(starsEarned = stars, starSize = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Completed in $moves moves",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "Time: ${secondsElapsed}s",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${(stars * 50) + 30} XP Earned",
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
                                cards = generateCardDeck(symbols)
                                flippedIndices = emptyList()
                                moves = 0
                                matchedPairs = 0
                                secondsElapsed = 0
                                isGameOver = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MidnightBg)
                            Text(" Play Again", color = MidnightBg, fontWeight = FontWeight.Bold)
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
                        text = "Memory Card Flip",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Moves: $moves",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Matched: $matchedPairs / ${symbols.size}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${secondsElapsed}s",
                        fontSize = 13.sp,
                        color = VioletAccent
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3 columns x 4 rows
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cards.size) { index ->
                        val card = cards[index]
                        MemoryCardView(
                            card = card,
                            onClick = {
                                if (!card.isFlipped && !card.isMatched && flippedIndices.size < 2) {
                                    cards = cards.mapIndexed { idx, c ->
                                        if (idx == index) c.copy(isFlipped = true) else c
                                    }
                                    val nextFlipped = flippedIndices + index
                                    flippedIndices = nextFlipped
                                    if (nextFlipped.size == 2) {
                                        moves += 1
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCardView(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "CardFlip"
    )

    Surface(
        modifier = Modifier
            .aspectRatio(0.85f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(enabled = !card.isFlipped && !card.isMatched) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (card.isMatched) EmeraldSuccess.copy(alpha = 0.2f) else if (card.isFlipped) CardBorder else CardDark,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (card.isMatched) EmeraldSuccess else if (card.isFlipped) CyanAccent else CardBorder
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (rotation >= 90f) {
                Text(
                    text = card.symbol,
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            } else {
                Text(
                    text = "?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }
    }
}

private data class MemoryCard(
    val id: Int,
    val symbol: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

private fun generateCardDeck(symbols: List<String>): List<MemoryCard> {
    val deck = mutableListOf<MemoryCard>()
    var idCounter = 0
    symbols.forEach { sym ->
        deck.add(MemoryCard(id = idCounter++, symbol = sym))
        deck.add(MemoryCard(id = idCounter++, symbol = sym))
    }
    return deck.shuffled()
}
