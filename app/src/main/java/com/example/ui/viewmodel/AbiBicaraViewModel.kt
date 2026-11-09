package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AbiBicaraDatabase
import com.example.data.local.AppStatsEntity
import com.example.data.local.ClinicalNoteEntity
import com.example.data.local.PracticePhraseEntity
import com.example.data.local.RoutineCheckEntity
import com.example.data.repository.AbiBicaraRepository
import com.example.hardware.SpeechAndAudioHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AbiBicaraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AbiBicaraDatabase.getInstance(application)
    private val repository = AbiBicaraRepository(database.abiBicaraDao())
    val speechAndAudio = SpeechAndAudioHelper(application, viewModelScope)

    // Reactive Database Flows
    val routines: StateFlow<List<RoutineCheckEntity>> = repository.routinesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val phrases: StateFlow<List<PracticePhraseEntity>> = repository.phrasesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clinicalNotes: StateFlow<List<ClinicalNoteEntity>> = repository.clinicalNotesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appStats: StateFlow<AppStatsEntity?> = repository.appStatsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppStatsEntity())

    // Selected Practice Phrase in Module 3
    private val _selectedPhraseId = MutableStateFlow(2) // Default to "Abi makan" (Target utama)
    val selectedPhraseId: StateFlow<Int> = _selectedPhraseId.asStateFlow()

    // Last tapped syllable highlight state for visual animation in Module 3
    private val _activeSyllableBadge = MutableStateFlow<String?>(null)
    val activeSyllableBadge: StateFlow<String?> = _activeSyllableBadge.asStateFlow()

    // Module 2: 4-Stage Jaw Exercise Simulator State
    private val _currentJawStageIndex = MutableStateFlow(0)
    val currentJawStageIndex: StateFlow<Int> = _currentJawStageIndex.asStateFlow()

    private val _jawTimerSecondsLeft = MutableStateFlow(
        ClinicalContentProvider.jawExerciseStages[0].durationSeconds
    )
    val jawTimerSecondsLeft: StateFlow<Int> = _jawTimerSecondsLeft.asStateFlow()

    private val _isJawTimerRunning = MutableStateFlow(false)
    val isJawTimerRunning: StateFlow<Boolean> = _isJawTimerRunning.asStateFlow()

    private var jawTimerJob: Job? = null

    // Module 4: Singing Therapy ("Abi Berhenti Mendadak 3 Detik" Cloze Procedure) State
    private val _activeClozeLineId = MutableStateFlow<Int?>(null)
    val activeClozeLineId: StateFlow<Int?> = _activeClozeLineId.asStateFlow()

    private val _clozeWaitSeconds = MutableStateFlow(0)
    val clozeWaitSeconds: StateFlow<Int> = _clozeWaitSeconds.asStateFlow()

    private val _clozePromptReadyForChild = MutableStateFlow<Int?>(null)
    val clozePromptReadyForChild: StateFlow<Int?> = _clozePromptReadyForChild.asStateFlow()

    private val _celebrationMessage = MutableStateFlow<String?>(null)
    val celebrationMessage: StateFlow<String?> = _celebrationMessage.asStateFlow()

    private var clozeJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    // --- MODULE 1: SCHEDULE & ROUTINES ---
    fun toggleRoutineCheck(routine: RoutineCheckEntity) {
        speechAndAudio.triggerGentleHaptic(false)
        viewModelScope.launch {
            repository.toggleRoutine(routine)
        }
    }

    fun resetDailySchedule() {
        speechAndAudio.triggerGentleHaptic(false)
        viewModelScope.launch {
            repository.resetDailyRoutines()
        }
    }

    // --- MODULE 2: ORAL-MOTOR & JAW GYM SIMULATOR ---
    fun selectJawStage(index: Int) {
        val safeIndex = index.coerceIn(0, ClinicalContentProvider.jawExerciseStages.lastIndex)
        pauseJawTimer()
        _currentJawStageIndex.value = safeIndex
        _jawTimerSecondsLeft.value = ClinicalContentProvider.jawExerciseStages[safeIndex].durationSeconds
        speechAndAudio.triggerGentleHaptic(false)
    }

    fun toggleJawTimer() {
        if (_isJawTimerRunning.value) {
            pauseJawTimer()
        } else {
            startJawTimer()
        }
    }

    private fun startJawTimer() {
        val stage = ClinicalContentProvider.jawExerciseStages[_currentJawStageIndex.value]
        if (_jawTimerSecondsLeft.value <= 0) {
            _jawTimerSecondsLeft.value = stage.durationSeconds
        }
        _isJawTimerRunning.value = true
        speechAndAudio.speakSlowly(stage.ttsGuideScript, customRate = 0.78f)

        jawTimerJob?.cancel()
        jawTimerJob = viewModelScope.launch {
            while (_jawTimerSecondsLeft.value > 0 && _isJawTimerRunning.value) {
                delay(1000L)
                val nextVal = _jawTimerSecondsLeft.value - 1
                _jawTimerSecondsLeft.value = nextVal
                // Gentle rhythmic pulse every 4 seconds during active jaw exercise
                if (nextVal > 0 && nextVal % 4 == 0) {
                    speechAndAudio.triggerGentleHaptic(false)
                }
            }
            if (_jawTimerSecondsLeft.value == 0) {
                _isJawTimerRunning.value = false
                speechAndAudio.triggerGentleHaptic(true)
                speechAndAudio.speakSlowly("Hebat sekali! Tahap senam rahang selesai.", customRate = 0.8f)
                repository.incrementJawGymSession()
            }
        }
    }

    fun pauseJawTimer() {
        _isJawTimerRunning.value = false
        jawTimerJob?.cancel()
        jawTimerJob = null
    }

    fun resetJawTimer() {
        pauseJawTimer()
        val stage = ClinicalContentProvider.jawExerciseStages[_currentJawStageIndex.value]
        _jawTimerSecondsLeft.value = stage.durationSeconds
        speechAndAudio.triggerGentleHaptic(false)
    }

    // --- MODULE 3: STUDIO LATIHAN 2 KATA & CERMIN MULUT ---
    fun selectPhrase(phraseId: Int) {
        _selectedPhraseId.value = phraseId
        speechAndAudio.clearStatusMessage()
        speechAndAudio.triggerGentleHaptic(false)
    }

    fun onTapSyllable(
        syllableText: String,
        isFirstSyllable: Boolean,
        phrase: PracticePhraseEntity? = null
    ) {
        speechAndAudio.triggerGentleHaptic(isStrongFirstSyllable = isFirstSyllable)
        // Speak with clear, slow rate 0.7f
        val spokenForm = when (syllableText.uppercase()) {
            "A" -> "Aaa"
            "BI" -> "Bi"
            "MA" -> "Maa"
            "KAN" -> "Kan"
            "BU" -> "Buu"
            "KA" -> "Kaa"
            "MI" -> "Mii"
            "NUM" -> "Num"
            "BO" -> "Boo"
            "MAN" -> "Maan"
            "DI" -> "Dii"
            "MO" -> "Moo"
            "BIL" -> "Bil"
            "JA" -> "Jaa"
            "LAN" -> "Lan"
            "TU" -> "Tuu"
            "DU" -> "Duu"
            "DE" -> "Dee"
            "LA" -> "Laa"
            else -> syllableText.lowercase()
        }
        speechAndAudio.speakSlowly(spokenForm, customRate = 0.7f)

        viewModelScope.launch {
            _activeSyllableBadge.value = syllableText
            delay(850)
            if (_activeSyllableBadge.value == syllableText) {
                _activeSyllableBadge.value = null
            }
        }

        if (phrase != null) {
            viewModelScope.launch {
                repository.recordPhrasePractice(phrase)
            }
        }
    }

    fun speakFullPhraseSlowly(phrase: PracticePhraseEntity) {
        speechAndAudio.triggerGentleHaptic(true)
        // Add subtle pause between syllables for clarity
        val articulated = "${phrase.word1Syllable1.lowercase()} ${phrase.word1Syllable2.lowercase()}, ${phrase.word2Syllable1.lowercase()} ${phrase.word2Syllable2.lowercase()}"
        speechAndAudio.speakSlowly(articulated, customRate = 0.7f)
        viewModelScope.launch {
            repository.recordPhrasePractice(phrase)
        }
    }

    fun startRecordingForPhrase(phrase: PracticePhraseEntity) {
        speechAndAudio.startFiveSecondRecording(phrase.id) { savedFilePath ->
            viewModelScope.launch {
                repository.recordPhrasePractice(phrase, audioPath = savedFilePath)
            }
        }
    }

    fun playRecordingForPhrase(phrase: PracticePhraseEntity) {
        speechAndAudio.playRecording(phrase.recordedFilePath)
    }

    fun updateClinicalLevel(phrase: PracticePhraseEntity, level: Int) {
        speechAndAudio.triggerGentleHaptic(true)
        viewModelScope.launch {
            repository.updatePhraseLevel(phrase, level)
            val levelPraise = when (level) {
                4 -> "Luar biasa! ${phrase.fullPhrase} sudah di Level 4, dua kata jelas dan spontan!"
                3 -> "Bagus sekali! Satu kata utuh sudah jelas."
                2 -> "Kemajuan bagus! Vokal pembuka rahang sudah mulai terdengar."
                else -> "Level dicatat. Yuk terus semangat latihan bersama Abi!"
            }
            _celebrationMessage.value = levelPraise
            delay(3200)
            if (_celebrationMessage.value == levelPraise) {
                _celebrationMessage.value = null
            }
        }
    }

    fun addNewTargetPhrase(
        w1s1: String,
        w1s2: String,
        w2s1: String,
        w2s2: String,
        focus: String
    ) {
        viewModelScope.launch {
            repository.addCustomPhrase(w1s1, w1s2, w2s1, w2s2, focus)
        }
    }

    // --- MODULE 4: TERAPI BERNYANYI "BURUNG KAKAKTUA" (CLOZE PROCEDURE) ---
    fun startClozeSingAndWait(line: KakaktuaLyricLine) {
        clozeJob?.cancel()
        _activeClozeLineId.value = line.id
        _clozePromptReadyForChild.value = null
        _clozeWaitSeconds.value = 0

        // 1. Abi/App sings the lead-in slowly
        speechAndAudio.speakSlowly(line.leadInTts, customRate = 0.7f)

        clozeJob = viewModelScope.launch {
            // Wait slightly for lead-in TTS to finish before starting the 3-second Cloze pause
            delay(1700L)
            // 2. "Rem Mendadak! Tahan 3 Detik Tunggu Adik Menyambung"
            for (sec in 3 downTo 1) {
                _clozeWaitSeconds.value = sec
                speechAndAudio.triggerGentleHaptic(false)
                delay(1000L)
            }
            _clozeWaitSeconds.value = 0
            _clozePromptReadyForChild.value = line.id
            speechAndAudio.triggerGentleHaptic(true)
        }
    }

    fun onChildCompletedClozeSyllable(line: KakaktuaLyricLine, syllableTapped: String) {
        onTapSyllable(syllableTapped, isFirstSyllable = true)
        viewModelScope.launch {
            repository.incrementSingingSession()
            val msg = "Horeee! Adik hebat menyambung '${line.targetSyllable1} - ${line.targetSyllable2}' dengan rahang terbuka!"
            _celebrationMessage.value = msg
            delay(3000L)
            if (_celebrationMessage.value == msg) {
                _celebrationMessage.value = null
            }
        }
    }

    // --- MODULE 5: CLINICAL NOTES & EXPORT REPORT ---
    fun addObservationNote(
        sessionContext: String,
        jawStatus: String,
        anchorWordUsed: String,
        observationText: String
    ) {
        speechAndAudio.triggerGentleHaptic(false)
        viewModelScope.launch {
            repository.addClinicalNote(
                sessionContext = sessionContext,
                jawStatus = jawStatus,
                anchorWordUsed = anchorWordUsed,
                observationText = observationText
            )
        }
    }

    fun deleteObservationNote(noteId: Int) {
        viewModelScope.launch {
            repository.deleteClinicalNote(noteId)
        }
    }

    fun generateClinicalReportText(): String {
        val currentPhrases = phrases.value
        val currentRoutines = routines.value
        val currentNotes = clinicalNotes.value
        val stats = appStats.value ?: AppStatsEntity()
        val dateStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())

        val level4Count = currentPhrases.count { it.clinicalLevel == 4 }
        val level3Count = currentPhrases.count { it.clinicalLevel == 3 }
        val level2Count = currentPhrases.count { it.clinicalLevel == 2 }
        val level1Count = currentPhrases.count { it.clinicalLevel == 1 }
        val avgReadiness = if (currentPhrases.isNotEmpty()) {
            (currentPhrases.sumOf { it.clinicalLevel * 25 }) / currentPhrases.size
        } else 0

        return buildString {
            appendLine("==========================================")
            appendLine("LAPORAN KLINIS TERAPI WICARA & ORAL-MOTOR")
            appendLine("Aplikasi: AbiBicara Pro")
            appendLine("Tanggal Cetak: $dateStr")
            appendLine("==========================================")
            appendLine()
            appendLine("1. PROFIL KLINIS ANAK")
            appendLine("• Usia: 3 Tahun")
            appendLine("• Fokus Terapi: Mengatasi Initial Syllable Deletion (penghilangan suku kata awal akibat rahang/TMJ tertahan) & Merangkai 2 Kata.")
            appendLine("• Modal Kata Jangkar: 'Abi minum' & Lagu 'Burung Kakaktua'")
            appendLine()
            appendLine("2. RINGKASAN KESIAPAN BICARA")
            appendLine("• Skor Kesiapan Artikulasi: $avgReadiness%")
            appendLine("• Streak Latihan Harian: ${stats.streakDays} Hari Berturut-turut")
            appendLine("• Total Sesi Senam Rahang: ${stats.totalJawGymSessions} Sesi")
            appendLine("• Total Sesi Terapi Bernyanyi: ${stats.totalSingingSessions} Sesi")
            appendLine("• Rutinitas Hari Ini: ${currentRoutines.count { it.isCompleted }}/${currentRoutines.size} Waktu Selesai")
            appendLine()
            appendLine("3. DISTRIBUSI 4 LEVEL KLINIS (${currentPhrases.size} Frase Target)")
            appendLine("• Level 4 (100% - 2 Kata Jelas & Spontan): $level4Count frase")
            appendLine("• Level 3 (75% - 1 Kata Utuh 2 Suku Kata): $level3Count frase")
            appendLine("• Level 2 (50% - Muncul Vokal Bayangan Depan): $level2Count frase")
            appendLine("• Level 1 (25% - Hanya Ujung Kata): $level1Count frase")
            appendLine()
            appendLine("4. DETAIL PROGRES PER KOMBINASI 2 KATA")
            currentPhrases.forEachIndexed { idx, p ->
                val levelDesc = when (p.clinicalLevel) {
                    1 -> "Level 1 (25% - Ujung kata: ${p.exampleLevel1})"
                    2 -> "Level 2 (50% - Vokal bayangan: ${p.exampleLevel2})"
                    3 -> "Level 3 (75% - 1 Kata utuh: ${p.exampleLevel3})"
                    else -> "Level 4 (100% - 2 Kata spontan: ${p.exampleLevel4})"
                }
                appendLine("${idx + 1}. ${p.fullPhrase} [${p.word1Syllable1}-${p.word1Syllable2} + ${p.word2Syllable1}-${p.word2Syllable2}]")
                appendLine("   Status: $levelDesc | Latihan: ${p.practiceCount}x")
            }
            appendLine()
            appendLine("5. CATATAN OBSERVASI ORANG TUA (ABI & IBU)")
            if (currentNotes.isEmpty()) {
                appendLine("• Belum ada catatan tambahan.")
            } else {
                currentNotes.take(5).forEach { n ->
                    appendLine("• [${n.dateLabel}] (${n.sessionContext})")
                    appendLine("  Status Rahang: ${n.jawStatus} | Jangkar: ${n.anchorWordUsed}")
                    appendLine("  Observasi: ${n.observationText}")
                }
            }
            appendLine("==========================================")
        }
    }

    override fun onCleared() {
        super.onCleared()
        pauseJawTimer()
        clozeJob?.cancel()
        speechAndAudio.releaseAll()
    }
}
