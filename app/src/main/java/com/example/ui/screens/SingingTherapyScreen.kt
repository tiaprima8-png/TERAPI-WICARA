package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.viewmodel.ClinicalContentProvider
import com.example.ui.viewmodel.KakaktuaLyricLine

@Composable
fun SingingTherapyScreen(
    activeClozeLineId: Int?,
    clozeWaitSeconds: Int,
    clozePromptReadyForChild: Int?,
    celebrationMessage: String?,
    totalSingingSessions: Int,
    onStartClozeProcedure: (KakaktuaLyricLine) -> Unit,
    onTapTargetSyllable: (KakaktuaLyricLine, String) -> Unit,
    onSpeakSlowly: (String) -> Unit
) {
    val lyrics = ClinicalContentProvider.kakaktuaLyrics

    val infiniteTransition = rememberInfiniteTransition(label = "singing_pulse")
    val readyScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ready_pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("singing_therapy_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card: Terapi Bernyanyi "Burung Kakaktua" (Cloze Procedure)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = AmberBright,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Karaoke Terapi: Burung Kakaktua",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White
                                )
                                Text(
                                    text = "Teknik Rem Mendadak / Jeda Menggantung (Cloze Procedure)",
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
                                text = "${totalSingingSessions}x Nyanyi",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnAmberContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PanTool,
                                contentDescription = null,
                                tint = AmberBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Cara Main: Ketuk \"Abi Nyanyi & Rem 3d\". Saat hitungan mundur 3 detik muncul, tahan suara Abi, tatap mata Adik dengan ekspresi antusias hingga Adik menyambung suku kata besar di ujung!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Celebration Banner when child completes a Cloze syllable
        if (!celebrationMessage.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberContainer),
                    border = BorderStroke(2.dp, AmberPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Celebration,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = celebrationMessage,
                            style = MaterialTheme.typography.titleSmall,
                            color = OnAmberContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Interactive Cloze Lyric Cards
        items(lyrics, key = { it.id }) { line ->
            val isWaiting3Sec = activeClozeLineId == line.id && clozeWaitSeconds > 0
            val isChildTurnNow = clozePromptReadyForChild == line.id

            ClozeLyricCard(
                line = line,
                isWaiting3Sec = isWaiting3Sec,
                clozeWaitSeconds = clozeWaitSeconds,
                isChildTurnNow = isChildTurnNow,
                pulseScale = if (isChildTurnNow) readyScale else 1f,
                onStartCloze = { onStartClozeProcedure(line) },
                onTapSyllable = { syl -> onTapTargetSyllable(line, syl) },
                onSpeakLeadIn = { onSpeakSlowly(line.leadInTts) }
            )
        }
    }
}

@Composable
private fun ClozeLyricCard(
    line: KakaktuaLyricLine,
    isWaiting3Sec: Boolean,
    clozeWaitSeconds: Int,
    isChildTurnNow: Boolean,
    pulseScale: Float,
    onStartCloze: () -> Unit,
    onTapSyllable: (String) -> Unit,
    onSpeakLeadIn: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cloze_lyric_card_${line.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (line.isChorusGym) {
                AmberContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isChildTurnNow || isWaiting3Sec) 2.5.dp else 1.dp,
            color = when {
                isWaiting3Sec -> CoralAccent
                isChildTurnNow -> AmberPrimary
                line.isChorusGym -> AmberPrimary
                else -> MaterialTheme.colorScheme.outline
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (line.isChorusGym) AmberPrimary else EmeraldContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (line.isChorusGym) "REFF SENAM RAHANG & PAHA" else "BARIS ${line.id} • CLOZE PROCEDURE",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (line.isChorusGym) Color.White else OnEmeraldContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${line.leadInLyric}\"",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { onSpeakLeadIn() }
                    )
                }

                Button(
                    onClick = onStartCloze,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWaiting3Sec) CoralAccent else EmeraldPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("cloze_wait_button_${line.id}")
                ) {
                    Icon(
                        imageVector = if (isWaiting3Sec) Icons.Filled.HourglassTop else Icons.Filled.PlayArrow,
                        contentDescription = "Mulai Rem Mendadak 3 Detik",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isWaiting3Sec) "Tahan ${clozeWaitSeconds}d..." else "Abi Nyanyi & Rem 3d",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // 3-Second "Abi Berhenti Mendadak" Visual Countdown Banner
            AnimatedVisibility(visible = isWaiting3Sec) {
                Surface(
                    color = CoralContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CoralAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$clozeWaitSeconds",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ABI BERHENTI MENDADAK! TAHAN SUARA...",
                                style = MaterialTheme.typography.titleSmall,
                                color = OnCoralContainer,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Buka mulut Abi lebar & tunggu Adik menyambung suku kata di bawah!",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnCoralContainer
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isChildTurnNow) {
                Surface(
                    color = AmberBright,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "GILIRAN ADIK SEKARANG! KETUK TOMBOL BESAR DI BAWAH!",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnAmberContainer,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Big Cloze Target Syllable Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(pulseScale),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First Cloze Syllable (Giant Button)
                Surface(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(94.dp)
                        .clickable { onTapSyllable(line.targetSyllable1) }
                        .testTag("cloze_syl1_${line.id}"),
                    shape = RoundedCornerShape(20.dp),
                    color = AmberPrimary,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "SUKU KATA AWAL!",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnAmberContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "[ ${line.targetSyllable1} ]",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ketuk & Bunyikan",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp
                        )
                    }
                }

                // Second Cloze Syllable (Giant Open Jaw Button)
                Surface(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(94.dp)
                        .clickable { onTapSyllable(line.targetSyllable2) }
                        .testTag("cloze_syl2_${line.id}"),
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldPrimary,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = AmberBright,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "BUKA RAHANG!",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnAmberContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "[ ${line.targetSyllable2} ]",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tahan Rahang Turun",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Extra Chorus Jaw Gym Button ("[ LA - LA - LA ]")
            if (line.targetExtraSyllable != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .clickable { onTapSyllable("La la la") }
                        .testTag("cloze_extra_chorus_${line.id}"),
                    shape = RoundedCornerShape(18.dp),
                    color = EmeraldDark
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "[ ${line.targetExtraSyllable} ]",
                                style = MaterialTheme.typography.headlineMedium,
                                color = AmberBright,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rahang tetap terbuka lebar, lidah naik-turun!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Bunyikan La La La",
                            tint = AmberBright,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Panduan Mulut: ${line.jawCoachingTip}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
