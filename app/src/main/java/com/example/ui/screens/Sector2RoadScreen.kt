package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AptitudeLevel
import com.example.data.model.AptitudeQuestion
import com.example.ui.components.SectorSwitchSegmentBar
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
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.BrainBoostViewModel

@Composable
fun Sector2RoadScreen(
    viewModel: BrainBoostViewModel,
    onBack: () -> Unit,
    onNavigateSector1: () -> Unit = {}
) {
    val levels by viewModel.aptitudeLevels.collectAsState()
    val activeLevelNum by viewModel.activeLevelNumber.collectAsState()
    val quizResult by viewModel.quizFinishedResult.collectAsState()
    val isQuizLoading by viewModel.isQuizLoading.collectAsState()
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val currentQIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptionIndex.collectAsState()
    val showExplanation by viewModel.showExplanation.collectAsState()
    val timeLeft by viewModel.quizTimeRemaining.collectAsState()
    val quizStreak by viewModel.quizStreak.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
    ) {
        if (activeLevelNum != null) {
            // Level Quiz View or Quiz Result View
            if (quizResult != null) {
                QuizResultOverlay(
                    result = quizResult!!,
                    onContinue = { viewModel.exitQuiz() }
                )
            } else if (isQuizLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CyanAccent)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Synthesizing IndiaBIX Curriculum Questions...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Text("Powered by Google Gemini 3.5 Flash", color = CyanAccent, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                val currentQ = quizQuestions.getOrNull(currentQIndex)
                if (currentQ != null) {
                    ActiveQuizView(
                        levelNumber = activeLevelNum!!,
                        question = currentQ,
                        questionIndex = currentQIndex,
                        totalQuestions = quizQuestions.size,
                        selectedOption = selectedOption,
                        showExplanation = showExplanation,
                        timeLeft = timeLeft,
                        streak = quizStreak,
                        onSelectOption = { viewModel.onSelectOption(it) },
                        onNext = { viewModel.nextQuestionOrFinish() },
                        onExit = { viewModel.exitQuiz() }
                    )
                }
            }
        } else {
            // Candy Crush-style continuous winding level progression map
            WindingRoadMap(
                levels = levels,
                onBack = onBack,
                onNavigateSector1 = onNavigateSector1,
                onSelectLevel = { level ->
                    if (level.isUnlocked) {
                        viewModel.startLevelQuiz(level.levelNumber)
                    }
                }
            )
        }
    }
}

@Composable
private fun WindingRoadMap(
    levels: List<AptitudeLevel>,
    onBack: () -> Unit,
    onNavigateSector1: () -> Unit = {},
    onSelectLevel: (AptitudeLevel) -> Unit
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky Header
        Surface(
            color = CardDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "Sector 2: Aptitude Road",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "IndiaBIX Syllabus • Continuous Progression",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VioletAccent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VioletAccent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Stars", tint = AmberGold, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${levels.sumOf { it.starsEarned }} / ${levels.size * 3}",
                                color = AmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Dual Sector Switcher Bar (Instant toggle between Cognitive Games & Aptitude Road)
                SectorSwitchSegmentBar(
                    activeSector = 2,
                    onSelectSector1 = onNavigateSector1,
                    onSelectSector2 = { /* Already on Sector 2 */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Winding Road Nodes List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Road Description Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini",
                            tint = CyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Complete each milestone with ≥1 star to unlock the next IndiaBIX placement topic on the road.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            itemsIndexed(levels) { index, level ->
                // Alternating horizontal alignment (Left, Center, Right) for winding Candy Crush feel
                val horizontalAlignment = when (index % 4) {
                    0 -> Alignment.Start
                    1 -> Alignment.CenterHorizontally
                    2 -> Alignment.End
                    else -> Alignment.CenterHorizontally
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = when (horizontalAlignment) {
                        Alignment.Start -> Alignment.CenterStart
                        Alignment.End -> Alignment.CenterEnd
                        else -> Alignment.Center
                    }
                ) {
                    WindingLevelNode(
                        level = level,
                        onClick = { onSelectLevel(level) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun WindingLevelNode(
    level: AptitudeLevel,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nodePulse"
    )

    val isCurrentActive = level.isUnlocked && level.starsEarned == 0
    val isMastered = level.starsEarned == 3

    val nodeBorderColor = when {
        !level.isUnlocked -> CardBorder
        isMastered -> AmberGold
        isCurrentActive -> CyanAccent
        else -> VioletAccent
    }

    val nodeBgGradient = when {
        !level.isUnlocked -> listOf(CardBorder.copy(alpha = 0.3f), CardDark)
        isMastered -> listOf(AmberGold.copy(alpha = 0.35f), CardDark)
        isCurrentActive -> listOf(CyanAccent.copy(alpha = 0.35f), CardDark)
        else -> listOf(VioletAccent.copy(alpha = 0.35f), CardDark)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = level.isUnlocked) { onClick() }
            .padding(4.dp)
    ) {
        // Active beacon indicator or mastered tag
        if (isCurrentActive) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyanAccent,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MidnightBg,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "NEXT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MidnightBg
                    )
                }
            }
        } else if (isMastered) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AmberGold.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, AmberGold.copy(alpha = 0.5f)),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "MASTERED ★★★",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(78.dp)
                .scale(if (isCurrentActive) pulseScale else 1f)
                .background(
                    Brush.radialGradient(colors = nodeBgGradient),
                    CircleShape
                )
                .border(
                    width = if (level.isUnlocked) 2.5.dp else 1.dp,
                    color = nodeBorderColor,
                    shape = CircleShape
                )
        ) {
            if (level.isUnlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${level.levelNumber}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isMastered) AmberGold else TextPrimary
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Stars
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (i in 1..3) {
                Icon(
                    imageVector = if (i <= level.starsEarned) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Star $i",
                    tint = if (i <= level.starsEarned) AmberGold else TextMuted.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Topic Title Badge with category indicator
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (level.isUnlocked) CardDark else CardDark.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(
                0.8.dp,
                if (level.isUnlocked) nodeBorderColor.copy(alpha = 0.4f) else CardBorder
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = level.topic,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (level.isUnlocked) TextPrimary else TextMuted
                )
                if (level.category.isNotEmpty()) {
                    Text(
                        text = level.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (level.isUnlocked) nodeBorderColor else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveQuizView(
    levelNumber: Int,
    question: AptitudeQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    showExplanation: Boolean,
    timeLeft: Int,
    streak: Int,
    onSelectOption: (Int) -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        // Quiz Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit", tint = TextPrimary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Level $levelNumber: ${question.topic}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Question ${questionIndex + 1} of $totalQuestions",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            StreakBadge(streakCount = streak)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Timer Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dynamic Countdown", fontSize = 11.sp, color = TextSecondary)
            Text("${timeLeft}s", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (timeLeft <= 5) CrimsonError else CyanAccent)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { timeLeft / 30f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (timeLeft <= 5) CrimsonError else CyanAccent,
            trackColor = CardBorder
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Question Statement Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = CardDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = VioletAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "INDIABIX • ${question.topic.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VioletAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = question.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Multiple Choice Options (A, B, C, D) with immediate green/red feedback
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val optionLabels = listOf("A", "B", "C", "D")
            question.options.forEachIndexed { idx, optionText ->
                val isSelected = selectedOption == idx
                val isCorrect = idx == question.correctIndex

                val (bgColor, borderColor, textColor) = when {
                    showExplanation && isCorrect -> Triple(
                        EmeraldSuccess.copy(alpha = 0.2f),
                        EmeraldSuccess,
                        TextPrimary
                    )
                    showExplanation && isSelected && !isCorrect -> Triple(
                        CrimsonError.copy(alpha = 0.2f),
                        CrimsonError,
                        TextPrimary
                    )
                    else -> Triple(
                        CardDark,
                        CardBorder,
                        TextPrimary
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !showExplanation) { onSelectOption(idx) },
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = borderColor.copy(alpha = 0.25f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = optionLabels.getOrElse(idx) { "$idx" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (showExplanation && isCorrect) EmeraldSuccess else TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionText,
                            fontSize = 14.sp,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Step-by-Step Explanation Card
        AnimatedVisibility(visible = showExplanation) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "STEP-BY-STEP EXPLANATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.explanation,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text(
                        text = if (questionIndex + 1 < totalQuestions) "Next Question" else "Complete Level",
                        color = MidnightBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = MidnightBg)
                }
            }
        }
    }
}

@Composable
private fun QuizResultOverlay(
    result: BrainBoostViewModel.QuizResult,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (result.starsEarned >= 1) "Level Completed!" else "Keep Practicing!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                StarRatingRow(starsEarned = result.starsEarned, starSize = 34.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${result.correctCount} / ${result.totalQuestions} Correct",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+${result.xpEarned} XP Earned",
                    fontSize = 14.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.SemiBold
                )
                if (result.maxStreak > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Streak Multiplier: 🔥 ${result.maxStreak}",
                        fontSize = 13.sp,
                        color = StreakFire
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text(
                        text = if (result.starsEarned >= 1) "Next Milestone on Road" else "Return to Road",
                        color = MidnightBg,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
