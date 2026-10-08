package com.example.data.local

import com.example.data.model.DailyStreakState
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Educational AsyncStorage Simulator:
 * Provides an exact inspection mirror of React Native's AsyncStorage key-value pairs
 * as specified in the BrainBoost College Project Technical Architecture:
 * - Key `@brainboost_user_stats` -> JSON of XP, Level, Daily Goal Progress
 * - Key `@brainboost_daily_streak` -> JSON streak count, last timestamp, multiplier, claimed status
 * - Key `@brainboost_level_progress` -> JSON mapping of levels, unlock status, star ratings
 * - Key `@brainboost_mini_games` -> High scores for the 5 cognitive modules
 */
object AsyncStorageSimulator {
    private val _storageDump = MutableStateFlow<Map<String, String>>(emptyMap())
    val storageDump: StateFlow<Map<String, String>> = _storageDump.asStateFlow()

    fun updateMirror(
        stats: UserStats,
        unlockedCount: Int,
        totalStars: Int,
        streakState: DailyStreakState? = null
    ) {
        val statsJson = """
            {
              "xp": ${stats.xp},
              "level": ${stats.level},
              "dailyGoalTarget": ${stats.dailyGoalTarget},
              "dailyGoalCompleted": ${stats.dailyGoalCompleted},
              "totalQuestionsSolved": ${stats.totalQuestionsSolved},
              "totalStars": $totalStars
            }
        """.trimIndent()

        val streak = streakState?.streakCount ?: stats.streak
        val claimed = !(streakState?.canClaimToday ?: false)
        val todayXp = streakState?.todayRewardXp ?: 150
        val longest = streakState?.longestStreak ?: stats.longestStreak
        val totalClaimed = streakState?.totalBonusXpClaimed ?: stats.totalStreakXpClaimed
        val multiplier = 1.0 + (streak * 0.05)

        val streakJson = """
            {
              "streakCount": $streak,
              "lastClaimDate": "${stats.lastStreakClaimDate.ifBlank { "2026-10-04" }}",
              "claimedToday": $claimed,
              "streakMultiplier": ${"%.2f".format(multiplier)},
              "todayRewardXp": $todayXp,
              "longestStreak": $longest,
              "totalStreakBonusXp": $totalClaimed,
              "activeCycleDay": ${((streak - 1) % 7) + 1}
            }
        """.trimIndent()

        val progressJson = """
            {
              "highestUnlockedLevel": $unlockedCount,
              "totalStarsEarned": $totalStars,
              "sectorsCompleted": ["Sector 1 (Active)", "Sector 2 (${unlockedCount}/12 Unlocked)"]
            }
        """.trimIndent()

        val miniGamesJson = """
            {
              "speedMathBest": ${stats.speedMathBest},
              "memoryFlipBestSec": ${stats.memoryFlipBestSec},
              "wordUnscrambleBest": ${stats.wordUnscrambleBest},
              "numberMemoryBestDigits": ${stats.numberMemoryBestDigits},
              "stroopBestScore": ${stats.stroopBestScore}
            }
        """.trimIndent()

        _storageDump.value = mapOf(
            "@brainboost_daily_streak" to streakJson,
            "@brainboost_user_stats" to statsJson,
            "@brainboost_level_progress" to progressJson,
            "@brainboost_mini_games" to miniGamesJson
        )
    }
}
