package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppStatsEntity
import com.example.ui.screens.ClinicalProgressScreen
import com.example.ui.screens.OralMotorScreen
import com.example.ui.screens.PracticeStudioScreen
import com.example.ui.screens.ScheduleHomeScreen
import com.example.ui.screens.SingingTherapyScreen
import com.example.ui.theme.AbiBicaraProTheme
import com.example.ui.theme.AmberBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.viewmodel.AbiBicaraViewModel

enum class AbiBicaraTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    SCHEDULE(
        route = "jadwal",
        label = "Jadwal",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        testTag = "nav_tab_jadwal"
    ),
    JAW_GYM(
        route = "senam_rahang",
        label = "Senam Rahang",
        selectedIcon = Icons.Filled.Spa,
        unselectedIcon = Icons.Outlined.Spa,
        testTag = "nav_tab_senam_rahang"
    ),
    PRACTICE(
        route = "latihan",
        label = "Latihan",
        selectedIcon = Icons.Filled.RecordVoiceOver,
        unselectedIcon = Icons.Outlined.RecordVoiceOver,
        testTag = "nav_tab_latihan"
    ),
    SINGING(
        route = "bernyanyi",
        label = "Bernyanyi",
        selectedIcon = Icons.Filled.MusicNote,
        unselectedIcon = Icons.Outlined.MusicNote,
        testTag = "nav_tab_bernyanyi"
    ),
    PROGRESS(
        route = "progres",
        label = "Progres",
        selectedIcon = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment,
        testTag = "nav_tab_progres"
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbiBicaraProTheme {
                AbiBicaraMainApp()
            }
        }
    }
}

@Composable
fun AbiBicaraMainApp(
    viewModel: AbiBicaraViewModel = viewModel()
) {
    var currentTab by rememberSaveable { mutableStateOf(AbiBicaraTab.SCHEDULE) }

    // Collect reactive state from ViewModel & Hardware Helper
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val phrases by viewModel.phrases.collectAsStateWithLifecycle()
    val clinicalNotes by viewModel.clinicalNotes.collectAsStateWithLifecycle()
    val appStats by viewModel.appStats.collectAsStateWithLifecycle()
    val selectedPhraseId by viewModel.selectedPhraseId.collectAsStateWithLifecycle()
    val activeSyllableBadge by viewModel.activeSyllableBadge.collectAsStateWithLifecycle()

    val currentJawStageIndex by viewModel.currentJawStageIndex.collectAsStateWithLifecycle()
    val jawTimerSecondsLeft by viewModel.jawTimerSecondsLeft.collectAsStateWithLifecycle()
    val isJawTimerRunning by viewModel.isJawTimerRunning.collectAsStateWithLifecycle()

    val activeClozeLineId by viewModel.activeClozeLineId.collectAsStateWithLifecycle()
    val clozeWaitSeconds by viewModel.clozeWaitSeconds.collectAsStateWithLifecycle()
    val clozePromptReadyForChild by viewModel.clozePromptReadyForChild.collectAsStateWithLifecycle()
    val celebrationMessage by viewModel.celebrationMessage.collectAsStateWithLifecycle()

    val currentlySpeakingText by viewModel.speechAndAudio.currentlySpeakingText.collectAsStateWithLifecycle()
    val isRecording by viewModel.speechAndAudio.isRecording.collectAsStateWithLifecycle()
    val recordingSecondsLeft by viewModel.speechAndAudio.recordingSecondsLeft.collectAsStateWithLifecycle()
    val isPlayingAudio by viewModel.speechAndAudio.isPlayingAudio.collectAsStateWithLifecycle()
    val audioStatusMessage by viewModel.speechAndAudio.audioStatusMessage.collectAsStateWithLifecycle()

    // Ensure hardware Back button returns to Schedule Home when on secondary tabs
    BackHandler(enabled = currentTab != AbiBicaraTab.SCHEDULE) {
        currentTab = AbiBicaraTab.SCHEDULE
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            AbiBicaraTopBar(
                currentlySpeakingText = currentlySpeakingText,
                onQuickAnchorSpeak = {
                    viewModel.speechAndAudio.speakSlowly("A bi, mi num", customRate = 0.7f)
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                AbiBicaraTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            viewModel.speechAndAudio.triggerGentleHaptic(false)
                            currentTab = tab
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnEmeraldContainer,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "main_tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    AbiBicaraTab.SCHEDULE -> {
                        ScheduleHomeScreen(
                            routines = routines,
                            phrases = phrases,
                            stats = appStats ?: AppStatsEntity(),
                            onToggleRoutine = viewModel::toggleRoutineCheck,
                            onResetRoutines = viewModel::resetDailySchedule,
                            onSpeakSlowly = { text -> viewModel.speechAndAudio.speakSlowly(text, 0.7f) },
                            onNavigateToPractice = { currentTab = AbiBicaraTab.PRACTICE },
                            onNavigateToJawGym = { currentTab = AbiBicaraTab.JAW_GYM },
                            onNavigateToSinging = { currentTab = AbiBicaraTab.SINGING }
                        )
                    }

                    AbiBicaraTab.JAW_GYM -> {
                        OralMotorScreen(
                            currentStageIndex = currentJawStageIndex,
                            timerSecondsLeft = jawTimerSecondsLeft,
                            isTimerRunning = isJawTimerRunning,
                            totalCompletedSessions = appStats?.totalJawGymSessions ?: 4,
                            onSelectStage = viewModel::selectJawStage,
                            onToggleTimer = viewModel::toggleJawTimer,
                            onResetTimer = viewModel::resetJawTimer,
                            onSpeakInstruction = { script ->
                                viewModel.speechAndAudio.speakSlowly(script, 0.75f)
                            }
                        )
                    }

                    AbiBicaraTab.PRACTICE -> {
                        PracticeStudioScreen(
                            phrases = phrases,
                            selectedPhraseId = selectedPhraseId,
                            activeSyllableBadge = activeSyllableBadge,
                            isRecording = isRecording,
                            recordingSecondsLeft = recordingSecondsLeft,
                            isPlayingAudio = isPlayingAudio,
                            audioStatusMessage = audioStatusMessage,
                            celebrationMessage = celebrationMessage,
                            onSelectPhrase = viewModel::selectPhrase,
                            onTapSyllable = viewModel::onTapSyllable,
                            onSpeakFullPhrase = viewModel::speakFullPhraseSlowly,
                            onStartRecording = viewModel::startRecordingForPhrase,
                            onPlayRecording = viewModel::playRecordingForPhrase,
                            onUpdateLevel = viewModel::updateClinicalLevel,
                            onAddCustomPhrase = viewModel::addNewTargetPhrase
                        )
                    }

                    AbiBicaraTab.SINGING -> {
                        SingingTherapyScreen(
                            activeClozeLineId = activeClozeLineId,
                            clozeWaitSeconds = clozeWaitSeconds,
                            clozePromptReadyForChild = clozePromptReadyForChild,
                            celebrationMessage = celebrationMessage,
                            totalSingingSessions = appStats?.totalSingingSessions ?: 5,
                            onStartClozeProcedure = viewModel::startClozeSingAndWait,
                            onTapTargetSyllable = viewModel::onChildCompletedClozeSyllable,
                            onSpeakSlowly = { text -> viewModel.speechAndAudio.speakSlowly(text, 0.7f) }
                        )
                    }

                    AbiBicaraTab.PROGRESS -> {
                        ClinicalProgressScreen(
                            phrases = phrases,
                            clinicalNotes = clinicalNotes,
                            onUpdatePhraseLevel = viewModel::updateClinicalLevel,
                            onAddClinicalNote = viewModel::addObservationNote,
                            onDeleteClinicalNote = viewModel::deleteObservationNote,
                            onGenerateReportText = viewModel::generateClinicalReportText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AbiBicaraTopBar(
    currentlySpeakingText: String?,
    onQuickAnchorSpeak: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EmeraldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AB",
                        style = MaterialTheme.typography.titleMedium,
                        color = AmberBright,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AbiBicara Pro",
                        style = MaterialTheme.typography.titleLarge,
                        color = EmeraldDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pediatric Speech & Oral-Motor Trainer",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = !currentlySpeakingText.isNullOrBlank()) {
                Surface(
                    color = AmberBright,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = "TTS Aktif",
                            tint = OnAmberContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TTS 0.7x",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnAmberContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (currentlySpeakingText.isNullOrBlank()) {
                Surface(
                    onClick = onQuickAnchorSpeak,
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("topbar_anchor_pill")
                ) {
                    Text(
                        text = "Jangkar: A-bi Mi-num",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnEmeraldContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
