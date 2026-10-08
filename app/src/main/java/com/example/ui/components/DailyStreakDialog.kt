package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStreakState
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.StreakFire
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun DailyStreakCard(
    streakState: DailyStreakState,
    onOpenCalendar: () -> Unit,
    onClaimToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streakGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenCalendar() },
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (streakState.canClaimToday) StreakFire.copy(alpha = 0.7f) else CardBorder
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            StreakFire.copy(alpha = 0.12f),
                            AmberGold.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(if (streakState.canClaimToday) pulseScale else 1f)
                            .background(
                                Brush.linearGradient(listOf(StreakFire, AmberGold)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Flame",
                            tint = MidnightBg,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${streakState.streakCount} Days Streak",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StreakFire.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "${"%.2f".format(streakState.streakMultiplier)}x XP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StreakFire,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (streakState.canClaimToday)
                                "Day ${streakState.currentDayIndex} Reward: +${streakState.todayRewardXp} XP waiting!"
                            else
                                "Today's bonus claimed! Longest: ${streakState.longestStreak} days",
                            fontSize = 12.sp,
                            color = if (streakState.canClaimToday) AmberGold else TextSecondary
                        )
                    }
                }

                if (streakState.canClaimToday) {
                    Button(
                        onClick = onClaimToday,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StreakFire),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Claim",
                            color = MidnightBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f)),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Claimed",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyStreakRewardCalendarDialog(
    streakState: DailyStreakState,
    onDismiss: () -> Unit,
    onClaimReward: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = StreakFire,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Login Streak Rewards",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Open BrainBoost every day to claim compounding XP bonuses and maintain your 🔥 answer streak multiplier!",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Multiplier and Telemetry Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Current Streak", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "🔥 ${streakState.streakCount} Days",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = StreakFire
                            )
                        }
                        Column {
                            Text(text = "Active Multiplier", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${"%.2f".format(streakState.streakMultiplier)}x XP",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                        Column {
                            Text(text = "Total Claimed", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "+${streakState.totalBonusXpClaimed} XP",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "7-DAY PROGRESSION CALENDAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 7 Days Grid/Column
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    streakState.weekRewards.forEach { item ->
                        val isPast = item.dayNumber < streakState.currentDayIndex ||
                                (item.dayNumber == streakState.currentDayIndex && !streakState.canClaimToday)
                        val isToday = item.dayNumber == streakState.currentDayIndex && streakState.canClaimToday
                        val isFuture = item.dayNumber > streakState.currentDayIndex

                        val bgColor = when {
                            isPast -> EmeraldSuccess.copy(alpha = 0.12f)
                            isToday -> StreakFire.copy(alpha = 0.2f)
                            else -> MidnightBg
                        }

                        val borderColor = when {
                            isPast -> EmeraldSuccess.copy(alpha = 0.4f)
                            isToday -> StreakFire
                            else -> CardBorder
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(borderColor.copy(alpha = 0.25f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPast) {
                                            Icon(Icons.Default.Check, contentDescription = "Claimed", tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                        } else if (isToday) {
                                            Icon(Icons.Default.LocalFireDepartment, contentDescription = "Today", tint = StreakFire, modifier = Modifier.size(16.dp))
                                        } else {
                                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextMuted, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.title,
                                            fontSize = 13.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isPast) EmeraldSuccess else if (isToday) TextPrimary else TextSecondary
                                        )
                                        if (item.isGrandReward) {
                                            Text(
                                                text = "⭐ Weekly Milestone Apex Bonus",
                                                fontSize = 10.sp,
                                                color = AmberGold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "+${item.xpBonus} XP",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPast) EmeraldSuccess else if (isToday) StreakFire else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AsyncStorage sync footnote
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DataObject, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Persisted asynchronously in AsyncStorage [@brainboost_daily_streak]",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        },
        confirmButton = {
            if (streakState.canClaimToday) {
                Button(
                    onClick = onClaimReward,
                    colors = ButtonDefaults.buttonColors(containerColor = StreakFire),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MidnightBg, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Claim Day ${streakState.currentDayIndex} (+${streakState.todayRewardXp} XP)",
                        color = MidnightBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Claimed for Today (Return Tomorrow)",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    )
}
