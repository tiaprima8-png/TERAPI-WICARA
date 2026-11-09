package com.example.hardware

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class SpeechAndAudioHelper(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var tts: TextToSpeech? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var recordingJob: Job? = null

    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _currentlySpeakingText = MutableStateFlow<String?>(null)
    val currentlySpeakingText: StateFlow<String?> = _currentlySpeakingText.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSecondsLeft = MutableStateFlow(0)
    val recordingSecondsLeft: StateFlow<Int> = _recordingSecondsLeft.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _audioStatusMessage = MutableStateFlow<String?>(null)
    val audioStatusMessage: StateFlow<String?> = _audioStatusMessage.asStateFlow()

    init {
        initTts()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val indoLocale = Locale("id", "ID")
                    val result = tts?.setLanguage(indoLocale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale.getDefault())
                    }
                    // Clinical slow rate 0.7f so child can observe and imitate syllable transitions
                    tts?.setSpeechRate(0.7f)
                    tts?.setPitch(1.05f)
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _currentlySpeakingText.value = utteranceId
                        }

                        override fun onDone(utteranceId: String?) {
                            _currentlySpeakingText.value = null
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _currentlySpeakingText.value = null
                        }
                    })
                    _isTtsReady.value = true
                }
            }
        } catch (e: Exception) {
            _isTtsReady.value = false
        }
    }

    /**
     * Speaks Indonesian text slowly (speechRate = 0.7f by default) for clear pediatric modeling.
     */
    fun speakSlowly(text: String, customRate: Float = 0.7f) {
        triggerGentleHaptic(isStrongFirstSyllable = true)
        val engine = tts
        if (engine != null && _isTtsReady.value) {
            engine.setSpeechRate(customRate)
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
        } else {
            // Visual fallback pulse if TTS engine is not ready on emulator
            scope.launch {
                _currentlySpeakingText.value = text
                delay(900)
                _currentlySpeakingText.value = null
            }
        }
    }

    /**
     * Triggers soft tactile vibration when tapping syllable pads or jaw rhythm buttons.
     */
    fun triggerGentleHaptic(isStrongFirstSyllable: Boolean = false) {
        try {
            val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                val durationMs = if (isStrongFirstSyllable) 65L else 35L
                val amplitude = if (isStrongFirstSyllable) 210 else 120
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {
            // Safe ignore if vibrator hardware unavailable
        }
    }

    /**
     * Records a 5-second voice practice sample for the given phraseId.
     */
    fun startFiveSecondRecording(
        phraseId: Int,
        onFinished: (String) -> Unit
    ) {
        if (_isRecording.value) {
            stopRecording()
            return
        }
        stopPlaying()

        val outputDir = File(context.filesDir, "rekaman_wicara")
        if (!outputDir.exists()) outputDir.mkdirs()
        val outputFile = File(outputDir, "rekaman_phrase_${phraseId}.m4a")
        if (outputFile.exists()) outputFile.delete()

        try {
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioSamplingRate(44100)
            recorder.setAudioEncodingBitRate(96000)
            recorder.setOutputFile(outputFile.absolutePath)
            recorder.prepare()
            recorder.start()

            mediaRecorder = recorder
            _isRecording.value = true
            _recordingSecondsLeft.value = 5
            _audioStatusMessage.value = "Merekam suara Adik & Abi (5 detik)..."
            triggerGentleHaptic(true)

            recordingJob?.cancel()
            recordingJob = scope.launch(Dispatchers.Main) {
                for (sec in 5 downTo 1) {
                    _recordingSecondsLeft.value = sec
                    delay(1000L)
                }
                stopRecording()
                if (outputFile.exists() && outputFile.length() > 0L) {
                    _audioStatusMessage.value = "Rekaman 5 detik berhasil disimpan! Ketuk Putar Rekaman."
                    onFinished(outputFile.absolutePath)
                } else {
                    _audioStatusMessage.value = "Rekaman selesai."
                    onFinished(outputFile.absolutePath)
                }
            }
        } catch (e: Exception) {
            _isRecording.value = false
            _recordingSecondsLeft.value = 0
            _audioStatusMessage.value = "Mikrofon sedang digunakan atau belum diizinkan."
        }
    }

    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
        } finally {
            mediaRecorder = null
            _isRecording.value = false
            _recordingSecondsLeft.value = 0
        }
    }

    fun playRecording(filePath: String?) {
        if (filePath.isNullOrBlank()) {
            _audioStatusMessage.value = "Belum ada rekaman untuk kata ini. Yuk rekam 5 detik dulu!"
            return
        }
        val file = File(filePath)
        if (!file.exists()) {
            _audioStatusMessage.value = "File rekaman belum tersedia. Yuk rekam suara Adik!"
            return
        }

        if (_isPlayingAudio.value) {
            stopPlaying()
            return
        }

        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener {
                    _isPlayingAudio.value = false
                    _audioStatusMessage.value = "Selesai memutar suara latihan Adik! Hebat!"
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { mp, _, _ ->
                    _isPlayingAudio.value = false
                    _audioStatusMessage.value = "Gagal memutar file rekaman."
                    mp.release()
                    mediaPlayer = null
                    true
                }
                prepare()
                start()
            }
            mediaPlayer = player
            _isPlayingAudio.value = true
            _audioStatusMessage.value = "Memutar ulang suara Adik..."
            triggerGentleHaptic(false)
        } catch (e: Exception) {
            _isPlayingAudio.value = false
            _audioStatusMessage.value = "Tidak dapat memutar rekaman pada perangkat ini."
        }
    }

    fun stopPlaying() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
            _isPlayingAudio.value = false
        }
    }

    fun clearStatusMessage() {
        _audioStatusMessage.value = null
    }

    fun releaseAll() {
        stopRecording()
        stopPlaying()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
    }
}
