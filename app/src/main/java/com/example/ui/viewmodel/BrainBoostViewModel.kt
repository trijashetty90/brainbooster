package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.audio.SoundEffect
import com.example.audio.SoundManager
import com.example.data.local.AsyncStorageSimulator
import com.example.data.local.BrainBoostDatabase
import com.example.data.model.AptitudeLevel
import com.example.data.model.AptitudeQuestion
import com.example.data.model.AuthenticatedUser
import com.example.data.model.CognitiveAnalytics
import com.example.data.model.UserStats
import com.example.data.repository.BrainBoostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BrainBoostViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        BrainBoostDatabase::class.java,
        "brainboost_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = BrainBoostRepository(
        userAccountDao = db.userAccountDao(),
        userProfileDao = db.userProfileDao(),
        aptitudeLevelDao = db.aptitudeLevelDao(),
        performanceLogDao = db.performanceLogDao()
    )

    // Audio State
    val isSfxEnabled: StateFlow<Boolean> = SoundManager.isSfxEnabled
    val isMusicEnabled: StateFlow<Boolean> = SoundManager.isMusicEnabled

    // Auth State
    private val _currentUser = MutableStateFlow<AuthenticatedUser?>(null)
    val currentUser: StateFlow<AuthenticatedUser?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Dynamic User Stats bound to the current logged-in user ID
    val userStats: StateFlow<UserStats> = _currentUser.flatMapLatest { user ->
        val uid = user?.id ?: 1L
        repository.getUserStats(uid)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserStats()
    )

    // Cognitive Analytics bound to current user
    val cognitiveAnalytics: StateFlow<CognitiveAnalytics?> = _currentUser.flatMapLatest { user ->
        val uid = user?.id ?: 1L
        repository.getCognitiveAnalytics(uid)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Selected Domain for Day-by-Day Analytics
    private val _selectedAnalyticsDomain = MutableStateFlow("Mental Calculation")
    val selectedAnalyticsDomain: StateFlow<String> = _selectedAnalyticsDomain.asStateFlow()

    fun selectAnalyticsDomain(domain: String) {
        _selectedAnalyticsDomain.value = domain
    }

    fun updateDomainDayScore(domain: String, dateLabel: String, score: Int) {
        val uid = _currentUser.value?.id ?: 1L
        viewModelScope.launch {
            repository.updateDomainDayScore(uid, domain, dateLabel, score)
            playSound(SoundEffect.UI_INTERACTION)
        }
    }

    fun setDayComparisonPreset(domain: String, todayScore: Int, tomorrowScore: Int, yesterdayScore: Int = 74) {
        val uid = _currentUser.value?.id ?: 1L
        viewModelScope.launch {
            repository.setPresetDomainComparison(uid, domain, todayScore, tomorrowScore, yesterdayScore)
            playSound(SoundEffect.LEVEL_UNLOCK)
        }
    }

    // Daily Login Streak State bound to current user
    val dailyStreakState: StateFlow<com.example.data.model.DailyStreakState> = _currentUser.flatMapLatest { user ->
        val uid = user?.id ?: 1L
        repository.getDailyStreakState(uid)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.example.data.model.DailyStreakState()
    )

    fun claimDailyStreakBonus() {
        val uid = _currentUser.value?.id ?: 1L
        viewModelScope.launch {
            val result = repository.claimDailyStreakReward(uid)
            result.onSuccess {
                playSound(SoundEffect.LEVEL_UNLOCK)
            }
        }
    }

    val aptitudeLevels: StateFlow<List<AptitudeLevel>> = repository.aptitudeLevels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BrainBoostRepository.defaultLevels
    )

    val storageDump: StateFlow<Map<String, String>> = AsyncStorageSimulator.storageDump

    // Active Quiz State for Sector 2
    private val _activeLevelNumber = MutableStateFlow<Int?>(null)
    val activeLevelNumber: StateFlow<Int?> = _activeLevelNumber.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<AptitudeQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<AptitudeQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _showExplanation = MutableStateFlow(false)
    val showExplanation: StateFlow<Boolean> = _showExplanation.asStateFlow()

    private val _quizCorrectCount = MutableStateFlow(0)
    val quizCorrectCount: StateFlow<Int> = _quizCorrectCount.asStateFlow()

    private val _quizStreak = MutableStateFlow(0)
    val quizStreak: StateFlow<Int> = _quizStreak.asStateFlow()

    private val _quizTimeRemaining = MutableStateFlow(30)
    val quizTimeRemaining: StateFlow<Int> = _quizTimeRemaining.asStateFlow()

    private val _isQuizLoading = MutableStateFlow(false)
    val isQuizLoading: StateFlow<Boolean> = _isQuizLoading.asStateFlow()

    private val _quizFinishedResult = MutableStateFlow<QuizResult?>(null)
    val quizFinishedResult: StateFlow<QuizResult?> = _quizFinishedResult.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val defaultUser = repository.initializeDefaultsIfEmpty()
            _currentUser.value = defaultUser
        }
    }

    // --- Audio Controls ---

    fun toggleSfx() {
        SoundManager.toggleSfx()
    }

    fun toggleMusic() {
        SoundManager.toggleMusic()
    }

    fun playSound(effect: SoundEffect) {
        SoundManager.playSound(effect)
    }

    // --- Auth Functions ---

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        _isAuthLoading.value = true
        _authError.value = null
        viewModelScope.launch {
            val result = repository.login(email, pass)
            _isAuthLoading.value = false
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    playSound(SoundEffect.LEVEL_UNLOCK)
                    onSuccess()
                },
                onFailure = { error ->
                    _authError.value = error.message ?: "Authentication failed."
                    playSound(SoundEffect.INCORRECT_ANSWER)
                }
            )
        }
    }

    fun signUp(email: String, pass: String, name: String, onSuccess: () -> Unit) {
        _isAuthLoading.value = true
        _authError.value = null
        viewModelScope.launch {
            val result = repository.signUp(email, pass, name)
            _isAuthLoading.value = false
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    playSound(SoundEffect.GAME_COMPLETION)
                    onSuccess()
                },
                onFailure = { error ->
                    _authError.value = error.message ?: "Sign up failed."
                    playSound(SoundEffect.INCORRECT_ANSWER)
                }
            )
        }
    }

    fun logout() {
        _currentUser.value = null
        playSound(SoundEffect.UI_INTERACTION)
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // --- Level Quiz Logic ---

    fun startLevelQuiz(levelNumber: Int) {
        playSound(SoundEffect.UI_INTERACTION)
        _activeLevelNumber.value = levelNumber
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _showExplanation.value = false
        _quizCorrectCount.value = 0
        _quizStreak.value = 0
        _quizFinishedResult.value = null
        _isQuizLoading.value = true

        viewModelScope.launch {
            val questions = repository.getQuestionsForLevel(levelNumber)
            _quizQuestions.value = questions
            _isQuizLoading.value = false
            startQuestionTimer()
        }
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        _quizTimeRemaining.value = 30
        timerJob = viewModelScope.launch {
            while (_quizTimeRemaining.value > 0 && !_showExplanation.value) {
                delay(1000)
                _quizTimeRemaining.value -= 1
            }
            if (_quizTimeRemaining.value == 0 && !_showExplanation.value) {
                onSelectOption(-1)
            }
        }
    }

    fun onSelectOption(optionIndex: Int) {
        if (_showExplanation.value) return
        timerJob?.cancel()
        _selectedOptionIndex.value = optionIndex
        _showExplanation.value = true

        val currentQ = _quizQuestions.value.getOrNull(_currentQuestionIndex.value) ?: return
        val isCorrect = optionIndex == currentQ.correctIndex

        if (isCorrect) {
            _quizCorrectCount.value += 1
            _quizStreak.value += 1
            playSound(SoundEffect.CORRECT_ANSWER)
        } else {
            _quizStreak.value = 0
            playSound(SoundEffect.INCORRECT_ANSWER)
        }
    }

    fun nextQuestionOrFinish() {
        playSound(SoundEffect.UI_INTERACTION)
        val total = _quizQuestions.value.size
        if (_currentQuestionIndex.value + 1 < total) {
            _currentQuestionIndex.value += 1
            _selectedOptionIndex.value = null
            _showExplanation.value = false
            startQuestionTimer()
        } else {
            finishLevelQuiz()
        }
    }

    private fun finishLevelQuiz() {
        val uid = _currentUser.value?.id ?: 1L
        val levelNum = _activeLevelNumber.value ?: 1
        val total = _quizQuestions.value.size.coerceAtLeast(1)
        val correct = _quizCorrectCount.value
        val percentage = (correct * 100) / total

        val stars = when {
            percentage >= 90 -> 3
            percentage >= 65 -> 2
            percentage >= 40 -> 1
            else -> 0
        }

        if (stars >= 1) {
            playSound(SoundEffect.LEVEL_UNLOCK)
        } else {
            playSound(SoundEffect.GAME_COMPLETION)
        }

        val xpGained = (correct * 40) + (_quizStreak.value * 15)
        val score = percentage

        _quizFinishedResult.value = QuizResult(
            levelNumber = levelNum,
            correctCount = correct,
            totalQuestions = total,
            starsEarned = stars,
            xpEarned = xpGained,
            maxStreak = _quizStreak.value
        )

        viewModelScope.launch {
            repository.completeLevel(uid, levelNum, stars, score)
            repository.recordGameScore(uid, "aptitude_road", score, xpGained, streakBonus = true)
        }
    }

    fun exitQuiz() {
        playSound(SoundEffect.UI_INTERACTION)
        timerJob?.cancel()
        _activeLevelNumber.value = null
        _quizFinishedResult.value = null
    }

    fun recordMiniGameResult(gameId: String, score: Int, stars: Int, xp: Int) {
        val uid = _currentUser.value?.id ?: 1L
        playSound(SoundEffect.GAME_COMPLETION)
        viewModelScope.launch {
            repository.recordGameScore(uid, gameId, score, xp, streakBonus = true)
        }
    }

    data class QuizResult(
        val levelNumber: Int,
        val correctCount: Int,
        val totalQuestions: Int,
        val starsEarned: Int,
        val xpEarned: Int,
        val maxStreak: Int
    )
}
