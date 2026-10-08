package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserStats(
    val xp: Int = 120,
    val level: Int = 2,
    val streak: Int = 4,
    val dailyGoalTarget: Int = 5,
    val dailyGoalCompleted: Int = 3,
    val totalStars: Int = 18,
    val speedMathBest: Int = 14,
    val memoryFlipBestSec: Int = 28,
    val wordUnscrambleBest: Int = 8,
    val numberMemoryBestDigits: Int = 6,
    val stroopBestScore: Int = 22,
    val totalQuestionsSolved: Int = 42,
    val lastActiveDate: String = "Today",
    val lastStreakClaimDate: String = "",
    val longestStreak: Int = 5,
    val totalStreakXpClaimed: Int = 350
)

@JsonClass(generateAdapter = true)
data class AptitudeQuestion(
    val id: String,
    val topic: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val difficulty: String = "Medium"
)

@JsonClass(generateAdapter = true)
data class AptitudeLevel(
    val levelNumber: Int,
    val title: String,
    val topic: String,
    val category: String, // Quantitative or Logical
    val starsEarned: Int = 0,
    val isUnlocked: Boolean = false,
    val bestScore: Int = 0,
    val syllabusReference: String = "IndiaBIX"
)

data class MiniGameInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val cognitiveDomain: String,
    val description: String,
    val iconName: String,
    val personalBest: String,
    val colorHex: Long
)

data class AuthenticatedUser(
    val id: Long,
    val email: String,
    val displayName: String
)

data class DayDomainPerformance(
    val day: String, // e.g. "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Yesterday", "Today", "Tomorrow"
    val score: Int,  // 0 - 100
    val deltaFromPrevDay: Int, // e.g. +14, -8
    val note: String,
    val isToday: Boolean = false,
    val isTomorrow: Boolean = false
)

data class DomainDayByDayTrend(
    val domain: String,
    val todayScore: Int,
    val yesterdayScore: Int,
    val tomorrowScore: Int,
    val deltaVsYesterday: Int, // todayScore - yesterdayScore
    val deltaVsTomorrow: Int,  // todayScore - tomorrowScore (positive means today is higher)
    val isHigherTodayThanYesterday: Boolean,
    val isHigherTodayThanTomorrow: Boolean, // true when today's score is more than tomorrow's
    val comparisonHeadline: String,
    val explanation: String,
    val dailyScores: List<DayDomainPerformance>
)

data class CognitiveAnalytics(
    val domainScores: Map<String, Int>, // Domain Name -> Score Percentage (0-100)
    val dailyXpTrends: List<Pair<String, Int>>, // e.g. "Mon" to 120, "Tue" to 180
    val quantitativeAccuracy: Int,
    val logicalAccuracy: Int,
    val compositeAgilityIndex: Int, // 0 - 1000
    val topStrengths: List<CognitiveInsight>,
    val areasForImprovement: List<CognitiveInsight>,
    val totalSessionsLogged: Int,
    val dayByDayDomainTrends: List<DomainDayByDayTrend> = emptyList()
)

data class CognitiveInsight(
    val domain: String,
    val scoreBadge: String,
    val title: String,
    val description: String,
    val recommendation: String
)

data class DailyStreakRewardItem(
    val dayNumber: Int,
    val xpBonus: Int,
    val title: String,
    val isGrandReward: Boolean = false
)

data class DailyStreakState(
    val streakCount: Int = 5,
    val lastClaimDate: String = "",
    val canClaimToday: Boolean = true,
    val todayRewardXp: Int = 150,
    val currentDayIndex: Int = 5, // 1 to 7
    val streakMultiplier: Float = 1.25f,
    val totalBonusXpClaimed: Int = 675,
    val longestStreak: Int = 14,
    val weekRewards: List<DailyStreakRewardItem> = listOf(
        DailyStreakRewardItem(1, 50, "Day 1 Starter"),
        DailyStreakRewardItem(2, 75, "Day 2 Momentum"),
        DailyStreakRewardItem(3, 100, "Day 3 Focus"),
        DailyStreakRewardItem(4, 125, "Day 4 Dedication"),
        DailyStreakRewardItem(5, 150, "Day 5 Mastery"),
        DailyStreakRewardItem(6, 200, "Day 6 Champion"),
        DailyStreakRewardItem(7, 350, "Day 7 Grand Apex", isGrandReward = true)
    )
)
