package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_checks")
data class RoutineCheckEntity(
    @PrimaryKey val id: Int,
    val timeSlot: String,       // "PAGI", "SIANG", "SORE", "MALAM"
    val timeLabel: String,      // "07.30 - Habis Mandi & Sarapan"
    val title: String,
    val oralMotorTask: String,
    val speechTask: String,
    val targetWordsCsv: String, // e.g., "Abi makan,Mau lagi"
    val clinicalTip: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "practice_phrases")
data class PracticePhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word1Syllable1: String, // e.g. "A"
    val word1Syllable2: String, // e.g. "BI"
    val word2Syllable1: String, // e.g. "MA"
    val word2Syllable2: String, // e.g. "KAN"
    val fullPhrase: String,     // e.g. "Abi makan"
    val clinicalFocus: String,  // e.g. "Target: Ubah '..kan' jadi 'Ma-kan'"
    val jawInstruction: String, // e.g. "Buka rahang lebar di A dan MA!"
    val isAnchorBase: Boolean = true,
    val clinicalLevel: Int = 2, // 1=25% Ujung kata, 2=50% Vokal bayangan, 3=75% 1 Kata utuh, 4=100% 2 Kata jelas
    val practiceCount: Int = 0,
    val lastPracticedAt: Long? = null,
    val recordedFilePath: String? = null,
    val exampleLevel1: String,  // e.g. "..kan"
    val exampleLevel2: String,  // e.g. "A-kan"
    val exampleLevel3: String,  // e.g. "Ma-kan"
    val exampleLevel4: String   // e.g. "Abi makan"
)

@Entity(tableName = "clinical_notes")
data class ClinicalNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateLabel: String,
    val sessionContext: String, // e.g. "Latihan Sore / Toples Bening"
    val jawStatus: String,      // e.g. "Rahang mulai rileks di vokal A"
    val anchorWordUsed: String, // e.g. "Abi minum -> Abi buka"
    val observationText: String
)

@Entity(tableName = "app_stats")
data class AppStatsEntity(
    @PrimaryKey val id: Int = 1,
    val streakDays: Int = 3,
    val totalJawGymSessions: Int = 4,
    val totalSingingSessions: Int = 5,
    val lastUpdatedDate: String = ""
)
