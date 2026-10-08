package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStats
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity Cybernetic Brain Hologram Card.
 * Renders an interactive animated canvas with pulsing neural nodes,
 * rotating quantum orbits, gradient glows, and live readiness telemetry.
 */
@Composable
fun CyberBrainHologramCard(
    userStats: UserStats,
    unlockedLevelsCount: Int,
    totalStarsCount: Int,
    onQuickStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyberHologram")

    // Rotation angles for inner and outer orbits
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerOrbit"
    )

    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerOrbit"
    )

    // Breathing glow effect
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val particleFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleFloat"
    )

    val readinessScore = (70 + (userStats.level * 3) + (totalStarsCount * 2)).coerceIn(75, 99)

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = CardDark,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    CyanAccent.copy(alpha = 0.7f),
                    VioletAccent.copy(alpha = 0.6f),
                    PurpleAccent.copy(alpha = 0.4f),
                    CyanAccent.copy(alpha = 0.7f)
                )
            )
        ),
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.12f),
                            VioletAccent.copy(alpha = 0.08f),
                            MidnightBg.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Tag Bar: Live Telemetry Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyanAccent.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, CyanAccent.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(EmeraldSuccess, CircleShape)
                            )
                            Text(
                                text = "NEURAL SYNC ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccent,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Level Trophy Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberGold.copy(alpha = 0.16f),
                        border = BorderStroke(0.8.dp, AmberGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "LVL ${userStats.level}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Middle Section: Animated Holographic Brain Canvas + Dynamic Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated Hologram Canvas
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.minDimension / 2f

                            // Central pulsing radial aura
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        CyanAccent.copy(alpha = 0.45f * pulseScale.coerceIn(0.6f, 1f)),
                                        VioletAccent.copy(alpha = 0.2f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = radius * 0.9f
                                ),
                                center = center,
                                radius = radius * 0.9f
                            )

                            // Outer dashed orbit
                            rotate(outerRotation, pivot = center) {
                                drawCircle(
                                    color = VioletAccent.copy(alpha = 0.5f),
                                    center = center,
                                    radius = radius * 0.88f,
                                    style = Stroke(
                                        width = 2f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                                    )
                                )
                                // Synapse node on outer orbit
                                val nodeAngle = Math.PI / 4
                                val nx = center.x + (radius * 0.88f) * cos(nodeAngle).toFloat()
                                val ny = center.y + (radius * 0.88f) * sin(nodeAngle).toFloat()
                                drawCircle(
                                    color = VioletAccent,
                                    center = Offset(nx, ny),
                                    radius = 4f
                                )
                            }

                            // Inner rotating orbit
                            rotate(innerRotation, pivot = center) {
                                drawCircle(
                                    color = CyanAccent.copy(alpha = 0.7f),
                                    center = center,
                                    radius = radius * 0.65f,
                                    style = Stroke(
                                        width = 2.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                                    )
                                )

                                // Three inner nodes connected by thin lines
                                for (i in 0 until 3) {
                                    val angle = (i * 2 * Math.PI / 3)
                                    val px = center.x + (radius * 0.65f) * cos(angle).toFloat()
                                    val py = center.y + (radius * 0.65f) * sin(angle).toFloat()
                                    drawLine(
                                        color = CyanAccent.copy(alpha = 0.35f),
                                        start = center,
                                        end = Offset(px, py),
                                        strokeWidth = 1.5f
                                    )
                                    drawCircle(
                                        color = CyanAccent,
                                        center = Offset(px, py),
                                        radius = 3.5f
                                    )
                                }
                            }

                            // Rising floating particle sparkles
                            for (p in 0 until 4) {
                                val px = center.x + (p - 1.5f) * 22f
                                val progress = (particleFloat + (p * 0.25f)) % 1f
                                val py = center.y + 30f - (progress * 60f)
                                val alpha = (1f - progress) * 0.8f
                                drawCircle(
                                    color = CyanAccent.copy(alpha = alpha.coerceIn(0f, 1f)),
                                    center = Offset(px, py),
                                    radius = 2.5f
                                )
                            }
                        }

                        // Brain Center Icon
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Hologram Core",
                            tint = TextPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Readiness Score & Cognitive Profile
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEURAL READINESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$readinessScore%",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OPTIMAL FOCUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }

                        // Dynamic XP Progress Track
                        val progressInLevel = (userStats.xp % 500) / 500f
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "XP ${userStats.xp} pts",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${userStats.xp % 500}/500 to Next Level",
                                fontSize = 11.sp,
                                color = AmberGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ClipGradientProgress(progress = progressInLevel)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom 3 Quick Metrics Pill Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        icon = Icons.Default.Bolt,
                        label = "Speed Math",
                        value = "${userStats.speedMathBest} pts",
                        accentColor = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        icon = Icons.Default.AutoAwesome,
                        label = "Road Stars",
                        value = "$totalStarsCount ★",
                        accentColor = AmberGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        icon = Icons.Default.Psychology,
                        label = "Sectors",
                        value = "2 Live",
                        accentColor = VioletAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Call to Action: Quick Brain Sprint Button
                Button(
                    onClick = onQuickStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("quick_brain_sprint_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CyanAccent, VioletAccent, PurpleAccent)
                                ),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MidnightBg,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DAILY COGNITIVE WORKOUT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MidnightBg,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardDark.copy(alpha = 0.7f),
        border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ClipGradientProgress(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MidnightBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0.05f, 1f))
                .height(7.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(CyanAccent, VioletAccent, AmberGold)
                    )
                )
        )
    }
}
