package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClinicalNoteEntity
import com.example.data.local.PracticePhraseEntity
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Level1Color
import com.example.ui.theme.Level2Color
import com.example.ui.theme.Level3Color
import com.example.ui.theme.Level4Color
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer

@Composable
fun ClinicalProgressScreen(
    phrases: List<PracticePhraseEntity>,
    clinicalNotes: List<ClinicalNoteEntity>,
    onUpdatePhraseLevel: (PracticePhraseEntity, Int) -> Unit,
    onAddClinicalNote: (String, String, String, String) -> Unit,
    onDeleteClinicalNote: (Int) -> Unit,
    onGenerateReportText: () -> String
) {
    val context = LocalContext.current
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var exportedReportText by remember { mutableStateOf<String?>(null) }
    var copyFeedback by remember { mutableStateOf(false) }

    val totalCount = phrases.size.coerceAtLeast(1)
    val lv1Count = phrases.count { it.clinicalLevel == 1 }
    val lv2Count = phrases.count { it.clinicalLevel == 2 }
    val lv3Count = phrases.count { it.clinicalLevel == 3 }
    val lv4Count = phrases.count { it.clinicalLevel == 4 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("clinical_progress_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Export Clinical Report Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assessment,
                            contentDescription = null,
                            tint = AmberBright,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rekam Medis & Tracker 4 Level",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Text(
                                text = "Evaluasi Mengatasi Initial Syllable Deletion & 2 Kata",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            copyFeedback = false
                            exportedReportText = onGenerateReportText()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("export_report_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberBright,
                            contentColor = OnAmberContainer
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.IosShare, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Export Laporan Progress (Untuk Terapis Wicara)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. 4-Level Clinical Speech Evaluation Legend & Visual Distribution Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Grafik Distribusi 4 Level Klinis Terapi Wicara",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ClinicalLevelDistributionBar(
                        levelTitle = "Level 4 (100% • Rangkaian 2 Kata Jelas & Spontan)",
                        subtitle = "Contoh: \"Abi makan\", \"Abi buka\" tanpa dipancing paksa",
                        count = lv4Count,
                        total = totalCount,
                        color = Level4Color
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ClinicalLevelDistributionBar(
                        levelTitle = "Level 3 (75% • Kata Utuh 1 Kata 2 Suku Kata)",
                        subtitle = "Contoh: \"Ma-kan\", \"Bu-ka\" dengan bantuan ketukan",
                        count = lv3Count,
                        total = totalCount,
                        color = Level3Color
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ClinicalLevelDistributionBar(
                        levelTitle = "Level 2 (50% • Muncul Vokal Bayangan Depan)",
                        subtitle = "Rahang mulai turun di awal: \"A-kan\", \"U-ka\", \"O-bil\"",
                        count = lv2Count,
                        total = totalCount,
                        color = Level2Color
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ClinicalLevelDistributionBar(
                        levelTitle = "Level 1 (25% • Hanya Ujung Kata / Initial Deletion)",
                        subtitle = "Masih memotong suku kata depan: \"..kan\", \"..ka\", \"..bil\"",
                        count = lv1Count,
                        total = totalCount,
                        color = Level1Color
                    )
                }
            }
        }

        // 3. Per-Word Clinical Level Matrix
        item {
            Text(
                text = "Status Level Setiap Target Kombinasi 2 Kata:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(phrases, key = { it.id }) { phrase ->
            PhraseClinicalProgressRow(
                phrase = phrase,
                onSelectLevel = { lv -> onUpdatePhraseLevel(phrase, lv) }
            )
        }

        // 4. Weekly Observation Notes Section (Catatan Observasi Mingguan)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Catatan Observasi Mingguan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Catatan perkembangan rahang & artikulasi untuk dilaporkan ke terapis",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = { showAddNoteDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldContainer,
                        contentColor = OnEmeraldContainer
                    ),
                    modifier = Modifier.testTag("add_clinical_note_button")
                ) {
                    Icon(Icons.Filled.AddComment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Catat", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        items(clinicalNotes, key = { it.id }) { note ->
            ClinicalObservationNoteCard(
                note = note,
                onDelete = { onDeleteClinicalNote(note.id) }
            )
        }
    }

    // Add Observation Note Dialog
    if (showAddNoteDialog) {
        AddObservationNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onSave = { ctx, jaw, anchor, text ->
                onAddClinicalNote(ctx, jaw, anchor, text)
                showAddNoteDialog = false
            }
        )
    }

    // Export Clinical Report Dialog
    if (exportedReportText != null) {
        AlertDialog(
            onDismissRequest = { exportedReportText = null },
            title = {
                Text(
                    text = "Laporan Klinis AbiBicara Pro",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column {
                    Surface(
                        color = EmeraldContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            item {
                                Text(
                                    text = exportedReportText ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    if (copyFeedback) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Laporan berhasil disalin ke Clipboard!",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(
                                ClipData.newPlainText("Laporan AbiBicara Pro", exportedReportText)
                            )
                            copyFeedback = true
                        },
                        modifier = Modifier.testTag("copy_report_button")
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Salin")
                    }
                    Button(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Laporan Terapi Wicara AbiBicara Pro")
                                putExtra(Intent.EXTRA_TEXT, exportedReportText)
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, "Bagikan Laporan ke Terapis Wicara")
                            )
                        },
                        modifier = Modifier.testTag("share_report_button")
                    ) {
                        Icon(Icons.Filled.IosShare, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Bagikan")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { exportedReportText = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun ClinicalLevelDistributionBar(
    levelTitle: String,
    subtitle: String,
    count: Int,
    total: Int,
    color: Color
) {
    val ratio = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = levelTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "$count Kata",
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun PhraseClinicalProgressRow(
    phrase: PracticePhraseEntity,
    onSelectLevel: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_phrase_card_${phrase.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${phrase.fullPhrase} (${phrase.word1Syllable1}-${phrase.word1Syllable2} + ${phrase.word2Syllable1}-${phrase.word2Syllable2})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${phrase.clinicalFocus} • Dilatih ${phrase.practiceCount}x",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = AmberContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "${phrase.clinicalLevel * 25}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnAmberContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val options = listOf(
                    Triple(1, "Lv1: ${phrase.exampleLevel1}", Level1Color),
                    Triple(2, "Lv2: ${phrase.exampleLevel2}", Level2Color),
                    Triple(3, "Lv3: ${phrase.exampleLevel3}", Level3Color),
                    Triple(4, "Lv4: ${phrase.exampleLevel4}", Level4Color)
                )
                options.forEach { (lv, label, col) ->
                    val active = phrase.clinicalLevel == lv
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectLevel(lv) }
                            .testTag("progress_phrase_${phrase.id}_lv_$lv"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (active) col else col.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, col)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (active) Color.White else col,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClinicalObservationNoteCard(
    note: ClinicalNoteEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clinical_note_card_${note.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.dateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = note.sessionContext,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Hapus catatan",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = EmeraldContainer, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "Rahang: ${note.jawStatus}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnEmeraldContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Surface(color = AmberContainer, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "Jangkar: ${note.anchorWordUsed}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnAmberContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.observationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AddObservationNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var sessionContext by remember { mutableStateOf("Latihan Sore / Sabotase Toples") }
    var jawStatus by remember { mutableStateOf("Rahang mulai turun di suku kata awal") }
    var anchorUsed by remember { mutableStateOf("Abi minum -> Abi buka") }
    var observation by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Tambah Catatan Observasi Klinis", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sessionContext,
                    onValueChange = { sessionContext = it },
                    label = { Text("Waktu / Aktivitas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = jawStatus,
                    onValueChange = { jawStatus = it },
                    label = { Text("Kondisi Bukaan Rahang (TMJ)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = anchorUsed,
                    onValueChange = { anchorUsed = it },
                    label = { Text("Kata Jangkar yang Dipakai") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = observation,
                    onValueChange = { observation = it },
                    label = { Text("Detail Respons Bicara Adik") },
                    placeholder = { Text("Misal: Adik berhasil bilang 'A-bi Bu-ka' setelah dipancing ketukan...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalObs = observation.ifBlank {
                        "Adik merespons positif terhadap pancingan kata jangkar $anchorUsed dengan bukaan rahang lebih lebar."
                    }
                    onSave(sessionContext, jawStatus, anchorUsed, finalObs)
                },
                modifier = Modifier.testTag("save_clinical_note_button")
            ) {
                Text("Simpan Catatan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
