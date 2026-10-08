package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CognitiveAnalytics
import com.example.data.model.CognitiveInsight
import com.example.data.model.DayDomainPerformance
import com.example.data.model.DomainDayByDayTrend
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightBg
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.StreakFire
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.BrainBoostViewModel

@Composable
fun ProgressAnalyticsScreen(
    viewModel: BrainBoostViewModel,
    onBack: () -> Unit
) {
    val analytics by viewModel.cognitiveAnalytics.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val selectedDomain by viewModel.selectedAnalyticsDomain.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .verticalScroll(scrollState)
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
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Progress Analytics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Cognitive Agility & Syllabus Aptitude Trends",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Composite Cognitive Agility Index Card
        CompositeAgilityCard(
            agilityIndex = analytics?.compositeAgilityIndex ?: 845,
            level = userStats.level
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 7-Day Performance Trend Chart
        SectionTitle(title = "7-DAY ACTIVITY & XP TRAJECTORY", icon = Icons.Default.Timeline)
        Spacer(modifier = Modifier.height(10.dp))
        WeeklyTrendChartCard(trends = analytics?.dailyXpTrends ?: emptyList())

        Spacer(modifier = Modifier.height(24.dp))

        // Mini-Games Domain Mastery (Horizontal Performance Breakdown)
        SectionTitle(title = "SECTOR 1: COGNITIVE DOMAINS MASTERY", icon = Icons.Default.Psychology)
        Spacer(modifier = Modifier.height(10.dp))
        CognitiveDomainsCard(domainScores = analytics?.domainScores ?: emptyMap())

        Spacer(modifier = Modifier.height(24.dp))

        // Day-by-Day Domain Comparative Analytics & Drift
        SectionTitle(title = "DAY-BY-DAY DOMAIN COMPARATIVE ANALYTICS", icon = Icons.Default.CompareArrows)
        Spacer(modifier = Modifier.height(10.dp))
        DayByDayAnalyticsCard(
            analytics = analytics,
            selectedDomain = selectedDomain,
            onSelectDomain = { viewModel.selectAnalyticsDomain(it) },
            onSetPreset = { domain, today, tomorrow ->
                viewModel.setDayComparisonPreset(domain, today, tomorrow)
            },
            onUpdateTodayScore = { domain, score ->
                viewModel.updateDomainDayScore(domain, "Today", score)
            },
            onUpdateTomorrowScore = { domain, score ->
                viewModel.updateDomainDayScore(domain, "Tomorrow", score)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sector 2: Aptitude Road Performance
        SectionTitle(title = "SECTOR 2: INDIABIX APTITUDE ACCURACY", icon = Icons.Default.TrendingUp)
        Spacer(modifier = Modifier.height(10.dp))
        AptitudeAccuracyCard(
            quantAccuracy = analytics?.quantitativeAccuracy ?: 83,
            logicalAccuracy = analytics?.logicalAccuracy ?: 89
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cognitive Strengths
        SectionTitle(title = "IDENTIFIED COGNITIVE STRENGTHS", icon = Icons.Default.CheckCircle)
        Spacer(modifier = Modifier.height(10.dp))
        analytics?.topStrengths?.forEach { strength ->
            InsightCard(insight = strength, isStrength = true)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Areas for Improvement & Recommendations
        SectionTitle(title = "AREAS FOR IMPROVEMENT & RECOMMENDATIONS", icon = Icons.Default.Lightbulb)
        Spacer(modifier = Modifier.height(10.dp))
        analytics?.areasForImprovement?.forEach { weakness ->
            InsightCard(insight = weakness, isStrength = false)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SectionTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun CompositeAgilityCard(
    agilityIndex: Int,
    level: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CardBorder)
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanAccent.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "TIER: ADVANCED SCHOLAR (TOP 8%)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cognitive Agility Index",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$agilityIndex",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = " / 1000",
                            fontSize = 15.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                        )
                    }
                    Text(
                        text = "Composite metric calculated from reaction speed, working memory span, and aptitude accuracy.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.linearGradient(listOf(CyanAccent, VioletAccent)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Agility",
                        tint = MidnightBg,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyTrendChartCard(
    trends: List<Pair<String, Int>>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly XP Accumulation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "+${trends.sumOf { it.second }} XP this week",
                    fontSize = 12.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Line Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (trends.isEmpty()) return@Canvas

                    val maxY = (trends.maxOfOrNull { it.second } ?: 200).coerceAtLeast(100).toFloat()
                    val spacing = size.width / (trends.size - 1).coerceAtLeast(1)

                    val path = Path()
                    val points = mutableListOf<Offset>()

                    trends.forEachIndexed { i, entry ->
                        val x = i * spacing
                        val y = size.height - (entry.second / maxY * (size.height - 24.dp.toPx())) - 12.dp.toPx()
                        points.add(Offset(x, y))
                        if (i == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    // Draw area gradient under curve
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )

                    // Draw line stroke
                    drawPath(
                        path = path,
                        color = CyanAccent,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw points
                    points.forEach { pt ->
                        drawCircle(
                            color = MidnightBg,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = CyanAccent,
                            radius = 3.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trends.forEach { (day, _) ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CognitiveDomainsCard(
    domainScores: Map<String, Int>
) {
    val domains = listOf(
        Pair("Mental Calculation", domainScores["Mental Calculation"] ?: 82),
        Pair("Visual Memory", domainScores["Visual Memory"] ?: 88),
        Pair("Verbal Reasoning", domainScores["Verbal Reasoning"] ?: 76),
        Pair("Working Memory Span", domainScores["Working Memory"] ?: 84),
        Pair("Executive Inhibition", domainScores["Executive Inhibition"] ?: 94)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            domains.forEach { (name, score) ->
                val barColor = when {
                    score >= 90 -> EmeraldSuccess
                    score >= 80 -> CyanAccent
                    score >= 70 -> AmberGold
                    else -> CrimsonError
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "$score%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = barColor)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { score / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = barColor,
                        trackColor = CardBorder
                    )
                }
            }
        }
    }
}

@Composable
private fun AptitudeAccuracyCard(
    quantAccuracy: Int,
    logicalAccuracy: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Placement Curriculum Readiness",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quantitative
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MidnightBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Quantitative", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$quantAccuracy%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Time & Work, Profit & Loss, Percentages", fontSize = 10.sp, color = TextMuted, maxLines = 1)
                    }
                }

                // Logical
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MidnightBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VioletAccent.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Logical Reasoning", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$logicalAccuracy%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VioletAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Syllogisms, Series, Blood Relations", fontSize = 10.sp, color = TextMuted, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: CognitiveInsight,
    isStrength: Boolean
) {
    val accentColor = if (isStrength) EmeraldSuccess else AmberGold
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = insight.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = insight.scoreBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = insight.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "Actionable Drill: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Text(
                    text = insight.recommendation,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DayByDayAnalyticsCard(
    analytics: CognitiveAnalytics?,
    selectedDomain: String,
    onSelectDomain: (String) -> Unit,
    onSetPreset: (domain: String, today: Int, tomorrow: Int) -> Unit,
    onUpdateTodayScore: (domain: String, newScore: Int) -> Unit,
    onUpdateTomorrowScore: (domain: String, newScore: Int) -> Unit
) {
    val domainTrends = analytics?.dayByDayDomainTrends ?: emptyList()
    val activeTrend = domainTrends.find { it.domain == selectedDomain }
        ?: domainTrends.firstOrNull()
        ?: DomainDayByDayTrend(
            domain = selectedDomain,
            todayScore = 88,
            yesterdayScore = 74,
            tomorrowScore = 70,
            deltaVsYesterday = 14,
            deltaVsTomorrow = 18,
            isHigherTodayThanYesterday = true,
            isHigherTodayThanTomorrow = true,
            comparisonHeadline = "Today's Mental Calculation (88%) is HIGHER than Tomorrow (70%) by +18% and Yesterday (74%) by +14%",
            explanation = "Day-by-Day Analysis: Today's Mental Calculation (88%) significantly outperformed the projected/subsequent tomorrow session (70%), showing a -18% decrease tomorrow. Peak sprint performance today followed by cognitive fatigue.",
            dailyScores = listOf(
                DayDomainPerformance("Mon", 68, 0, "Baseline speed test"),
                DayDomainPerformance("Tue", 72, 4, "Speed Math 14 answers"),
                DayDomainPerformance("Wed", 78, 6, "Rapid mental addition"),
                DayDomainPerformance("Thu", 75, -3, "Slight latency on division"),
                DayDomainPerformance("Fri", 82, 7, "Clean multiplier streaks"),
                DayDomainPerformance("Yesterday", 74, -8, "Time pressure on 2-digit multiplication"),
                DayDomainPerformance("Today", 88, 14, "Peak agility: 18 correct calculations", isToday = true),
                DayDomainPerformance("Tomorrow", 70, -18, "Projected fatigue drop (-18%)", isTomorrow = true)
            )
        )

    val domainsList = listOf(
        "Mental Calculation" to "⚡",
        "Visual Memory" to "🧩",
        "Verbal Reasoning" to "🔤",
        "Working Memory" to "🔢",
        "Executive Inhibition" to "🎨",
        "Quantitative" to "📐",
        "Logical" to "💡"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Comparison State Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DAY-BY-DAY COMPARATIVE DRIFT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeTrend.isHigherTodayThanTomorrow) EmeraldSuccess.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (activeTrend.isHigherTodayThanTomorrow) "TODAY > TOMORROW" else "TODAY < TOMORROW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (activeTrend.isHigherTodayThanTomorrow) EmeraldSuccess else AmberGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Domain Selector Chips Row
            val domainChipScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(domainChipScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                domainsList.forEach { (domain, emoji) ->
                    val isSelected = domain == selectedDomain
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectDomain(domain) },
                        color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else MidnightBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanAccent else CardBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = emoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = domain,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyanAccent else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Comparative Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MidnightBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (activeTrend.isHigherTodayThanTomorrow) EmeraldSuccess.copy(alpha = 0.4f) else AmberGold.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (activeTrend.isHigherTodayThanTomorrow) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (activeTrend.isHigherTodayThanTomorrow) EmeraldSuccess else CrimsonError,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeTrend.isHigherTodayThanTomorrow)
                                "Today is MORE (+${activeTrend.deltaVsTomorrow}%), Tomorrow is LESS (-${activeTrend.deltaVsTomorrow}%)"
                            else
                                "Today is LESS (-${-activeTrend.deltaVsTomorrow}%), Tomorrow is MORE (+${-activeTrend.deltaVsTomorrow}%)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activeTrend.isHigherTodayThanTomorrow) EmeraldSuccess else AmberGold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = activeTrend.comparisonHeadline,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = activeTrend.explanation,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Three-Day Comparison Metric Cards (Yesterday, Today, Tomorrow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Yesterday Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "YESTERDAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeTrend.yesterdayScore}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Benchmark",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                // Today Card (Prominently Highlighted)
                Surface(
                    modifier = Modifier.weight(1.15f),
                    shape = RoundedCornerShape(12.dp),
                    color = CyanAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "TODAY", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = CyanAccent)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "⚡", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeTrend.todayScore}%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeTrend.deltaVsYesterday >= 0) "+${activeTrend.deltaVsYesterday}% vs yest" else "${activeTrend.deltaVsYesterday}% vs yest",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTrend.deltaVsYesterday >= 0) EmeraldSuccess else CrimsonError
                        )
                    }
                }

                // Tomorrow Card
                val tomorrowColor = if (activeTrend.isHigherTodayThanTomorrow) CrimsonError else EmeraldSuccess
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, tomorrowColor.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "TOMORROW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeTrend.tomorrowScore}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tomorrowColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeTrend.isHigherTodayThanTomorrow) "-${activeTrend.deltaVsTomorrow}% (Less)" else "+${-activeTrend.deltaVsTomorrow}% (More)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tomorrowColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 8-Day Day-by-Day Bar Chart
            Text(
                text = "DAY-BY-DAY PROGRESSION SEQUENCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Bar visualizer
            DayByDayBarVisualizer(dailyScores = activeTrend.dailyScores)

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Day-by-Day Scenario Tester
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MidnightBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate Day-by-Day Shift",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Live Telemetry",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Scenario 1: Today More, Tomorrow Less
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSetPreset(selectedDomain, 88, 70)
                                },
                            color = if (activeTrend.isHigherTodayThanTomorrow) CyanAccent.copy(alpha = 0.25f) else CardDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (activeTrend.isHigherTodayThanTomorrow) CyanAccent else CardBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚡ Today > Tmrw",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTrend.isHigherTodayThanTomorrow) CyanAccent else TextPrimary
                                )
                                Text(
                                    text = "88% vs 70% (-18%)",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Scenario 2: Today Less, Tomorrow More
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSetPreset(selectedDomain, 68, 86)
                                },
                            color = if (!activeTrend.isHigherTodayThanTomorrow) VioletAccent.copy(alpha = 0.25f) else CardDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (!activeTrend.isHigherTodayThanTomorrow) VioletAccent else CardBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🌱 Today < Tmrw",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!activeTrend.isHigherTodayThanTomorrow) VioletAccent else TextPrimary
                                )
                                Text(
                                    text = "68% vs 86% (+18%)",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Scenario 3: Apex Surge (Today 95%, Tomorrow 65%)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSetPreset(selectedDomain, 95, 65)
                                },
                            color = CardDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔥 Apex Surge",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StreakFire
                                )
                                Text(
                                    text = "95% vs 65% (-30%)",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step adjustments for Today and Tomorrow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Today Score adjust
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Today: ", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${activeTrend.todayScore}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ScoreMiniButton(text = "-5") {
                                onUpdateTodayScore(selectedDomain, (activeTrend.todayScore - 5).coerceAtLeast(40))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            ScoreMiniButton(text = "+5") {
                                onUpdateTodayScore(selectedDomain, (activeTrend.todayScore + 5).coerceAtMost(100))
                            }
                        }

                        // Tomorrow Score adjust
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Tmrw: ", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${activeTrend.tomorrowScore}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTrend.isHigherTodayThanTomorrow) CrimsonError else EmeraldSuccess
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ScoreMiniButton(text = "-5") {
                                onUpdateTomorrowScore(selectedDomain, (activeTrend.tomorrowScore - 5).coerceAtLeast(40))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            ScoreMiniButton(text = "+5") {
                                onUpdateTomorrowScore(selectedDomain, (activeTrend.tomorrowScore + 5).coerceAtMost(100))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day-by-Day Historical Drill Entries
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                activeTrend.dailyScores.forEach { dayScore ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            dayScore.isToday -> CyanAccent.copy(alpha = 0.1f)
                            dayScore.isTomorrow -> (if (activeTrend.isHigherTodayThanTomorrow) CrimsonError else EmeraldSuccess).copy(alpha = 0.08f)
                            else -> MidnightBg
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            when {
                                dayScore.isToday -> CyanAccent.copy(alpha = 0.5f)
                                dayScore.isTomorrow -> (if (activeTrend.isHigherTodayThanTomorrow) CrimsonError else EmeraldSuccess).copy(alpha = 0.4f)
                                else -> CardBorder.copy(alpha = 0.6f)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = dayScore.day,
                                    fontSize = 11.sp,
                                    fontWeight = if (dayScore.isToday || dayScore.isTomorrow) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        dayScore.isToday -> CyanAccent
                                        dayScore.isTomorrow -> if (activeTrend.isHigherTodayThanTomorrow) CrimsonError else EmeraldSuccess
                                        else -> TextSecondary
                                    },
                                    modifier = Modifier.width(70.dp)
                                )
                                Text(
                                    text = dayScore.note,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    maxLines = 1
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${dayScore.score}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        dayScore.score >= 85 -> EmeraldSuccess
                                        dayScore.score >= 70 -> CyanAccent
                                        else -> AmberGold
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val deltaStr = when {
                                    dayScore.deltaFromPrevDay > 0 -> "+${dayScore.deltaFromPrevDay}%"
                                    dayScore.deltaFromPrevDay < 0 -> "${dayScore.deltaFromPrevDay}%"
                                    else -> "—"
                                }
                                val deltaColor = when {
                                    dayScore.deltaFromPrevDay > 0 -> EmeraldSuccess
                                    dayScore.deltaFromPrevDay < 0 -> CrimsonError
                                    else -> TextMuted
                                }
                                Text(
                                    text = deltaStr,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = deltaColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayByDayBarVisualizer(
    dailyScores: List<DayDomainPerformance>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MidnightBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Chart Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyScores.forEach { dayScore ->
                    val barHeightDp = ((dayScore.score / 100f) * 65f).coerceIn(12f, 65f).dp
                    val barColor = when {
                        dayScore.isToday -> CyanAccent
                        dayScore.isTomorrow -> if (dayScore.deltaFromPrevDay < 0) CrimsonError else EmeraldSuccess
                        dayScore.score >= 85 -> EmeraldSuccess.copy(alpha = 0.7f)
                        dayScore.score >= 75 -> VioletAccent.copy(alpha = 0.7f)
                        else -> CardBorder
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${dayScore.score}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dayScore.isToday) CyanAccent else TextMuted
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(barHeightDp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(barColor, barColor.copy(alpha = 0.35f))
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Day labels and delta indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dailyScores.forEach { dayScore ->
                    val label = when (dayScore.day) {
                        "Yesterday" -> "Yest"
                        "Tomorrow" -> "Tmrw"
                        else -> dayScore.day
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            fontWeight = if (dayScore.isToday || dayScore.isTomorrow) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                dayScore.isToday -> CyanAccent
                                dayScore.isTomorrow -> if (dayScore.deltaFromPrevDay < 0) CrimsonError else EmeraldSuccess
                                else -> TextSecondary
                            }
                        )
                        val deltaText = when {
                            dayScore.deltaFromPrevDay > 0 -> "+${dayScore.deltaFromPrevDay}"
                            dayScore.deltaFromPrevDay < 0 -> "${dayScore.deltaFromPrevDay}"
                            else -> "•"
                        }
                        Text(
                            text = deltaText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                dayScore.deltaFromPrevDay > 0 -> EmeraldSuccess
                                dayScore.deltaFromPrevDay < 0 -> CrimsonError
                                else -> TextMuted
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreMiniButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
