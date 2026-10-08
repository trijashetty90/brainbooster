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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthenticatedUser
import com.example.data.model.DailyStreakState
import com.example.data.model.UserStats
import com.example.ui.components.CyberBrainHologramCard
import com.example.ui.components.DailyGoalProgressCard
import com.example.ui.components.DailyStreakCard
import com.example.ui.components.DailyStreakRewardCalendarDialog
import com.example.ui.components.SectorSwitchSegmentBar
import com.example.ui.components.StreakBadge
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

@Composable
fun DashboardScreen(
    userStats: UserStats,
    currentUser: AuthenticatedUser?,
    dailyStreakState: DailyStreakState,
    isSfxEnabled: Boolean,
    isMusicEnabled: Boolean,
    unlockedLevelsCount: Int,
    totalStarsCount: Int,
    onToggleSfx: () -> Unit,
    onToggleMusic: () -> Unit,
    onClaimDailyStreak: () -> Unit,
    onNavigateAuth: () -> Unit,
    onLogout: () -> Unit,
    onNavigateSector1: () -> Unit,
    onNavigateSector2: () -> Unit,
    onNavigateAnalytics: () -> Unit,
    onNavigateReport: () -> Unit,
    onNavigateInspector: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showAccountDialog by remember { mutableStateOf(false) }
    var showStreakCalendarDialog by remember { mutableStateOf(false) }

    if (showAccountDialog) {
        AccountInfoDialog(
            user = currentUser,
            onDismiss = { showAccountDialog = false },
            onSwitchAccount = {
                showAccountDialog = false
                onNavigateAuth()
            },
            onLogout = {
                showAccountDialog = false
                onLogout()
            }
        )
    }

    if (showStreakCalendarDialog) {
        DailyStreakRewardCalendarDialog(
            streakState = dailyStreakState,
            onDismiss = { showStreakCalendarDialog = false },
            onClaimReward = {
                onClaimDailyStreak()
                showStreakCalendarDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .verticalScroll(scrollState)
            .padding(18.dp)
    ) {
        // App Header: Branding + Audio Controls + User Account Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            Brush.linearGradient(listOf(CyanAccent, VioletAccent)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "BrainBoost Logo",
                        tint = MidnightBg,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "BrainBoost",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentUser?.displayName ?: "Guest Explorer",
                        fontSize = 11.sp,
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Controls Cluster: SFX toggle, Music toggle, Account Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // SFX Toggle
                Surface(
                    shape = CircleShape,
                    color = if (isSfxEnabled) CyanAccent.copy(alpha = 0.2f) else CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSfxEnabled) CyanAccent else CardBorder),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onToggleSfx() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isSfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle SFX",
                            tint = if (isSfxEnabled) CyanAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Music Toggle
                Surface(
                    shape = CircleShape,
                    color = if (isMusicEnabled) VioletAccent.copy(alpha = 0.2f) else CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isMusicEnabled) VioletAccent else CardBorder),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onToggleMusic() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Toggle Music",
                            tint = if (isMusicEnabled) VioletAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Account Avatar Pill
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .height(36.dp)
                        .clickable {
                            if (currentUser == null) {
                                onNavigateAuth()
                            } else {
                                showAccountDialog = true
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            tint = if (currentUser != null) EmeraldSuccess else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (currentUser != null) "Account" else "Sign In",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Cybernetic Brain Hologram Hero Centerpiece
        CyberBrainHologramCard(
            userStats = userStats,
            unlockedLevelsCount = unlockedLevelsCount,
            totalStarsCount = totalStarsCount,
            onQuickStart = onNavigateSector1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Login Streak Reward Card
        DailyStreakCard(
            streakState = dailyStreakState,
            onOpenCalendar = { showStreakCalendarDialog = true },
            onClaimToday = onClaimDailyStreak
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Minimalist Dark-Mode Animated Daily Goal Progress
        DailyGoalProgressCard(
            completed = userStats.dailyGoalCompleted,
            target = userStats.dailyGoalTarget,
            streak = userStats.streak
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Telemetry Metrics Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricPill(
                icon = Icons.Default.Star,
                label = "Total Stars",
                value = "$totalStarsCount / 36",
                color = AmberGold,
                modifier = Modifier.weight(1f)
            )
            StatMetricPill(
                icon = Icons.Default.CheckCircle,
                label = "Questions Solved",
                value = "${userStats.totalQuestionsSolved}",
                color = EmeraldSuccess,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricPill(
                icon = Icons.Default.LocalFireDepartment,
                label = "Streak Flame",
                value = "🔥 ${userStats.streak} Days",
                color = StreakFire,
                modifier = Modifier.weight(1f)
            )
            StatMetricPill(
                icon = Icons.Default.AutoAwesome,
                label = "Total XP",
                value = "${userStats.xp} XP",
                color = CyanAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section Title: Progress Analytics Feature
        Text(
            text = "PERFORMANCE ANALYTICS & INSIGHTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Analytics Hero Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateAnalytics() },
            shape = RoundedCornerShape(20.dp),
            color = CardDark,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, CyanAccent.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.12f),
                                VioletAccent.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(CyanAccent.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analytics",
                            tint = CyanAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Progress Analytics Dashboard",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Day-by-day domain drift (Today vs Tomorrow), cognitive mastery charts, and placement readiness.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Analytics",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dual Sector Quick Switcher
        Text(
            text = "TWO MAIN SECTORS (INSTANT SWITCH)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectorSwitchSegmentBar(
            activeSector = 0,
            onSelectSector1 = onNavigateSector1,
            onSelectSector2 = onNavigateSector2
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Core Architecture Sectors
        Text(
            text = "CORE ARCHITECTURE SECTORS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sector 1: Cognitive Arcade Launcher Card
        SectorLauncherCard(
            sectorBadge = "SECTOR 1",
            title = "Cognitive Arcade",
            subtitle = "5 Neurocognitive assessment mini-games",
            tagline = "Speed Math • Memory Flip • Anagrams • Number Flash • Stroop",
            accentColor = CyanAccent,
            icon = Icons.Default.Games,
            actionLabel = "Launch Arcade (5 Games)",
            onClick = onNavigateSector1
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sector 2: Aptitude Road Launcher Card
        SectorLauncherCard(
            sectorBadge = "SECTOR 2",
            title = "Aptitude Road",
            subtitle = "Winding map progression • IndiaBIX Syllabus",
            tagline = "Gemini 3.5 Flash Real-Time Generation • 3-Star System • Timers",
            accentColor = VioletAccent,
            icon = Icons.Default.Explore,
            actionLabel = "Explore Winding Road ($unlockedLevelsCount Unlocked)",
            onClick = onNavigateSector2
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section Title: College Engineering Project Suite
        Text(
            text = "ACADEMIC DOCUMENTATION & INSPECTION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Project Report Card
        AcademicToolCard(
            icon = Icons.Default.Description,
            title = "College Project Report",
            subtitle = "IEEE 830 SRS, UML Diagrams, DFDs & 8 Academic Chapters",
            accentColor = AmberGold,
            onClick = onNavigateReport
        )

        Spacer(modifier = Modifier.height(10.dp))

        // AsyncStorage Inspector Card
        AcademicToolCard(
            icon = Icons.Default.DataObject,
            title = "AsyncStorage Telemetry Inspector",
            subtitle = "Live examination of React Native local persistence JSON stores",
            accentColor = EmeraldSuccess,
            onClick = onNavigateInspector
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun AccountInfoDialog(
    user: AuthenticatedUser?,
    onDismiss: () -> Unit,
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Text(
                text = "Authenticated Account",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Name: ${user?.displayName ?: "Scholar"}",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Email: ${user?.email ?: "guest@brainboost.edu"}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All your XP, star ratings, and game telemetry are securely encrypted and associated with this account.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSwitchAccount,
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Text("Switch Account", color = MidnightBg, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out", tint = TextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Out", color = TextPrimary)
            }
        }
    )
}

@Composable
private fun StatMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, fontSize = 11.sp, color = TextSecondary)
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun SectorLauncherCard(
    sectorBadge: String,
    title: String,
    subtitle: String,
    tagline: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionLabel: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(
            1.4.dp,
            Brush.horizontalGradient(
                listOf(accentColor.copy(alpha = 0.7f), CardBorder, accentColor.copy(alpha = 0.4f))
            )
        ),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, accentColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = sectorBadge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(accentColor.copy(alpha = 0.25f), CardDark)
                                ),
                                CircleShape
                            )
                            .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tagline,
                    fontSize = 11.sp,
                    color = accentColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.16f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = actionLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AcademicToolCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
