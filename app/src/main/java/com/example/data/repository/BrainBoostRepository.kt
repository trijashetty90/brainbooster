package com.example.data.repository

import com.example.data.local.AptitudeLevelDao
import com.example.data.local.AptitudeLevelEntity
import com.example.data.local.AsyncStorageSimulator
import com.example.data.local.PerformanceLogDao
import com.example.data.local.PerformanceLogEntity
import com.example.data.local.UserAccountDao
import com.example.data.local.UserAccountEntity
import com.example.data.local.UserProfileDao
import com.example.data.local.UserProfileEntity
import com.example.data.model.AptitudeLevel
import com.example.data.model.AptitudeQuestion
import com.example.data.model.AuthenticatedUser
import com.example.data.model.CognitiveAnalytics
import com.example.data.model.CognitiveInsight
import com.example.data.model.DayDomainPerformance
import com.example.data.model.DomainDayByDayTrend
import com.example.data.model.UserStats
import com.example.data.remote.GeminiAptitudeGenerator
import com.example.util.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BrainBoostRepository(
    private val userAccountDao: UserAccountDao,
    private val userProfileDao: UserProfileDao,
    private val aptitudeLevelDao: AptitudeLevelDao,
    private val performanceLogDao: PerformanceLogDao
) {

    // --- Authentication ---

    suspend fun signUp(email: String, password: String, displayName: String): Result<AuthenticatedUser> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val existing = userAccountDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }

        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password, salt)
        val name = displayName.ifBlank { trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }

        val newAccount = UserAccountEntity(
            email = trimmedEmail,
            passwordHash = hash,
            salt = salt,
            displayName = name
        )

        return try {
            val newId = userAccountDao.insertUser(newAccount)
            // Initialize their blank user profile
            val initialProfile = UserProfileEntity(
                userId = newId,
                xp = 100,
                level = 1,
                streak = 1,
                dailyGoalTarget = 5,
                dailyGoalCompleted = 0,
                totalStars = 0
            )
            userProfileDao.insertOrUpdate(initialProfile)

            // Seed introductory benchmark log
            performanceLogDao.insertLog(
                PerformanceLogEntity(
                    userId = newId,
                    activityName = "Account Initialized",
                    domain = "Mental Calculation",
                    scorePercentage = 80,
                    xpEarned = 100,
                    dateLabel = "Today"
                )
            )

            Result.success(AuthenticatedUser(newId, trimmedEmail, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthenticatedUser> {
        val trimmedEmail = email.trim().lowercase()
        val account = userAccountDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("No account found with this email."))

        val isMatch = SecurityUtils.verifyPassword(password, account.salt, account.passwordHash)
        if (!isMatch) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        return Result.success(AuthenticatedUser(account.id, account.email, account.displayName))
    }

    suspend fun getUserAccountById(userId: Long): AuthenticatedUser? {
        val entity = userAccountDao.getUserById(userId) ?: return null
        return AuthenticatedUser(entity.id, entity.email, entity.displayName)
    }

    // --- Profile & Progress ---

    val weekRewardsList = listOf(
        com.example.data.model.DailyStreakRewardItem(1, 50, "Day 1: Spark"),
        com.example.data.model.DailyStreakRewardItem(2, 75, "Day 2: Momentum"),
        com.example.data.model.DailyStreakRewardItem(3, 100, "Day 3: Focus"),
        com.example.data.model.DailyStreakRewardItem(4, 125, "Day 4: Dedication"),
        com.example.data.model.DailyStreakRewardItem(5, 150, "Day 5: Mastery"),
        com.example.data.model.DailyStreakRewardItem(6, 200, "Day 6: Champion"),
        com.example.data.model.DailyStreakRewardItem(7, 350, "Day 7: Apex Milestone", isGrandReward = true)
    )

    fun getDailyStreakState(userId: Long): Flow<com.example.data.model.DailyStreakState> {
        return userProfileDao.getUserProfile(userId).map { entity ->
            val todayStr = getTodayDateString()
            val streak = entity?.streak ?: 5
            val lastClaim = entity?.lastStreakClaimDate ?: ""
            val canClaim = lastClaim != todayStr
            val cycleDay = if (streak > 0) ((streak - 1) % 7) + 1 else 1
            val todayReward = weekRewardsList.getOrNull(cycleDay - 1)?.xpBonus ?: 150

            com.example.data.model.DailyStreakState(
                streakCount = streak,
                lastClaimDate = lastClaim,
                canClaimToday = canClaim,
                todayRewardXp = todayReward,
                currentDayIndex = cycleDay,
                streakMultiplier = 1.0f + (streak * 0.05f),
                totalBonusXpClaimed = entity?.totalStreakXpClaimed ?: 350,
                longestStreak = entity?.longestStreak ?: streak,
                weekRewards = weekRewardsList
            )
        }
    }

    suspend fun claimDailyStreakReward(userId: Long): Result<com.example.data.model.DailyStreakState> {
        val profile = userProfileDao.getProfileDirect(userId)
            ?: return Result.failure(IllegalStateException("User profile not found"))

        val todayStr = getTodayDateString()
        if (profile.lastStreakClaimDate == todayStr) {
            return Result.failure(IllegalStateException("Already claimed streak reward for today!"))
        }

        val yesterdayStr = getYesterdayDateString()
        val newStreak = if (profile.lastStreakClaimDate == yesterdayStr) {
            profile.streak + 1
        } else if (profile.lastStreakClaimDate.isBlank()) {
            profile.streak.coerceAtLeast(1)
        } else {
            profile.streak + 1
        }

        val cycleDay = ((newStreak - 1) % 7) + 1
        val xpBonus = weekRewardsList.getOrNull(cycleDay - 1)?.xpBonus ?: 150

        userProfileDao.claimStreakReward(userId, newStreak, xpBonus, todayStr)

        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = "Daily Login Streak (Day $cycleDay)",
                domain = "Daily Login Streak",
                scorePercentage = 100,
                xpEarned = xpBonus,
                dateLabel = "Today"
            )
        )

        val updatedState = com.example.data.model.DailyStreakState(
            streakCount = newStreak,
            lastClaimDate = todayStr,
            canClaimToday = false,
            todayRewardXp = xpBonus,
            currentDayIndex = cycleDay,
            streakMultiplier = 1.0f + (newStreak * 0.05f),
            totalBonusXpClaimed = profile.totalStreakXpClaimed + xpBonus,
            longestStreak = maxOf(profile.longestStreak, newStreak),
            weekRewards = weekRewardsList
        )

        val updatedStats = UserStats(
            xp = profile.xp + xpBonus,
            level = ((profile.xp + xpBonus) / 100) + 1,
            streak = newStreak,
            dailyGoalTarget = profile.dailyGoalTarget,
            dailyGoalCompleted = profile.dailyGoalCompleted,
            totalStars = profile.totalStars,
            lastStreakClaimDate = todayStr,
            longestStreak = maxOf(profile.longestStreak, newStreak),
            totalStreakXpClaimed = profile.totalStreakXpClaimed + xpBonus
        )

        AsyncStorageSimulator.updateMirror(
            stats = updatedStats,
            unlockedCount = defaultLevels.count { it.isUnlocked },
            totalStars = defaultLevels.sumOf { it.starsEarned },
            streakState = updatedState
        )

        return Result.success(updatedState)
    }

    private fun getTodayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    private fun getYesterdayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        return sdf.format(cal.time)
    }

    fun getUserStats(userId: Long): Flow<UserStats> = userProfileDao.getUserProfile(userId).map { entity ->
        if (entity == null) {
            UserStats()
        } else {
            UserStats(
                xp = entity.xp,
                level = entity.level,
                streak = entity.streak,
                dailyGoalTarget = entity.dailyGoalTarget,
                dailyGoalCompleted = entity.dailyGoalCompleted,
                totalStars = entity.totalStars,
                speedMathBest = entity.speedMathBest,
                memoryFlipBestSec = entity.memoryFlipBestSec,
                wordUnscrambleBest = entity.wordUnscrambleBest,
                numberMemoryBestDigits = entity.numberMemoryBestDigits,
                stroopBestScore = entity.stroopBestScore,
                totalQuestionsSolved = entity.totalQuestionsSolved,
                lastStreakClaimDate = entity.lastStreakClaimDate,
                longestStreak = entity.longestStreak,
                totalStreakXpClaimed = entity.totalStreakXpClaimed
            )
        }
    }

    val aptitudeLevels: Flow<List<AptitudeLevel>> = aptitudeLevelDao.getAllLevels().map { list ->
        if (list.isEmpty()) {
            defaultLevels
        } else {
            list.map {
                AptitudeLevel(
                    levelNumber = it.levelNumber,
                    title = it.title,
                    topic = it.topic,
                    category = it.category,
                    starsEarned = it.starsEarned,
                    isUnlocked = it.isUnlocked,
                    bestScore = it.bestScore,
                    syllabusReference = it.syllabusReference
                )
            }
        }
    }

    suspend fun initializeDefaultsIfEmpty(): AuthenticatedUser {
        // Pre-seed default user account if none exists
        val defaultEmail = "scholar@brainboost.edu"
        var account = userAccountDao.getUserByEmail(defaultEmail)
        if (account == null) {
            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPassword("brainboost123", salt)
            val newAcc = UserAccountEntity(
                email = defaultEmail,
                passwordHash = hash,
                salt = salt,
                displayName = "Alex Carter"
            )
            val insertedId = userAccountDao.insertUser(newAcc)
            account = newAcc.copy(id = insertedId)

            val initialProfile = UserProfileEntity(
                userId = insertedId,
                xp = 480,
                level = 4,
                streak = 6,
                dailyGoalTarget = 5,
                dailyGoalCompleted = 3,
                totalStars = 21,
                speedMathBest = 18,
                memoryFlipBestSec = 22,
                wordUnscrambleBest = 10,
                numberMemoryBestDigits = 8,
                stroopBestScore = 28,
                totalQuestionsSolved = 56
            )
            userProfileDao.insertOrUpdate(initialProfile)

            // Seed historical performance telemetry for Analytics charts
            val initialLogs = listOf(
                PerformanceLogEntity(userId = insertedId, activityName = "Speed Math", domain = "Mental Calculation", scorePercentage = 88, xpEarned = 140, dateLabel = "Mon"),
                PerformanceLogEntity(userId = insertedId, activityName = "Memory Card Flip", domain = "Visual Memory", scorePercentage = 92, xpEarned = 150, dateLabel = "Tue"),
                PerformanceLogEntity(userId = insertedId, activityName = "Word Unscramble", domain = "Verbal Reasoning", scorePercentage = 75, xpEarned = 110, dateLabel = "Wed"),
                PerformanceLogEntity(userId = insertedId, activityName = "Number Flash", domain = "Working Memory", scorePercentage = 85, xpEarned = 160, dateLabel = "Thu"),
                PerformanceLogEntity(userId = insertedId, activityName = "Stroop Test", domain = "Executive Inhibition", scorePercentage = 96, xpEarned = 180, dateLabel = "Fri"),
                PerformanceLogEntity(userId = insertedId, activityName = "Time & Work", domain = "Quantitative", scorePercentage = 80, xpEarned = 130, dateLabel = "Sat"),
                PerformanceLogEntity(userId = insertedId, activityName = "Syllogisms", domain = "Logical", scorePercentage = 85, xpEarned = 140, dateLabel = "Sun")
            )
            initialLogs.forEach { performanceLogDao.insertLog(it) }
        }

        // Initialize levels if empty
        val entities = defaultLevels.map {
            AptitudeLevelEntity(
                levelNumber = it.levelNumber,
                title = it.title,
                topic = it.topic,
                category = it.category,
                starsEarned = it.starsEarned,
                isUnlocked = it.isUnlocked,
                bestScore = it.bestScore,
                syllabusReference = it.syllabusReference
            )
        }
        aptitudeLevelDao.insertAll(entities)

        AsyncStorageSimulator.updateMirror(
            UserStats(
                xp = 480,
                level = 4,
                streak = 6,
                dailyGoalTarget = 5,
                dailyGoalCompleted = 3,
                totalStars = defaultLevels.sumOf { it.starsEarned }
            ),
            unlockedCount = defaultLevels.count { it.isUnlocked },
            totalStars = defaultLevels.sumOf { it.starsEarned }
        )

        return AuthenticatedUser(account.id, account.email, account.displayName)
    }

    suspend fun recordGameScore(
        userId: Long,
        gameId: String,
        score: Int,
        xpGained: Int,
        streakBonus: Boolean
    ) {
        userProfileDao.recordQuestionSolved(userId, xpGained)

        val (domain, label) = when (gameId) {
            "speed_math" -> Pair("Mental Calculation", "Speed Math")
            "memory_flip" -> Pair("Visual Memory", "Memory Card Flip")
            "word_unscramble" -> Pair("Verbal Reasoning", "Word Unscramble")
            "number_memory" -> Pair("Working Memory", "Number Memory Flash")
            "stroop_challenge" -> Pair("Executive Inhibition", "Stroop Challenge")
            else -> Pair("Aptitude", "Aptitude Road")
        }

        val scorePct = (score * 10).coerceIn(40, 100)
        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = label,
                domain = domain,
                scorePercentage = scorePct,
                xpEarned = xpGained,
                dateLabel = "Today"
            )
        )
    }

    suspend fun completeLevel(userId: Long, levelNumber: Int, starsEarned: Int, score: Int) {
        aptitudeLevelDao.updateLevelProgress(levelNumber, starsEarned, score)
        if (starsEarned >= 1 && levelNumber < 12) {
            aptitudeLevelDao.unlockLevel(levelNumber + 1)
        }

        val level = defaultLevels.find { it.levelNumber == levelNumber }
        val domain = if (level?.category == "Logical Reasoning") "Logical" else "Quantitative"

        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = "Level $levelNumber: ${level?.topic ?: "Aptitude"}",
                domain = domain,
                scorePercentage = score.coerceIn(0, 100),
                xpEarned = starsEarned * 50,
                dateLabel = "Today"
            )
        )
    }

    // --- Analytics Aggregation ---

    suspend fun updateDomainDayScore(
        userId: Long,
        domain: String,
        dateLabel: String,
        score: Int,
        activityName: String = "$domain Practice"
    ) {
        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = activityName,
                domain = domain,
                scorePercentage = score.coerceIn(0, 100),
                xpEarned = (score * 1.5).toInt(),
                dateLabel = dateLabel,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun setPresetDomainComparison(
        userId: Long,
        domain: String,
        todayScore: Int,
        tomorrowScore: Int,
        yesterdayScore: Int = 74
    ) {
        val now = System.currentTimeMillis()
        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = "$domain Yesterday",
                domain = domain,
                scorePercentage = yesterdayScore.coerceIn(0, 100),
                xpEarned = 100,
                dateLabel = "Yesterday",
                timestamp = now - 86400000L
            )
        )
        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = "$domain Today",
                domain = domain,
                scorePercentage = todayScore.coerceIn(0, 100),
                xpEarned = 150,
                dateLabel = "Today",
                timestamp = now
            )
        )
        performanceLogDao.insertLog(
            PerformanceLogEntity(
                userId = userId,
                activityName = "$domain Tomorrow",
                domain = domain,
                scorePercentage = tomorrowScore.coerceIn(0, 100),
                xpEarned = 120,
                dateLabel = "Tomorrow",
                timestamp = now + 86400000L
            )
        )
    }

    private data class DomainConfig(
        val domain: String,
        val subtitle: String,
        val defaultToday: Int,
        val defaultTomorrow: Int
    )

    private fun calculateDayByDayTrends(
        logs: List<PerformanceLogEntity>
    ): List<DomainDayByDayTrend> {
        val domainConfigs = listOf(
            DomainConfig("Mental Calculation", "Speed Math arithmetic & response latency", 88, 70),
            DomainConfig("Visual Memory", "Spatial grid recall & card matching", 92, 85),
            DomainConfig("Verbal Reasoning", "Anagram decoding & cognitive vocabulary", 80, 75),
            DomainConfig("Working Memory", "Digit span retention & interference resistance", 86, 78),
            DomainConfig("Executive Inhibition", "Incongruent color conflict filtering", 96, 88),
            DomainConfig("Quantitative", "IndiaBIX quantitative problem speed", 85, 80),
            DomainConfig("Logical", "Deductive reasoning & syllogisms", 90, 82)
        )

        return domainConfigs.map { config ->
            val domain = config.domain
            val domainLogs = logs.filter { it.domain == domain }

            val todayLog = domainLogs.filter { it.dateLabel == "Today" }.maxByOrNull { it.timestamp }
            val yesterdayLog = domainLogs.filter { it.dateLabel == "Yesterday" }.maxByOrNull { it.timestamp }
            val tomorrowLog = domainLogs.filter { it.dateLabel == "Tomorrow" }.maxByOrNull { it.timestamp }

            val yesterdayScore = yesterdayLog?.scorePercentage ?: 74
            val todayScore = todayLog?.scorePercentage ?: config.defaultToday
            val tomorrowScore = tomorrowLog?.scorePercentage ?: config.defaultTomorrow

            val deltaVsYesterday = todayScore - yesterdayScore
            val deltaVsTomorrow = todayScore - tomorrowScore
            val isHigherTodayThanYesterday = todayScore > yesterdayScore
            val isHigherTodayThanTomorrow = todayScore > tomorrowScore

            val comparisonHeadline = when {
                isHigherTodayThanTomorrow && isHigherTodayThanYesterday ->
                    "Today's $domain ($todayScore%) is HIGHER than Tomorrow ($tomorrowScore%) by +${deltaVsTomorrow}% and Yesterday ($yesterdayScore%) by +${deltaVsYesterday}%"
                isHigherTodayThanTomorrow && !isHigherTodayThanYesterday ->
                    "Today's $domain ($todayScore%) is HIGHER than Tomorrow ($tomorrowScore%) by +${deltaVsTomorrow}%, but below Yesterday ($yesterdayScore%)"
                !isHigherTodayThanTomorrow && isHigherTodayThanYesterday ->
                    "Today's $domain ($todayScore%) is LESS than Tomorrow ($tomorrowScore%) by ${-deltaVsTomorrow}%, but above Yesterday ($yesterdayScore%)"
                else ->
                    "Today's $domain ($todayScore%) is LESS than Tomorrow ($tomorrowScore%) by ${-deltaVsTomorrow}% and Yesterday ($yesterdayScore%)"
            }

            val explanation = when {
                isHigherTodayThanTomorrow ->
                    "Day-by-Day Analysis: Today's $domain ($todayScore%) significantly outperformed the projected/subsequent tomorrow session ($tomorrowScore%), showing a -${deltaVsTomorrow}% decrease tomorrow. This pattern reflects peak sprint performance today followed by cognitive recalibration. To prevent tomorrow's drop, schedule a 3-minute warm-up."
                else ->
                    "Day-by-Day Analysis: Today's $domain is $todayScore%, with tomorrow trending higher at $tomorrowScore% (+${-deltaVsTomorrow}% gain). Continued practice and spaced repetition are driving upward cognitive trajectory."
            }

            val dailyScores = listOf(
                DayDomainPerformance("Mon", (yesterdayScore - 6).coerceIn(40, 98), 0, "Initial weekly baseline"),
                DayDomainPerformance("Tue", (yesterdayScore - 2).coerceIn(40, 98), +4, "Speed and accuracy drill"),
                DayDomainPerformance("Wed", (yesterdayScore + 4).coerceIn(40, 98), +6, "Mid-week cognitive sprint"),
                DayDomainPerformance("Thu", (yesterdayScore + 1).coerceIn(40, 98), -3, "Slight latency variance"),
                DayDomainPerformance("Fri", (yesterdayScore + 6).coerceIn(40, 98), +5, "Consistent answer streaks"),
                DayDomainPerformance("Yesterday", yesterdayScore, yesterdayScore - (yesterdayScore + 6), "Pre-session benchmark"),
                DayDomainPerformance("Today", todayScore, deltaVsYesterday, if (isHigherTodayThanTomorrow) "Peak session ($todayScore%): Higher than tomorrow" else "Session score: $todayScore%", isToday = true),
                DayDomainPerformance("Tomorrow", tomorrowScore, -deltaVsTomorrow, if (isHigherTodayThanTomorrow) "Projected dip: $tomorrowScore% (Less than today)" else "Projected gain: $tomorrowScore% (More than today)", isTomorrow = true)
            )

            DomainDayByDayTrend(
                domain = domain,
                todayScore = todayScore,
                yesterdayScore = yesterdayScore,
                tomorrowScore = tomorrowScore,
                deltaVsYesterday = deltaVsYesterday,
                deltaVsTomorrow = deltaVsTomorrow,
                isHigherTodayThanYesterday = isHigherTodayThanYesterday,
                isHigherTodayThanTomorrow = isHigherTodayThanTomorrow,
                comparisonHeadline = comparisonHeadline,
                explanation = explanation,
                dailyScores = dailyScores
            )
        }
    }

    fun getCognitiveAnalytics(userId: Long): Flow<CognitiveAnalytics> {
        return performanceLogDao.getRecentLogs(userId).map { logs ->
            // Domain averages
            val domainMap = mutableMapOf(
                "Mental Calculation" to 82,
                "Visual Memory" to 88,
                "Verbal Reasoning" to 76,
                "Working Memory" to 84,
                "Executive Inhibition" to 94
            )

            // Override with actual logs if present
            val groupedByDomain = logs.groupBy { it.domain }
            groupedByDomain.forEach { (domain, domainLogs) ->
                if (domainMap.containsKey(domain)) {
                    domainMap[domain] = domainLogs.map { it.scorePercentage }.average().toInt()
                }
            }

            // Quantitative vs Logical Aptitude Accuracy
            val quantLogs = logs.filter { it.domain == "Quantitative" }
            val logicalLogs = logs.filter { it.domain == "Logical" }

            val quantAcc = if (quantLogs.isNotEmpty()) quantLogs.map { it.scorePercentage }.average().toInt() else 83
            val logicalAcc = if (logicalLogs.isNotEmpty()) logicalLogs.map { it.scorePercentage }.average().toInt() else 89

            // Weekly XP Trend
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val defaultXp = listOf(110, 140, 95, 160, 175, 130, 190)
            val trend = days.mapIndexed { idx, day ->
                val dayLogs = logs.filter { it.dateLabel == day }
                val dayXp = if (dayLogs.isNotEmpty()) dayLogs.sumOf { it.xpEarned } else defaultXp[idx]
                Pair(day, dayXp)
            }

            // Composite Agility Index (out of 1000)
            val avgDomainScore = domainMap.values.average()
            val compositeIndex = ((avgDomainScore * 0.6 + ((quantAcc + logicalAcc) / 2.0) * 0.4) * 10).toInt().coerceIn(300, 1000)

            // Insights
            val sortedDomains = domainMap.entries.sortedByDescending { it.value }
            val strengths = sortedDomains.take(2).map { entry ->
                CognitiveInsight(
                    domain = entry.key,
                    scoreBadge = "${entry.value}% Mastery",
                    title = "Peak Cognitive Strength: ${entry.key}",
                    description = "You demonstrate exceptional speed and accuracy in suppressing distractions and processing rapid signals.",
                    recommendation = "Maintain your competitive advantage by tackling advanced level tests."
                )
            }

            val weaknesses = sortedDomains.takeLast(2).map { entry ->
                CognitiveInsight(
                    domain = entry.key,
                    scoreBadge = "${entry.value}% Accuracy",
                    title = "Growth Opportunity: ${entry.key}",
                    description = "Response latency is slightly elevated in this domain compared to your other skills.",
                    recommendation = "Dedicate 5 minutes daily to ${entry.key} warm-ups before campus aptitude rounds."
                )
            }

            val dayTrends = calculateDayByDayTrends(logs)

            CognitiveAnalytics(
                domainScores = domainMap,
                dailyXpTrends = trend,
                quantitativeAccuracy = quantAcc,
                logicalAccuracy = logicalAcc,
                compositeAgilityIndex = compositeIndex,
                topStrengths = strengths,
                areasForImprovement = weaknesses,
                totalSessionsLogged = logs.size.coerceAtLeast(14),
                dayByDayDomainTrends = dayTrends
            )
        }
    }

    suspend fun getQuestionsForLevel(levelNumber: Int): List<AptitudeQuestion> {
        val level = defaultLevels.find { it.levelNumber == levelNumber }
        val topic = level?.topic ?: "Time and Work"
        val category = level?.category ?: "Quantitative Aptitude"

        val aiQuestion = GeminiAptitudeGenerator.generateIndiaBixQuestion(topic, category)
        val bankQuestions = curriculumQuestionBank[levelNumber] ?: emptyList()
        val combined = mutableListOf<AptitudeQuestion>()
        if (aiQuestion != null) {
            combined.add(aiQuestion)
        }
        combined.addAll(bankQuestions)
        return if (combined.isNotEmpty()) combined.take(3) else fallbackQuestion(topic)
    }

    companion object {
        val defaultLevels = listOf(
            AptitudeLevel(1, "Level 1: Efficiency Spark", "Time & Work", "Quantitative", starsEarned = 3, isUnlocked = true, bestScore = 95),
            AptitudeLevel(2, "Level 2: Market Margin", "Profit & Loss", "Quantitative", starsEarned = 2, isUnlocked = true, bestScore = 80),
            AptitudeLevel(3, "Level 3: Percentile Peak", "Percentages", "Quantitative", starsEarned = 2, isUnlocked = true, bestScore = 85),
            AptitudeLevel(4, "Level 4: Ratio Junction", "Ratio & Proportion", "Quantitative", starsEarned = 1, isUnlocked = true, bestScore = 70),
            AptitudeLevel(5, "Level 5: Locomotive Speed", "Trains & Speed", "Quantitative", starsEarned = 0, isUnlocked = true, bestScore = 0),
            AptitudeLevel(6, "Level 6: Compound Orbit", "Compound Interest", "Quantitative", starsEarned = 0, isUnlocked = false, bestScore = 0),
            AptitudeLevel(7, "Level 7: Logic Sequence", "Number Series", "Logical Reasoning", starsEarned = 0, isUnlocked = false, bestScore = 0),
            AptitudeLevel(8, "Level 8: Syllogism Gate", "Syllogisms", "Logical Reasoning", starsEarned = 0, isUnlocked = false, bestScore = 0),
            AptitudeLevel(9, "Level 9: Kinship Matrix", "Blood Relations", "Logical Reasoning", starsEarned = 0, isUnlocked = false, bestScore = 0),
            AptitudeLevel(10, "Level 10: Cryptic Cipher", "Coding-Decoding", "Logical Reasoning", starsEarned = 0, isUnlocked = false, bestScore = 0),
            AptitudeLevel(11, "Level 11: Compass Bearing", "Direction Sense", "Logical Reasoning", starsEarned = 0, isUnlocked = false, bestScore = 0),
            AptitudeLevel(12, "Level 12: Apex Synthesis", "Analytical Deductions", "Logical Reasoning", starsEarned = 0, isUnlocked = false, bestScore = 0)
        )

        private fun fallbackQuestion(topic: String): List<AptitudeQuestion> {
            return listOf(
                AptitudeQuestion(
                    id = "fb_1",
                    topic = topic,
                    question = "A can complete a piece of work in 12 days and B in 24 days. Working together, in how many days will they finish the work?",
                    options = listOf("6 days", "8 days", "10 days", "16 days"),
                    correctIndex = 1,
                    explanation = "A's 1 day work = 1/12. B's 1 day work = 1/24. (A + B)'s 1 day work = 1/12 + 1/24 = 3/24 = 1/8. Hence, together they finish in 8 days."
                )
            )
        }

        val curriculumQuestionBank: Map<Int, List<AptitudeQuestion>> = mapOf(
            1 to listOf(
                AptitudeQuestion(
                    id = "tw_1",
                    topic = "Time & Work",
                    question = "A can do a work in 15 days and B in 20 days. If they work on it together for 4 days, then the fraction of the work that is left is:",
                    options = listOf("7/15", "8/15", "1/4", "11/15"),
                    correctIndex = 1,
                    explanation = "A's 1 day work = 1/15, B's 1 day work = 1/20. Together in 1 day = (1/15 + 1/20) = 7/60. In 4 days, work done = 4 * (7/60) = 7/15. Fraction of work remaining = 1 - 7/15 = 8/15."
                ),
                AptitudeQuestion(
                    id = "tw_2",
                    topic = "Time & Work",
                    question = "A alone can do a piece of work in 6 days and B alone in 8 days. A and B undertook to do it for Rs. 3200. With the help of C, they finished the work in 3 days. How much is to be paid to C?",
                    options = listOf("Rs. 375", "Rs. 400", "Rs. 600", "Rs. 800"),
                    correctIndex = 1,
                    explanation = "C's 1 day work = 1/3 - (1/6 + 1/8) = 1/3 - 7/24 = 1/24. Ratio of work = 1/6 : 1/8 : 1/24 = 4 : 3 : 1. C's share = (1/8) * 3200 = Rs. 400."
                )
            ),
            2 to listOf(
                AptitudeQuestion(
                    id = "pl_1",
                    topic = "Profit & Loss",
                    question = "A vendor bought toffees at 6 for a rupee. How many for a rupee must he sell to gain 20%?",
                    options = listOf("3", "4", "5", "6"),
                    correctIndex = 2,
                    explanation = "Cost Price of 6 toffees = Re 1. SP of 6 toffees = 120% of Re 1 = 6/5. For Rs 6/5, toffees sold = 6. For Re 1, toffees sold = 6 * (5/6) = 5 toffees."
                ),
                AptitudeQuestion(
                    id = "pl_2",
                    topic = "Profit & Loss",
                    question = "If selling price is doubled, the profit triples. Find the profit percentage.",
                    options = listOf("66.66%", "100%", "105.33%", "120%"),
                    correctIndex = 1,
                    explanation = "Let CP = x and SP = y. Profit = y - x. Given: 2y - x = 3(y - x) => 2y - x = 3y - 3x => y = 2x. Profit = 2x - x = x. Profit percentage = (x/x) * 100 = 100%."
                )
            ),
            3 to listOf(
                AptitudeQuestion(
                    id = "pct_1",
                    topic = "Percentages",
                    question = "Two students appeared at an examination. One secured 9 marks more than the other and his marks was 56% of the sum of their marks. The marks obtained by them are:",
                    options = listOf("39, 30", "41, 32", "42, 33", "43, 34"),
                    correctIndex = 2,
                    explanation = "Let marks be x and x + 9. x + 9 = 56/100 * (x + x + 9) = 14/25 * (2x + 9). 25x + 225 = 28x + 126 => 3x = 99 => x = 33. Marks are 42 and 33."
                )
            ),
            7 to listOf(
                AptitudeQuestion(
                    id = "lr_1",
                    topic = "Number Series",
                    question = "Look at this series: 2, 1, (1/2), (1/4), ... What number should come next?",
                    options = listOf("(1/3)", "(1/8)", "(2/8)", "(1/16)"),
                    correctIndex = 1,
                    explanation = "This is a simple division series; each number is one-half of the previous number. (1/4) / 2 = 1/8."
                )
            ),
            8 to listOf(
                AptitudeQuestion(
                    id = "syl_1",
                    topic = "Syllogisms",
                    question = "Statements: All the actors are girls. All the girls are beautiful.\nConclusions:\n1. All the actors are beautiful.\n2. Some girls are actors.",
                    options = listOf("Only (1) follows", "Only (2) follows", "Either (1) or (2) follows", "Both (1) and (2) follow"),
                    correctIndex = 3,
                    explanation = "Actor is a subset of Girls, and Girls is a subset of Beautiful. Therefore, All actors are beautiful (1 follows) and Some girls are actors (2 follows)."
                )
            )
        )
    }
}
