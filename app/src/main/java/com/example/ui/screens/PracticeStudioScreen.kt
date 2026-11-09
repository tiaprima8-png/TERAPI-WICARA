package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.local.PracticePhraseEntity
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Level1Color
import com.example.ui.theme.Level2Color
import com.example.ui.theme.Level3Color
import com.example.ui.theme.Level4Color
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldContainer

@Composable
fun PracticeStudioScreen(
    phrases: List<PracticePhraseEntity>,
    selectedPhraseId: Int,
    activeSyllableBadge: String?,
    isRecording: Boolean,
    recordingSecondsLeft: Int,
    isPlayingAudio: Boolean,
    audioStatusMessage: String?,
    celebrationMessage: String?,
    onSelectPhrase: (Int) -> Unit,
    onTapSyllable: (String, Boolean, PracticePhraseEntity) -> Unit,
    onSpeakFullPhrase: (PracticePhraseEntity) -> Unit,
    onStartRecording: (PracticePhraseEntity) -> Unit,
    onPlayRecording: (PracticePhraseEntity) -> Unit,
    onUpdateLevel: (PracticePhraseEntity, Int) -> Unit,
    onAddCustomPhrase: (String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val activePhrase = phrases.find { it.id == selectedPhraseId } ?: phrases.firstOrNull()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var isCameraMirrorEnabled by remember { mutableStateOf(true) }
    var showAddPhraseDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) isCameraMirrorEnabled = true
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted && activePhrase != null) {
            onStartRecording(activePhrase)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("practice_studio_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Front Camera Mirror ("Cermin Mulut Abi & Adik")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("camera_mirror_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CameraFront,
                                contentDescription = null,
                                tint = AmberBright,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Cermin Mulut Abi & Adik",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Posisikan wajah berdampingan • Fokus bukaan rahang",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldContainer
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (!hasCameraPermission) {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                } else {
                                    isCameraMirrorEnabled = !isCameraMirrorEnabled
                                }
                            },
                            modifier = Modifier.testTag("toggle_camera_mirror_button")
                        ) {
                            Icon(
                                imageVector = if (hasCameraPermission && isCameraMirrorEnabled) {
                                    Icons.Filled.Videocam
                                } else {
                                    Icons.Filled.VideocamOff
                                },
                                contentDescription = "Aktifkan atau matikan kamera cermin",
                                tint = AmberBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Camera Preview Box or Animated Mirror Guide Fallback
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(195.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF022C22)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasCameraPermission && isCameraMirrorEnabled) {
                            FrontCameraMirrorPreview(modifier = Modifier.fillMaxSize())
                        } else {
                            // Interactive Animated Mirror Guide when camera permission not yet granted
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Mode Panduan Visual Mulut Abi & Adik",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Aktifkan Kamera Depan agar Adik bisa melihat bibir & rahang bergerak bersama Abi.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldContainer,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (!hasCameraPermission) {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        } else {
                                            isCameraMirrorEnabled = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AmberBright,
                                        contentColor = OnAmberContainer
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.testTag("enable_camera_permission_button")
                                ) {
                                    Icon(Icons.Filled.CameraFront, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Buka Kamera Cermin", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Dual Mouth Guide Overlay (Abi Left + Adik Right)
                        MouthGuideFrameOverlay(
                            activeSyllable = activeSyllableBadge,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // 2. Horizontal Phrase Selector + Add Custom 2-Word Button
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Target Rangkaian 2 Kata (${phrases.size}):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FilledTonalButton(
                        onClick = { showAddPhraseDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EmeraldContainer,
                            contentColor = OnEmeraldContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_custom_phrase_button")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Tambah Kata", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                    modifier = Modifier.testTag("phrase_selector_row")
                ) {
                    items(phrases, key = { it.id }) { item ->
                        val isSelected = item.id == activePhrase?.id
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectPhrase(item.id) }
                                .testTag("phrase_chip_${item.id}"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (item.clinicalLevel) {
                                                4 -> Level4Color
                                                3 -> Level3Color
                                                2 -> Level2Color
                                                else -> Level1Color
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.fullPhrase,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Interactive Syllable Tap-Pad (Papan Ketuk Suku Kata 2 Kata)
        if (activePhrase != null) {
            item {
                SyllableTapPadCard(
                    phrase = activePhrase,
                    activeSyllableBadge = activeSyllableBadge,
                    onTapSyllable = { syl, isFirst ->
                        onTapSyllable(syl, isFirst, activePhrase)
                    },
                    onSpeakFullPhrase = { onSpeakFullPhrase(activePhrase) }
                )
            }

            // 4. Voice Recorder (5 Seconds) & Playback + 4-Level Quick Evaluation
            item {
                VoiceRecorderAndEvaluationCard(
                    phrase = activePhrase,
                    isRecording = isRecording,
                    recordingSecondsLeft = recordingSecondsLeft,
                    isPlayingAudio = isPlayingAudio,
                    audioStatusMessage = audioStatusMessage,
                    celebrationMessage = celebrationMessage,
                    onRecordClick = {
                        if (hasMicPermission) {
                            onStartRecording(activePhrase)
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onPlayClick = { onPlayRecording(activePhrase) },
                    onSelectLevel = { newLevel -> onUpdateLevel(activePhrase, newLevel) }
                )
            }
        }
    }

    if (showAddPhraseDialog) {
        AddCustomPhraseDialog(
            onDismiss = { showAddPhraseDialog = false },
            onConfirm = { w1s1, w1s2, w2s1, w2s2, focus ->
                onAddCustomPhrase(w1s1, w1s2, w2s1, w2s2, focus)
                showAddPhraseDialog = false
            }
        )
    }
}

@Composable
private fun FrontCameraMirrorPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        val listener = Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val selector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
            } catch (_: Exception) {
                // Safe fallback if emulator camera is busy
            }
        }
        cameraProviderFuture.addListener(listener, executor)

        onDispose {
            try {
                cameraProviderFuture.get().unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

@Composable
private fun MouthGuideFrameOverlay(
    activeSyllable: String?,
    modifier: Modifier = Modifier
) {
    val isFirstSyllableActive = activeSyllable != null
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
            val frameWidth = size.width * 0.36f
            val frameHeight = if (isFirstSyllableActive) size.height * 0.56f else size.height * 0.44f
            val topY = (size.height - frameHeight) / 2f

            // Left Frame (Mulut Abi)
            drawRoundRect(
                color = Color(0xFF34D399).copy(alpha = 0.85f),
                topLeft = Offset(size.width * 0.08f, topY),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(36f, 36f),
                style = Stroke(width = 4f, pathEffect = dashEffect)
            )

            // Right Frame (Mulut Adik)
            drawRoundRect(
                color = Color(0xFFFBBF24).copy(alpha = 0.9f),
                topLeft = Offset(size.width * 0.56f, topY),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(36f, 36f),
                style = Stroke(width = 5f, pathEffect = dashEffect)
            )
        }

        // Top badge when syllable tapped
        AnimatedVisibility(
            visible = activeSyllable != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        ) {
            Surface(
                color = AmberBright,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "BUKA RAHANG: \"${activeSyllable ?: ""}\"!",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnAmberContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Bottom labels for Abi & Adik frames
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = EmeraldPrimary.copy(alpha = 0.85f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Panduan Mulut Abi",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Surface(
                color = AmberPrimary.copy(alpha = 0.9f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Bukaan Rahang Adik",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun SyllableTapPadCard(
    phrase: PracticePhraseEntity,
    activeSyllableBadge: String?,
    onTapSyllable: (String, Boolean) -> Unit,
    onSpeakFullPhrase: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "first_syl_pulse")
    val firstSylPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "first_syl_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("syllable_tap_pad_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, AmberPrimary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (phrase.isAnchorBase) EmeraldContainer else AmberContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = phrase.clinicalFocus,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (phrase.isAnchorBase) OnEmeraldContainer else OnAmberContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Papan Ketuk: \"${phrase.fullPhrase}\"",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = onSpeakFullPhrase,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("speak_full_phrase_button")
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = "Bunyikan 2 kata utuh", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("2 Kata Utuh", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = phrase.jawInstruction,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // WORD 1 ROW: Suku Kata Pertama (BESAR + Kontras Tinggi + BUKA RAHANG!) + Suku Kata Kedua
            Text(
                text = "KATA 1 (Ketuk Suku Kata Depan Lebih Kuat):",
                style = MaterialTheme.typography.labelMedium,
                color = EmeraldDark,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SUKU KATA PERTAMA KATA 1 (LARGER, HIGH CONTRAST)
                FirstSyllableBigButton(
                    syllable = phrase.word1Syllable1,
                    isHighlighted = activeSyllableBadge == phrase.word1Syllable1,
                    containerColor = EmeraldPrimary,
                    badgeColor = AmberBright,
                    badgeTextColor = OnAmberContainer,
                    scale = firstSylPulse,
                    testTag = "tap_w1_s1_button",
                    modifier = Modifier.weight(1.38f),
                    onClick = { onTapSyllable(phrase.word1Syllable1, true) }
                )

                // SUKU KATA KEDUA KATA 1
                SecondSyllableButton(
                    syllable = phrase.word1Syllable2,
                    isHighlighted = activeSyllableBadge == phrase.word1Syllable2,
                    testTag = "tap_w1_s2_button",
                    modifier = Modifier.weight(1f),
                    onClick = { onTapSyllable(phrase.word1Syllable2, false) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // WORD 2 ROW: Suku Kata Pertama (BESAR + Kontras Tinggi + BUKA RAHANG!) + Suku Kata Kedua
            Text(
                text = "KATA 2 (JANGAN POTONG AWAL KATA!):",
                style = MaterialTheme.typography.labelMedium,
                color = AmberPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SUKU KATA PERTAMA KATA 2 (LARGER, HIGH CONTRAST WARM AMBER)
                FirstSyllableBigButton(
                    syllable = phrase.word2Syllable1,
                    isHighlighted = activeSyllableBadge == phrase.word2Syllable1,
                    containerColor = AmberPrimary,
                    badgeColor = Color.White,
                    badgeTextColor = OnAmberContainer,
                    scale = firstSylPulse,
                    testTag = "tap_w2_s1_button",
                    modifier = Modifier.weight(1.38f),
                    onClick = { onTapSyllable(phrase.word2Syllable1, true) }
                )

                // SUKU KATA KEDUA KATA 2
                SecondSyllableButton(
                    syllable = phrase.word2Syllable2,
                    isHighlighted = activeSyllableBadge == phrase.word2Syllable2,
                    testTag = "tap_w2_s2_button",
                    modifier = Modifier.weight(1f),
                    onClick = { onTapSyllable(phrase.word2Syllable2, false) }
                )
            }
        }
    }
}

@Composable
private fun FirstSyllableBigButton(
    syllable: String,
    isHighlighted: Boolean,
    containerColor: Color,
    badgeColor: Color,
    badgeTextColor: Color,
    scale: Float,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(106.dp)
            .scale(if (isHighlighted) 1.07f else scale)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        color = containerColor,
        shadowElevation = if (isHighlighted) 8.dp else 4.dp,
        border = BorderStroke(
            width = if (isHighlighted) 3.dp else 0.dp,
            color = if (isHighlighted) AmberBright else Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "BUKA RAHANG!",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeTextColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(
                text = syllable,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ketuk & Getar • 0.7x",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SecondSyllableButton(
    syllable: String,
    isHighlighted: Boolean,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = if (isHighlighted) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(2.dp, EmeraldPrimary.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = syllable,
                style = MaterialTheme.typography.headlineLarge,
                color = OnEmeraldContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Suku Kata Ujung",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun VoiceRecorderAndEvaluationCard(
    phrase: PracticePhraseEntity,
    isRecording: Boolean,
    recordingSecondsLeft: Int,
    isPlayingAudio: Boolean,
    audioStatusMessage: String?,
    celebrationMessage: String?,
    onRecordClick: () -> Unit,
    onPlayClick: () -> Unit,
    onSelectLevel: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recorder_evaluation_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Rekam Suara Adik (5 Detik) & Evaluasi Cepat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Anak usia 3 tahun sangat senang mendengar suaranya sendiri! Rekam lalu putar kembali.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRecordClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("record_5_sec_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) CoralAccent else EmeraldPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                        contentDescription = "Rekam suara 5 detik"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isRecording) "Merekam (${recordingSecondsLeft}d)" else "Rekam Suara (5d)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("play_recording_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, AmberPrimary)
                ) {
                    Icon(
                        imageVector = if (isPlayingAudio) Icons.Filled.GraphicEq else Icons.Filled.PlayArrow,
                        contentDescription = "Putar rekaman",
                        tint = AmberPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isPlayingAudio) "Memutar..." else "Putar Rekaman",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnAmberContainer
                    )
                }
            }

            if (!audioStatusMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = CoralContainer.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = audioStatusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnCoralContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Bagaimana Ucapan Adik Tadi? (Pilih Level Klinis):",
                style = MaterialTheme.typography.labelLarge,
                color = EmeraldDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 4-Level Clinical Selector Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val levels = listOf(
                    Triple(1, "Lv 1 (25%)", phrase.exampleLevel1),
                    Triple(2, "Lv 2 (50%)", phrase.exampleLevel2),
                    Triple(3, "Lv 3 (75%)", phrase.exampleLevel3),
                    Triple(4, "Lv 4 (100%)", phrase.exampleLevel4)
                )
                levels.forEach { (lv, title, example) ->
                    val isSelected = phrase.clinicalLevel == lv
                    val lvColor = when (lv) {
                        1 -> Level1Color
                        2 -> Level2Color
                        3 -> Level3Color
                        else -> Level4Color
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectLevel(lv) }
                            .testTag("eval_level_button_$lv"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) lvColor else lvColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.5.dp, lvColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else lvColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"$example\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = !celebrationMessage.isNullOrBlank()) {
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = celebrationMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnEmeraldContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCustomPhraseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var w1s1 by remember { mutableStateOf("A") }
    var w1s2 by remember { mutableStateOf("BI") }
    var w2s1 by remember { mutableStateOf("CU") }
    var w2s2 by remember { mutableStateOf("CI") }
    var focus by remember { mutableStateOf("Latihan suku kata awal bersama Abi") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Target 2 Kata Baru",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pisahkan setiap kata menjadi 2 suku kata agar Suku Kata Pertama tampil besar.",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = w1s1,
                        onValueChange = { w1s1 = it },
                        label = { Text("Kata 1 - Awal") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = w1s2,
                        onValueChange = { w1s2 = it },
                        label = { Text("Kata 1 - Akhir") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = w2s1,
                        onValueChange = { w2s1 = it },
                        label = { Text("Kata 2 - Awal") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = w2s2,
                        onValueChange = { w2s2 = it },
                        label = { Text("Kata 2 - Akhir") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = focus,
                    onValueChange = { focus = it },
                    label = { Text("Catatan Fokus Terapi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (w1s1.isNotBlank() && w2s1.isNotBlank()) {
                        onConfirm(w1s1, w1s2, w2s1, w2s2, focus)
                    }
                },
                modifier = Modifier.testTag("confirm_add_phrase_button")
            ) {
                Text("Simpan Frase")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
