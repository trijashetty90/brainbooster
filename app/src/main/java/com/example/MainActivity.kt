package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.SoundEffect
import com.example.ui.components.BrainBoostBottomBar
import com.example.ui.screens.AsyncStorageInspectorScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProgressAnalyticsScreen
import com.example.ui.screens.ProjectReportScreen
import com.example.ui.screens.Sector1ArcadeScreen
import com.example.ui.screens.Sector2RoadScreen
import com.example.ui.screens.minigames.MemoryCardFlipScreen
import com.example.ui.screens.minigames.NumberMemoryFlashScreen
import com.example.ui.screens.minigames.SpeedMathGameScreen
import com.example.ui.screens.minigames.StroopColorGameScreen
import com.example.ui.screens.minigames.WordUnscrambleScreen
import com.example.ui.theme.BrainBoostTheme
import com.example.ui.viewmodel.BrainBoostViewModel

enum class AppScreen {
    DASHBOARD,
    AUTH,
    PROGRESS_ANALYTICS,
    SECTOR_1_ARCADE,
    SECTOR_2_ROAD,
    PROJECT_REPORT,
    ASYNC_STORAGE_INSPECTOR,
    GAME_SPEED_MATH,
    GAME_MEMORY_FLIP,
    GAME_WORD_UNSCRAMBLE,
    GAME_NUMBER_MEMORY,
    GAME_STROOP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrainBoostTheme {
                val viewModel: BrainBoostViewModel = viewModel()
                val userStats by viewModel.userStats.collectAsState()
                val aptitudeLevels by viewModel.aptitudeLevels.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val isSfxEnabled by viewModel.isSfxEnabled.collectAsState()
                val isMusicEnabled by viewModel.isMusicEnabled.collectAsState()
                val dailyStreakState by viewModel.dailyStreakState.collectAsState()
                val activeLevelNum by viewModel.activeLevelNumber.collectAsState()

                var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

                val shouldShowBottomBar = when (currentScreen) {
                    AppScreen.DASHBOARD,
                    AppScreen.SECTOR_1_ARCADE,
                    AppScreen.PROGRESS_ANALYTICS -> true
                    AppScreen.SECTOR_2_ROAD -> activeLevelNum == null
                    else -> false
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            BrainBoostBottomBar(
                                currentScreen = currentScreen,
                                unlockedAptitudeLevels = aptitudeLevels.count { it.isUnlocked },
                                onNavigate = { targetScreen ->
                                    viewModel.playSound(SoundEffect.UI_INTERACTION)
                                    currentScreen = targetScreen
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> {
                                DashboardScreen(
                                    userStats = userStats,
                                    currentUser = currentUser,
                                    dailyStreakState = dailyStreakState,
                                    isSfxEnabled = isSfxEnabled,
                                    isMusicEnabled = isMusicEnabled,
                                    unlockedLevelsCount = aptitudeLevels.count { it.isUnlocked },
                                    totalStarsCount = aptitudeLevels.sumOf { it.starsEarned },
                                    onToggleSfx = { viewModel.toggleSfx() },
                                    onToggleMusic = { viewModel.toggleMusic() },
                                    onClaimDailyStreak = { viewModel.claimDailyStreakBonus() },
                                    onNavigateAuth = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.AUTH
                                    },
                                    onLogout = { viewModel.logout() },
                                    onNavigateSector1 = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.SECTOR_1_ARCADE
                                    },
                                    onNavigateSector2 = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.SECTOR_2_ROAD
                                    },
                                    onNavigateAnalytics = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.PROGRESS_ANALYTICS
                                    },
                                    onNavigateReport = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.PROJECT_REPORT
                                    },
                                    onNavigateInspector = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.ASYNC_STORAGE_INSPECTOR
                                    }
                                )
                            }

                            AppScreen.AUTH -> {
                                BackHandler { currentScreen = AppScreen.DASHBOARD }
                                AuthScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = AppScreen.DASHBOARD },
                                    onAuthSuccess = { currentScreen = AppScreen.DASHBOARD }
                                )
                            }

                            AppScreen.PROGRESS_ANALYTICS -> {
                                BackHandler { currentScreen = AppScreen.DASHBOARD }
                                ProgressAnalyticsScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = AppScreen.DASHBOARD }
                                )
                            }

                            AppScreen.SECTOR_1_ARCADE -> {
                                BackHandler { currentScreen = AppScreen.DASHBOARD }
                                Sector1ArcadeScreen(
                                    userStats = userStats,
                                    onBack = { currentScreen = AppScreen.DASHBOARD },
                                    onNavigateSector2 = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.SECTOR_2_ROAD
                                    },
                                    onLaunchGame = { gameId ->
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = when (gameId) {
                                            "speed_math" -> AppScreen.GAME_SPEED_MATH
                                            "memory_flip" -> AppScreen.GAME_MEMORY_FLIP
                                            "word_unscramble" -> AppScreen.GAME_WORD_UNSCRAMBLE
                                            "number_memory" -> AppScreen.GAME_NUMBER_MEMORY
                                            "stroop_challenge" -> AppScreen.GAME_STROOP
                                            else -> AppScreen.SECTOR_1_ARCADE
                                        }
                                    }
                                )
                            }

                            AppScreen.SECTOR_2_ROAD -> {
                                BackHandler { currentScreen = AppScreen.DASHBOARD }
                                Sector2RoadScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = AppScreen.DASHBOARD },
                                    onNavigateSector1 = {
                                        viewModel.playSound(SoundEffect.UI_INTERACTION)
                                        currentScreen = AppScreen.SECTOR_1_ARCADE
                                    }
                                )
                            }

                        AppScreen.PROJECT_REPORT -> {
                            BackHandler { currentScreen = AppScreen.DASHBOARD }
                            ProjectReportScreen(
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                        }

                        AppScreen.ASYNC_STORAGE_INSPECTOR -> {
                            BackHandler { currentScreen = AppScreen.DASHBOARD }
                            AsyncStorageInspectorScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                        }

                        AppScreen.GAME_SPEED_MATH -> {
                            BackHandler { currentScreen = AppScreen.SECTOR_1_ARCADE }
                            SpeedMathGameScreen(
                                onBack = { currentScreen = AppScreen.SECTOR_1_ARCADE },
                                onGameComplete = { score, stars, xp ->
                                    viewModel.recordMiniGameResult("speed_math", score, stars, xp)
                                }
                            )
                        }

                        AppScreen.GAME_MEMORY_FLIP -> {
                            BackHandler { currentScreen = AppScreen.SECTOR_1_ARCADE }
                            MemoryCardFlipScreen(
                                onBack = { currentScreen = AppScreen.SECTOR_1_ARCADE },
                                onGameComplete = { score, stars, xp ->
                                    viewModel.recordMiniGameResult("memory_flip", score, stars, xp)
                                }
                            )
                        }

                        AppScreen.GAME_WORD_UNSCRAMBLE -> {
                            BackHandler { currentScreen = AppScreen.SECTOR_1_ARCADE }
                            WordUnscrambleScreen(
                                onBack = { currentScreen = AppScreen.SECTOR_1_ARCADE },
                                onGameComplete = { score, stars, xp ->
                                    viewModel.recordMiniGameResult("word_unscramble", score, stars, xp)
                                }
                            )
                        }

                        AppScreen.GAME_NUMBER_MEMORY -> {
                            BackHandler { currentScreen = AppScreen.SECTOR_1_ARCADE }
                            NumberMemoryFlashScreen(
                                onBack = { currentScreen = AppScreen.SECTOR_1_ARCADE },
                                onGameComplete = { score, stars, xp ->
                                    viewModel.recordMiniGameResult("number_memory", score, stars, xp)
                                }
                            )
                        }

                        AppScreen.GAME_STROOP -> {
                            BackHandler { currentScreen = AppScreen.SECTOR_1_ARCADE }
                            StroopColorGameScreen(
                                onBack = { currentScreen = AppScreen.SECTOR_1_ARCADE },
                                onGameComplete = { score, stars, xp ->
                                    viewModel.recordMiniGameResult("stroop_challenge", score, stars, xp)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}
