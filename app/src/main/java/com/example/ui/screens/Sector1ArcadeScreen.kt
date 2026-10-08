package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStats
import com.example.ui.components.SectorSwitchSegmentBar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.StreakFire
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

data class MiniGameItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val domain: String,
    val bestScoreLabel: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun Sector1ArcadeScreen(
    userStats: UserStats,
    onBack: () -> Unit,
    onNavigateSector2: () -> Unit = {},
    onLaunchGame: (String) -> Unit
) {
    val games = listOf(
        MiniGameItem(
            id = "speed_math",
            title = "Speed Math",
            subtitle = "Rapid arithmetic under 30s countdown",
            domain = "Mental Calculation",
            bestScoreLabel = "Best: ${userStats.speedMathBest} correct",
            icon = Icons.Default.Calculate,
            accentColor = CyanAccent
        ),
        MiniGameItem(
            id = "memory_flip",
            title = "Memory Card Flip",
            subtitle = "Visual matrix pair-matching with move counter",
            domain = "Visual Working Memory",
            bestScoreLabel = "Best: ${userStats.memoryFlipBestSec}s finish",
            icon = Icons.Default.Casino,
            accentColor = EmeraldSuccess
        ),
        MiniGameItem(
            id = "word_unscramble",
            title = "Word Unscramble",
            subtitle = "Anagram resolution of tech & cognitive terms",
            domain = "Verbal Reasoning",
            bestScoreLabel = "Best: ${userStats.wordUnscrambleBest} solved",
            icon = Icons.Default.Spellcheck,
            accentColor = VioletAccent
        ),
        MiniGameItem(
            id = "number_memory",
            title = "Number Memory Flash",
            subtitle = "Transient recall with increasing digit span",
            domain = "Working Memory Span",
            bestScoreLabel = "Best: ${userStats.numberMemoryBestDigits} digits",
            icon = Icons.Default.Memory,
            accentColor = AmberGold
        ),
        MiniGameItem(
            id = "stroop_challenge",
            title = "Stroop Color Challenge",
            subtitle = "Neurocognitive inhibition & selective attention",
            domain = "Executive Inhibition",
            bestScoreLabel = "Best: ${userStats.stroopBestScore} pts",
            icon = Icons.Default.ColorLens,
            accentColor = StreakFire
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(18.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Sector 1: Cognitive Arcade",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "5 Neurocognitive Assessment Modules",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dual Sector Switcher Bar (Instant toggle between Cognitive Games & Aptitude Road)
        SectorSwitchSegmentBar(
            activeSector = 1,
            onSelectSector1 = { /* Already on Sector 1 */ },
            onSelectSector2 = onNavigateSector2
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Cyber Arcade Hero Banner
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        Brush.horizontalGradient(
                            listOf(CyanAccent.copy(alpha = 0.7f), CardBorder, VioletAccent.copy(alpha = 0.5f))
                        )
                    ),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        CyanAccent.copy(alpha = 0.12f),
                                        MidnightBg.copy(alpha = 0.9f)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CyanAccent.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, CyanAccent.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = CyanAccent,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "ADAPTIVE DIFFICULTY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberGold.copy(alpha = 0.18f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, AmberGold.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "+50-250 XP/Game",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberGold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Train Cognitive Reflexes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Fast-paced micro sessions calibrated to enhance your processing speed, working memory span, and selective focus.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            items(games) { game ->
                MiniGameCard(
                    game = game,
                    onClick = { onLaunchGame(game.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun MiniGameCard(
    game: MiniGameItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(game.accentColor.copy(alpha = 0.65f), CardBorder)
            )
        ),
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(game.accentColor.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Pill with glowing border
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(game.accentColor.copy(alpha = 0.25f), CardDark)
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(1.dp, game.accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = game.title,
                        tint = game.accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = game.accentColor.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, game.accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = game.domain.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = game.accentColor,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = game.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = game.subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.bestScoreLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play Button Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = game.accentColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, game.accentColor.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = game.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "PLAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = game.accentColor
                        )
                    }
                }
            }
        }
    }
}
