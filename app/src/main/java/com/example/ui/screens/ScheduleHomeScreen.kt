package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AppStatsEntity
import com.example.data.local.PracticePhraseEntity
import com.example.data.local.RoutineCheckEntity
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Level4Color
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.viewmodel.ClinicalContentProvider
import com.example.ui.viewmodel.ParentGoldenRule

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleHomeScreen(
    routines: List<RoutineCheckEntity>,
    phrases: List<PracticePhraseEntity>,
    stats: AppStatsEntity,
    onToggleRoutine: (RoutineCheckEntity) -> Unit,
    onResetRoutines: () -> Unit,
    onSpeakSlowly: (String) -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToJawGym: () -> Unit,
    onNavigateToSinging: () -> Unit
) {
    val completedRoutines = routines.count { it.isCompleted }
    val totalRoutines = routines.size.coerceAtLeast(1)
    val level4Count = phrases.count { it.clinicalLevel == 4 }
    val readinessPercent = if (phrases.isNotEmpty()) {
        (phrases.sumOf { it.clinicalLevel * 25 }) / phrases.size
    } else 65

    val infiniteTransition = rememberInfiniteTransition(label = "anchor_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("schedule_home_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner & Clinical Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_summary_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(156.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_abibicara_1794184388268),
                            contentDescription = "Ilustrasi Abi dan Adik berlatih bicara bersama",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            EmeraldDark.copy(alpha = 0.88f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Surface(
                                color = AmberBright,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "USIA 3 TAHUN • FOKUS BUKAAN RAHANG & 2 KATA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnAmberContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "Semangat Pagi, Abi & Adik!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }

                    // 3 Key Metrics Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardMetricPill(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Verified,
                            value = "$readinessPercent%",
                            label = "Kesiapan Bicara",
                            accentColor = Color(0xFF34D399)
                        )
                        DashboardMetricPill(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.EmojiEvents,
                            value = "$level4Count Kata",
                            label = "Level 4 (2 Kata)",
                            accentColor = AmberBright
                        )
                        DashboardMetricPill(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.LocalFireDepartment,
                            value = "${stats.streakDays} Hari",
                            label = "Streak Latihan",
                            accentColor = Color(0xFFFB7185)
                        )
                    }

                    // Quick Action Anchor Strip
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Modal Jangkar Terkuat Adik:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldContainer
                                )
                                Text(
                                    text = "\"A-BI MI-NUM\" & \"KAKAKTUA\"",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = { onSpeakSlowly("A bi, mi num") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberBright,
                                    contentColor = OnAmberContainer
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .scale(pulseScale)
                                    .testTag("quick_anchor_speak_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "Bunyikan kata jangkar Abi Minum",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "A-BI MI-NUM",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Quick Navigation Shortcuts to Interactive Therapy Tools
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = onNavigateToJawGym,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("shortcut_jaw_gym_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldContainer,
                        contentColor = OnEmeraldContainer
                    )
                ) {
                    Icon(Icons.Filled.Spa, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Pijat TMJ", style = MaterialTheme.typography.labelLarge)
                }
                FilledTonalButton(
                    onClick = onNavigateToPractice,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("shortcut_practice_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AmberContainer,
                        contentColor = OnAmberContainer
                    )
                ) {
                    Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cermin 2 Kata", style = MaterialTheme.typography.labelLarge)
                }
                FilledTonalButton(
                    onClick = onNavigateToSinging,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("shortcut_singing_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CoralContainer,
                        contentColor = OnCoralContainer
                    )
                ) {
                    Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Kakaktua", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // 3. Checklist Rutinitas 4 Waktu Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Checklist Rutinitas 4 Waktu",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tersimpan otomatis • $completedRoutines dari $totalRoutines sesi hari ini selesai",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = onResetRoutines,
                            modifier = Modifier.testTag("reset_daily_routines_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Reset checklist harian",
                                tint = EmeraldPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { completedRoutines.toFloat() / totalRoutines.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = EmeraldPrimary,
                        trackColor = EmeraldContainer
                    )
                }
            }
        }

        // 4. Routine Items (Pagi, Siang, Sore, Malam)
        items(routines, key = { it.id }) { routine ->
            RoutineTimeCard(
                routine = routine,
                onToggle = { onToggleRoutine(routine) },
                onSpeakWord = onSpeakSlowly
            )
        }

        // 5. Kartu Saku "Do's & Don'ts" Orang Tua (Aturan Emas Terapi di Rumah)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Kartu Saku \"Do's & Don'ts\" Abi & Ibu",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "3 Aturan Emas Klinis agar Otot Rahang Adik Rileks & Mau Meniru",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(ClinicalContentProvider.parentGoldenRules, key = { it.id }) { rule ->
            ParentGoldenRuleCard(
                rule = rule,
                onSpeakExample = onSpeakSlowly
            )
        }
    }
}

@Composable
private fun DashboardMetricPill(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    accentColor: Color
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldContainer,
                fontSize = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutineTimeCard(
    routine: RoutineCheckEntity,
    onToggle: () -> Unit,
    onSpeakWord: (String) -> Unit
) {
    val badgeBg = when (routine.timeSlot) {
        "PAGI" -> AmberContainer
        "SIANG" -> Color(0xFFE0F2FE)
        "SORE" -> EmeraldContainer
        else -> Color(0xFFEDE9FE)
    }
    val badgeText = when (routine.timeSlot) {
        "PAGI" -> OnAmberContainer
        "SIANG" -> Color(0xFF0C4A6E)
        "SORE" -> OnEmeraldContainer
        else -> Color(0xFF4C1D95)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("routine_card_${routine.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (routine.isCompleted) {
                EmeraldContainer.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (routine.isCompleted) 2.dp else 1.dp,
            color = if (routine.isCompleted) Level4Color else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("routine_checkbox_${routine.id}")
                ) {
                    Icon(
                        imageVector = if (routine.isCompleted) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Outlined.CheckBoxOutlineBlank
                        },
                        contentDescription = if (routine.isCompleted) "Selesai" else "Belum selesai",
                        tint = if (routine.isCompleted) Level4Color else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "${routine.timeSlot} • ${routine.timeLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = routine.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Motorik Oral: ${routine.oralMotorTask}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Wicara: ${routine.speechTask}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Ketuk Target Kata (Suara TTS Lambat):",
                style = MaterialTheme.typography.labelMedium,
                color = EmeraldPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                routine.targetWordsCsv.split(",").forEach { rawWord ->
                    val word = rawWord.trim()
                    if (word.isNotEmpty()) {
                        AssistChip(
                            onClick = { onSpeakWord(word) },
                            label = {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.PlayCircleFilled,
                                    contentDescription = "Bunyikan $word",
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AmberContainer.copy(alpha = 0.7f),
                                labelColor = OnAmberContainer
                            ),
                            border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("routine_word_chip_${word.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = AmberContainer.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = routine.clinicalTip,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnAmberContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ParentGoldenRuleCard(
    rule: ParentGoldenRule,
    onSpeakExample: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(rule.id == 1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("golden_rule_card_${rule.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = CoralContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = rule.dontTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = OnCoralContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rule.doTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Buka detail aturan",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Surface(
                        color = CoralContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Hindari Kalimat Ini:",
                                style = MaterialTheme.typography.labelSmall,
                                color = CoralAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = rule.dontExample,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnCoralContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = EmeraldContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gunakan Kalimat Terapi Ini:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold
                                )
                                AssistChip(
                                    onClick = { onSpeakExample("A bi mi num. Sekarang, A bi ma kan! Ma, kan!") },
                                    label = { Text("Contoh Suara", style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.VolumeUp,
                                            contentDescription = "Dengar contoh",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }
                            Text(
                                text = rule.doExample,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnEmeraldContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Alasan Klinis: ${rule.clinicalReason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
