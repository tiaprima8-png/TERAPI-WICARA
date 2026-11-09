package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.viewmodel.ClinicalContentProvider
import com.example.ui.viewmodel.JawExerciseStage
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OralMotorScreen(
    currentStageIndex: Int,
    timerSecondsLeft: Int,
    isTimerRunning: Boolean,
    totalCompletedSessions: Int,
    onSelectStage: (Int) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSpeakInstruction: (String) -> Unit
) {
    val stages = ClinicalContentProvider.jawExerciseStages
    val activeStage = stages.getOrElse(currentStageIndex) { stages.first() }
    val progressFraction = (timerSecondsLeft.toFloat() / activeStage.durationSeconds.toFloat())
        .coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("oral_motor_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Stage Selector Tabs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = null,
                                tint = AmberBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Simulator Pijat & Senam Rahang",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White
                                )
                                Text(
                                    text = "Melemaskan Otot Masseter & Sendi TMJ Sebelum Bicara",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldContainer
                                )
                            }
                        }
                        Surface(
                            color = AmberBright,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "$totalCompletedSessions Sesi",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnAmberContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Stage Stepper Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stages.forEachIndexed { index, stage ->
                            val isSelected = index == currentStageIndex
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { onSelectStage(index) }
                                    .testTag("jaw_stage_tab_${index + 1}"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) AmberBright else Color.White.copy(alpha = 0.14f)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Tahap ${stage.stageNumber}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) OnAmberContainer else Color.White
                                    )
                                    Text(
                                        text = "${stage.durationSeconds}d",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) OnAmberContainer else EmeraldContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Interactive Animated Face / Jaw Opening Visualizer + Countdown Timer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jaw_visualizer_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = AmberContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "SUARA PANDUAN: \"${activeStage.soundCue}\"",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnAmberContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Animated Pediatric Face & Jaw Visualizer
                    AnimatedJawFaceVisualizer(
                        stageNumber = activeStage.stageNumber,
                        isAnimating = isTimerRunning
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = activeStage.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = activeStage.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countdown Timer Display
                    val minutes = timerSecondsLeft / 60
                    val seconds = timerSecondsLeft % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.displayLarge,
                            color = if (isTimerRunning) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("jaw_timer_display")
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        FilledTonalButton(
                            onClick = { onSpeakInstruction(activeStage.ttsGuideScript) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AmberContainer,
                                contentColor = OnAmberContainer
                            ),
                            modifier = Modifier.testTag("jaw_tts_guide_button")
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Suara Instruksi", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Suara Panduan", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if (isTimerRunning) AmberPrimary else EmeraldPrimary,
                        trackColor = EmeraldContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play / Pause / Reset Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onToggleTimer,
                            modifier = Modifier
                                .weight(1.6f)
                                .height(54.dp)
                                .testTag("jaw_timer_play_pause_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) AmberPrimary else EmeraldPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isTimerRunning) "Jeda Timer" else "Mulai Timer"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTimerRunning) "Jeda Latihan" else "Mulai Hitung Mundur",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = onResetTimer,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("jaw_timer_reset_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, EmeraldPrimary)
                        ) {
                            Icon(Icons.Filled.Replay, contentDescription = "Ulangi Timer")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        // 3. Step-by-Step Clinical Instructions for Active Stage
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Langkah Praktik Bersama Adik:",
                        style = MaterialTheme.typography.titleLarge,
                        color = EmeraldDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    activeStage.steps.forEachIndexed { idx, stepText ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = OnEmeraldContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stepText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = EmeraldContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Manfaat Klinis Terapi Wicara:",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldDark,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeStage.clinicalBenefit,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnEmeraldContainer
                            )
                        }
                    }
                }
            }
        }

        // 4. All 4 Stages Quick Overview Cards
        itemsIndexed(stages) { idx, stage ->
            JawStageOverviewRow(
                stage = stage,
                isSelected = idx == currentStageIndex,
                onClick = { onSelectStage(idx) }
            )
        }
    }
}

@Composable
private fun AnimatedJawFaceVisualizer(
    stageNumber: Int,
    isAnimating: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jaw_anim")

    // Jaw opening ratio (0.15f closed -> 1.0f wide open)
    val jawOpenProgress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isAnimating) 1.0f else 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (stageNumber == 3) 900 else 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jaw_open"
    )

    // Circular massage angle for Stage 1 & Lip tap index for Stage 2
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400),
            repeatMode = RepeatMode.Restart
        ),
        label = "tmj_circle"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF0FDF4)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp)
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f - 10f
            val headRadius = size.minDimension * 0.38f

            // Dynamic chin drop based on stage & animation
            val jawDropPx = when (stageNumber) {
                3 -> 28f * jawOpenProgress // Lion roar 100% drop
                4 -> 24f * jawOpenProgress // Jaw prompting drop
                1 -> 16f * jawOpenProgress // Hum -> Aaa
                else -> 14f * jawOpenProgress
            }

            // Ears (TMJ reference landmarks)
            drawCircle(
                color = Color(0xFFFDE68A),
                radius = 16f,
                center = Offset(centerX - headRadius + 4f, centerY)
            )
            drawCircle(
                color = Color(0xFFFDE68A),
                radius = 16f,
                center = Offset(centerX + headRadius - 4f, centerY)
            )

            // Upper Head Circle + Lower Jaw Oval that drops vertically
            drawCircle(
                color = Color(0xFFFEF3C7),
                radius = headRadius,
                center = Offset(centerX, centerY)
            )
            drawRoundRect(
                color = Color(0xFFFEF3C7),
                topLeft = Offset(centerX - headRadius * 0.75f, centerY),
                size = Size(headRadius * 1.5f, headRadius + jawDropPx),
                cornerRadius = CornerRadius(48f, 48f)
            )

            // Outline of head & jaw
            drawRoundRect(
                color = Color(0xFFD97706),
                topLeft = Offset(centerX - headRadius * 0.85f, centerY - headRadius * 0.9f),
                size = Size(headRadius * 1.7f, headRadius * 1.85f + jawDropPx),
                cornerRadius = CornerRadius(70f, 70f),
                style = Stroke(width = 5f)
            )

            // Cheerful Eyes
            drawCircle(
                color = Color(0xFF064E3B),
                radius = 7f,
                center = Offset(centerX - 26f, centerY - 18f)
            )
            drawCircle(
                color = Color(0xFF064E3B),
                radius = 7f,
                center = Offset(centerX + 26f, centerY - 18f)
            )

            // Cheeks (Puff out in Stage 3)
            val cheekRadius = if (stageNumber == 3) 14f + 8f * jawOpenProgress else 13f
            drawCircle(
                color = Color(0xFFFDA4AF).copy(alpha = 0.65f),
                radius = cheekRadius,
                center = Offset(centerX - 42f, centerY + 8f)
            )
            drawCircle(
                color = Color(0xFFFDA4AF).copy(alpha = 0.65f),
                radius = cheekRadius,
                center = Offset(centerX + 42f, centerY + 8f)
            )

            // Mouth Cavity (Opens vertically with jawDropPx)
            val mouthWidth = if (stageNumber == 2) 34f else 52f
            val mouthHeight = (14f + jawDropPx * 1.35f).coerceAtLeast(14f)
            val mouthTop = centerY + 14f

            drawRoundRect(
                color = Color(0xFF881337),
                topLeft = Offset(centerX - mouthWidth / 2f, mouthTop),
                size = Size(mouthWidth, mouthHeight),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // Tongue inside open mouth
            if (mouthHeight > 22f) {
                drawRoundRect(
                    color = Color(0xFFFB7185),
                    topLeft = Offset(centerX - mouthWidth * 0.32f, mouthTop + mouthHeight * 0.45f),
                    size = Size(mouthWidth * 0.64f, mouthHeight * 0.5f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
            }

            // STAGE-SPECIFIC VISUAL GUIDES
            when (stageNumber) {
                1 -> {
                    // Stage 1: TMJ & Masseter Circular Massage Points
                    val rad = Math.toRadians(rotationAngle.toDouble())
                    val orbitX = (cos(rad) * 8f).toFloat()
                    val orbitY = (sin(rad) * 12f).toFloat()

                    val leftTmj = Offset(centerX - headRadius * 0.72f + orbitX, centerY + 6f + orbitY)
                    val rightTmj = Offset(centerX + headRadius * 0.72f - orbitX, centerY + 6f + orbitY)

                    drawCircle(color = EmeraldPrimary.copy(alpha = 0.35f), radius = 18f, center = leftTmj)
                    drawCircle(color = EmeraldPrimary, radius = 9f, center = leftTmj)

                    drawCircle(color = EmeraldPrimary.copy(alpha = 0.35f), radius = 18f, center = rightTmj)
                    drawCircle(color = EmeraldPrimary, radius = 9f, center = rightTmj)
                }

                2 -> {
                    // Stage 2: Lip Tapping Dots around mouth
                    val tapCount = 8
                    val activeTapIndex = ((rotationAngle / 360f) * tapCount).toInt() % tapCount
                    for (i in 0 until tapCount) {
                        val angle = Math.toRadians((i * (360.0 / tapCount)))
                        val dotX = centerX + (cos(angle) * (mouthWidth * 0.78f)).toFloat()
                        val dotY = (mouthTop + mouthHeight / 2f) + (sin(angle) * (mouthHeight * 0.72f)).toFloat()
                        drawCircle(
                            color = if (i == activeTapIndex) CoralAccent else AmberBright,
                            radius = if (i == activeTapIndex) 9f else 5f,
                            center = Offset(dotX, dotY)
                        )
                    }
                }

                3 -> {
                    // Stage 3: Lion Roar 100% Downward Expansion Arrows
                    val arrowY = centerY + headRadius + jawDropPx + 14f
                    drawCircle(
                        color = AmberPrimary,
                        radius = 10f,
                        center = Offset(centerX, arrowY)
                    )
                }

                4 -> {
                    // Stage 4: Abi's Index Finger Prompting Under the Chin
                    val chinY = centerY + headRadius * 0.95f + jawDropPx
                    val fingerPath = Path().apply {
                        moveTo(centerX - 36f, chinY + 18f)
                        lineTo(centerX + 36f, chinY + 18f)
                    }
                    drawPath(
                        path = fingerPath,
                        color = EmeraldPrimary,
                        style = Stroke(width = 16f)
                    )
                    drawCircle(
                        color = AmberBright,
                        radius = 11f,
                        center = Offset(centerX, chinY + 6f)
                    )
                }
            }
        }

        // Top-right status pill inside visualizer
        Surface(
            color = if (isAnimating) EmeraldPrimary else AmberContainer,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Text(
                text = when (stageNumber) {
                    1 -> "Titik Hijau = Pijat Putar TMJ"
                    2 -> "Titik Merah = Ketuk Lingkar Bibir"
                    3 -> "Rahang Turun 100% (Singa)"
                    else -> "Garis Hijau = Jari Abi di Bawah Dagu"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (isAnimating) Color.White else OnAmberContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun JawStageOverviewRow(
    stage: JawExerciseStage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("jaw_stage_card_${stage.stageNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isSelected) EmeraldPrimary else AmberContainer,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${stage.stageNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) Color.White else OnAmberContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stage.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${stage.durationSeconds} Detik • Cue: ${stage.soundCue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
