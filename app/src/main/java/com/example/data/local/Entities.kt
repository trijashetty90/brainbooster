package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val displayName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: Long = 1,
    val xp: Int = 240,
    val level: Int = 3,
    val streak: Int = 5,
    val dailyGoalTarget: Int = 5,
    val dailyGoalCompleted: Int = 3,
    val totalStars: Int = 18,
    val speedMathBest: Int = 16,
    val memoryFlipBestSec: Int = 24,
    val wordUnscrambleBest: Int = 9,
    val numberMemoryBestDigits: Int = 7,
    val stroopBestScore: Int = 26,
    val totalQuestionsSolved: Int = 48,
    val lastActiveDate: String = "2026-10-05",
    val lastStreakClaimDate: String = "",
    val longestStreak: Int = 5,
    val totalStreakXpClaimed: Int = 350
)

@Entity(tableName = "aptitude_levels")
data class AptitudeLevelEntity(
    @PrimaryKey val levelNumber: Int,
    val title: String,
    val topic: String,
    val category: String,
    val starsEarned: Int,
    val isUnlocked: Boolean,
    val bestScore: Int,
    val syllabusReference: String = "IndiaBIX"
)

@Entity(tableName = "performance_logs")
data class PerformanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val activityName: String,
    val domain: String,
    val scorePercentage: Int,
    val xpEarned: Int,
    val dateLabel: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserAccountEntity): Long

    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    fun getUserProfile(userId: Long): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfileDirect(userId: Long): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET xp = xp + :xpGained, totalQuestionsSolved = totalQuestionsSolved + 1, dailyGoalCompleted = dailyGoalCompleted + 1 WHERE userId = :userId")
    suspend fun recordQuestionSolved(userId: Long, xpGained: Int)

    @Query("UPDATE user_profile SET streak = :newStreak WHERE userId = :userId")
    suspend fun updateStreak(userId: Long, newStreak: Int)

    @Query("UPDATE user_profile SET xp = xp + :xpBonus, streak = :newStreak, lastStreakClaimDate = :claimDate, totalStreakXpClaimed = totalStreakXpClaimed + :xpBonus, longestStreak = max(longestStreak, :newStreak) WHERE userId = :userId")
    suspend fun claimStreakReward(userId: Long, newStreak: Int, xpBonus: Int, claimDate: String)

    @Query("UPDATE user_profile SET speedMathBest = max(speedMathBest, :score) WHERE userId = :userId")
    suspend fun updateSpeedMathBest(userId: Long, score: Int)

    @Query("UPDATE user_profile SET memoryFlipBestSec = min(memoryFlipBestSec, :sec) WHERE userId = :userId")
    suspend fun updateMemoryFlipBest(userId: Long, sec: Int)

    @Query("UPDATE user_profile SET wordUnscrambleBest = max(wordUnscrambleBest, :score) WHERE userId = :userId")
    suspend fun updateWordUnscrambleBest(userId: Long, score: Int)

    @Query("UPDATE user_profile SET numberMemoryBestDigits = max(numberMemoryBestDigits, :digits) WHERE userId = :userId")
    suspend fun updateNumberMemoryBest(userId: Long, digits: Int)

    @Query("UPDATE user_profile SET stroopBestScore = max(stroopBestScore, :score) WHERE userId = :userId")
    suspend fun updateStroopBest(userId: Long, score: Int)
}

@Dao
interface AptitudeLevelDao {
    @Query("SELECT * FROM aptitude_levels ORDER BY levelNumber ASC")
    fun getAllLevels(): Flow<List<AptitudeLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(levels: List<AptitudeLevelEntity>)

    @Query("UPDATE aptitude_levels SET starsEarned = :stars, bestScore = :score WHERE levelNumber = :levelNumber")
    suspend fun updateLevelProgress(levelNumber: Int, stars: Int, score: Int)

    @Query("UPDATE aptitude_levels SET isUnlocked = 1 WHERE levelNumber = :levelNumber")
    suspend fun unlockLevel(levelNumber: Int)
}

@Dao
interface PerformanceLogDao {
    @Query("SELECT * FROM performance_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(userId: Long): Flow<List<PerformanceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PerformanceLogEntity)

    @Query("SELECT * FROM performance_logs WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getAllLogsForUser(userId: Long): List<PerformanceLogEntity>
}

@Database(
    entities = [
        UserAccountEntity::class,
        UserProfileEntity::class,
        AptitudeLevelEntity::class,
        PerformanceLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BrainBoostDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun aptitudeLevelDao(): AptitudeLevelDao
    abstract fun performanceLogDao(): PerformanceLogDao
}
