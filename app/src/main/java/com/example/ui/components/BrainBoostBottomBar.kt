package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

data class BottomNavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val testTag: String,
    val activeColor: Color,
    val badgeText: String? = null
)

/**
 * Modern Material 3 Bottom Navigation Bar for switching between
 * the two main sectors (Sector 1: Cognitive Games, Sector 2: Aptitude Road)
 * along with quick access to the Home Dashboard and Analytics.
 */
@Composable
fun BrainBoostBottomBar(
    currentScreen: AppScreen,
    unlockedAptitudeLevels: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            screen = AppScreen.DASHBOARD,
            label = "Dashboard",
            icon = Icons.Default.Dashboard,
            testTag = "bottom_nav_dashboard",
            activeColor = AmberGold
        ),
        BottomNavItem(
            screen = AppScreen.SECTOR_1_ARCADE,
            label = "Cognitive Games",
            icon = Icons.Default.Psychology,
            testTag = "bottom_nav_cognitive_games",
            activeColor = CyanAccent,
            badgeText = "5"
        ),
        BottomNavItem(
            screen = AppScreen.SECTOR_2_ROAD,
            label = "Aptitude Road",
            icon = Icons.Default.Explore,
            testTag = "bottom_nav_aptitude_road",
            activeColor = VioletAccent,
            badgeText = if (unlockedAptitudeLevels > 0) "${unlockedAptitudeLevels}L" else null
        ),
        BottomNavItem(
            screen = AppScreen.PROGRESS_ANALYTICS,
            label = "Analytics",
            icon = Icons.Default.Analytics,
            testTag = "bottom_nav_analytics",
            activeColor = EmeraldSuccess
        )
    )

    Surface(
        color = CardDark,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Luminous ambient top border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                CyanAccent.copy(alpha = 0.7f),
                                VioletAccent.copy(alpha = 0.8f),
                                AmberGold.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
            items.forEach { item ->
                val isSelected = currentScreen == item.screen

                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) item.activeColor else TextMuted,
                    animationSpec = tween(durationMillis = 200),
                    label = "iconTint"
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            onNavigate(item.screen)
                        }
                    },
                    icon = {
                        if (item.badgeText != null) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = if (isSelected) item.activeColor else TextMuted.copy(alpha = 0.7f),
                                        contentColor = MidnightBg
                                    ) {
                                        Text(
                                            text = item.badgeText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextMuted,
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = item.activeColor,
                        unselectedIconColor = TextMuted,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextMuted,
                        indicatorColor = item.activeColor.copy(alpha = 0.16f)
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
}

/**
 * Dual Sector Segment Switcher to allow instant toggle between
 * Sector 1 (Cognitive Games) and Sector 2 (Aptitude Road)
 * at the top of sector screens or on the dashboard.
 */
@Composable
fun SectorSwitchSegmentBar(
    activeSector: Int, // 1 for Cognitive Games, 2 for Aptitude Road
    onSelectSector1: () -> Unit,
    onSelectSector2: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MidnightBg.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, CardBorder),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Sector 1 Button
            val isSector1 = activeSector == 1
            val sector1BgModifier = if (isSector1) {
                Modifier.background(
                    Brush.horizontalGradient(
                        listOf(CyanAccent.copy(alpha = 0.25f), VioletAccent.copy(alpha = 0.15f))
                    )
                )
            } else {
                Modifier.background(Color.Transparent)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(sector1BgModifier)
                    .border(
                        width = 1.dp,
                        color = if (isSector1) CyanAccent.copy(alpha = 0.6f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectSector1() }
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .testTag("segment_sector_1_cognitive"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Cognitive Games",
                        tint = if (isSector1) CyanAccent else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Cognitive Games",
                                fontSize = 12.sp,
                                fontWeight = if (isSector1) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSector1) TextPrimary else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isSector1) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(CyanAccent, CircleShape)
                                )
                            }
                        }
                        Text(
                            text = "Sector 1 • 5 Games",
                            fontSize = 10.sp,
                            color = if (isSector1) CyanAccent else TextMuted
                        )
                    }
                }
            }

            // Sector 2 Button
            val isSector2 = activeSector == 2
            val sector2BgModifier = if (isSector2) {
                Modifier.background(
                    Brush.horizontalGradient(
                        listOf(VioletAccent.copy(alpha = 0.25f), CyanAccent.copy(alpha = 0.15f))
                    )
                )
            } else {
                Modifier.background(Color.Transparent)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(sector2BgModifier)
                    .border(
                        width = 1.dp,
                        color = if (isSector2) VioletAccent.copy(alpha = 0.6f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectSector2() }
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .testTag("segment_sector_2_aptitude"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Aptitude Road",
                        tint = if (isSector2) VioletAccent else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Aptitude Road",
                                fontSize = 12.sp,
                                fontWeight = if (isSector2) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSector2) TextPrimary else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isSector2) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(VioletAccent, CircleShape)
                                )
                            }
                        }
                        Text(
                            text = "Sector 2 • IndiaBIX AI",
                            fontSize = 10.sp,
                            color = if (isSector2) VioletAccent else TextMuted
                        )
                    }
                }
            }
        }
    }
}
