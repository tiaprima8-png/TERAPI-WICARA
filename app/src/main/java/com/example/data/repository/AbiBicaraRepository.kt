package com.example.data.repository

import com.example.data.local.AbiBicaraDao
import com.example.data.local.AppStatsEntity
import com.example.data.local.ClinicalNoteEntity
import com.example.data.local.PracticePhraseEntity
import com.example.data.local.RoutineCheckEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AbiBicaraRepository(private val dao: AbiBicaraDao) {

    val routinesFlow: Flow<List<RoutineCheckEntity>> = dao.getAllRoutines()
    val phrasesFlow: Flow<List<PracticePhraseEntity>> = dao.getAllPhrases()
    val clinicalNotesFlow: Flow<List<ClinicalNoteEntity>> = dao.getAllClinicalNotes()
    val appStatsFlow: Flow<AppStatsEntity?> = dao.getAppStats()

    suspend fun ensureSeeded() {
        if (dao.getRoutineCount() == 0) {
            dao.insertRoutines(defaultRoutines())
        }
        if (dao.getPhraseCount() == 0) {
            dao.insertPhrases(defaultPracticePhrases())
        }
        if (dao.getClinicalNoteCount() == 0) {
            dao.insertClinicalNotes(defaultClinicalNotes())
        }
        if (dao.getAppStatsOnce() == null) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            dao.insertAppStats(
                AppStatsEntity(
                    id = 1,
                    streakDays = 4,
                    totalJawGymSessions = 6,
                    totalSingingSessions = 8,
                    lastUpdatedDate = today
                )
            )
        }
    }

    suspend fun toggleRoutine(routine: RoutineCheckEntity) {
        val updated = routine.copy(
            isCompleted = !routine.isCompleted,
            completedAt = if (!routine.isCompleted) System.currentTimeMillis() else null
        )
        dao.updateRoutine(updated)
    }

    suspend fun resetDailyRoutines() {
        dao.resetAllRoutines()
    }

    suspend fun recordPhrasePractice(phrase: PracticePhraseEntity, audioPath: String? = null) {
        val updated = phrase.copy(
            practiceCount = phrase.practiceCount + 1,
            lastPracticedAt = System.currentTimeMillis(),
            recordedFilePath = audioPath ?: phrase.recordedFilePath
        )
        dao.updatePhrase(updated)
    }

    suspend fun updatePhraseLevel(phrase: PracticePhraseEntity, newLevel: Int) {
        val coerced = newLevel.coerceIn(1, 4)
        dao.updatePhrase(
            phrase.copy(
                clinicalLevel = coerced,
                lastPracticedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addCustomPhrase(
        w1s1: String,
        w1s2: String,
        w2s1: String,
        w2s2: String,
        focus: String
    ) {
        val s11 = w1s1.trim().uppercase()
        val s12 = w1s2.trim().uppercase()
        val s21 = w2s1.trim().uppercase()
        val s22 = w2s2.trim().uppercase()
        val word1 = (s11 + s12).lowercase().replaceFirstChar { it.uppercase() }
        val word2 = (s21 + s22).lowercase()
        val full = "$word1 $word2"
        val newPhrase = PracticePhraseEntity(
            word1Syllable1 = s11,
            word1Syllable2 = s12,
            word2Syllable1 = s21,
            word2Syllable2 = s22,
            fullPhrase = full,
            clinicalFocus = focus.ifBlank { "Latihan penekanan suku kata awal $s21-$s22" },
            jawInstruction = "Buka rahang lebar saat ketuk $s11 dan $s21!",
            isAnchorBase = s11 == "A" && s12 == "BI",
            clinicalLevel = 1,
            practiceCount = 0,
            exampleLevel1 = "..${s22.lowercase()}",
            exampleLevel2 = "A-${s22.lowercase()}",
            exampleLevel3 = "${s21.lowercase().replaceFirstChar { it.uppercase() }}-${s22.lowercase()}",
            exampleLevel4 = full
        )
        dao.insertPhrase(newPhrase)
    }

    suspend fun addClinicalNote(
        sessionContext: String,
        jawStatus: String,
        anchorWordUsed: String,
        observationText: String
    ) {
        val dateLabel = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        dao.insertClinicalNote(
            ClinicalNoteEntity(
                timestamp = System.currentTimeMillis(),
                dateLabel = dateLabel,
                sessionContext = sessionContext,
                jawStatus = jawStatus,
                anchorWordUsed = anchorWordUsed,
                observationText = observationText
            )
        )
    }

    suspend fun deleteClinicalNote(id: Int) {
        dao.deleteClinicalNote(id)
    }

    suspend fun incrementJawGymSession() {
        val current = dao.getAppStatsOnce() ?: AppStatsEntity()
        dao.insertAppStats(current.copy(totalJawGymSessions = current.totalJawGymSessions + 1))
    }

    suspend fun incrementSingingSession() {
        val current = dao.getAppStatsOnce() ?: AppStatsEntity()
        dao.insertAppStats(current.copy(totalSingingSessions = current.totalSingingSessions + 1))
    }

    private fun defaultRoutines(): List<RoutineCheckEntity> = listOf(
        RoutineCheckEntity(
            id = 1,
            timeSlot = "PAGI",
            timeLabel = "07.30 • Habis Mandi & Sarapan",
            title = "Pemanasan Sendi Rahang & Kunyah Kenyal",
            oralMotorTask = "Pijat ekstra-oral memutar di pipi & sendi rahang (TMJ) + mengunyah makanan bertekstur kenyal di geraham samping kiri & kanan.",
            speechTask = "Gunakan kata jangkar 'Abi' untuk memancing kata 'makan' dan 'lagi' saat suapan sarapan.",
            targetWordsCsv = "Abi makan,Mau lagi",
            clinicalTip = "Pegang sendok sejajar mulut Abi selama 3 detik agar Adik melihat bukaan rahang MA-kan.",
            isCompleted = true
        ),
        RoutineCheckEntity(
            id = 2,
            timeSlot = "SIANG",
            timeLabel = "12.30 • Sebelum Tidur Siang",
            title = "Terapi Sedotan Pendek & Lagu Kakaktua",
            oralMotorTask = "Minum air/susu dengan sedotan pendek (melatih kekuatan bibir depan/orbicularis oris) agar tidak menggigit sedotan.",
            speechTask = "Bernyanyi 'Burung Kakaktua' tempo lambat dengan teknik jeda menggantung (Cloze Procedure).",
            targetWordsCsv = "Bu-rung,Tu-a,Abi bobo",
            clinicalTip = "Berhenti mendadak sebelum kata 'TU-A' dan beri waktu 3 detik sambil tersenyum.",
            isCompleted = false
        ),
        RoutineCheckEntity(
            id = 3,
            timeSlot = "SORE",
            timeLabel = "16.30 • Bermain Fisik & Motorik",
            title = "Lompat Ubin 2 Suku Kata & Toples Bening",
            oralMotorTask = "Lompat 2 ubin sambil tepuk paha ritmis (1 lompatan = 1 suku kata) untuk membangun memori ritme 2 suku kata.",
            speechTask = "Teknik Sabotase Komunikasi: Masukkan mobil/mainan favorit ke dalam toples bening tertutup rapat.",
            targetWordsCsv = "Abi buka,Buka pintu,Mobil jalan",
            clinicalTip = "Saat Adik menyerahkan toples dan bilang '..ka', gunakan Recasting: 'Iya! BU-KA! A-bi BU-KA!'",
            isCompleted = false
        ),
        RoutineCheckEntity(
            id = 4,
            timeSlot = "MALAM",
            timeLabel = "19.30 • Sebelum Tidur Malam",
            title = "Permainan 'Abi Pura-pura' & Penguatan Jangkar",
            oralMotorTask = "Relaksasi rahang sambil menguap lebar buatan ('Haaaah ngantuk...') untuk melepas ketegangan masseter.",
            speechTask = "Pengulangan santai tanpa beban menggunakan kata jangkar terkuat anak sambil minum air hangat.",
            targetWordsCsv = "Abi minum,Abis minum",
            clinicalTip = "Akhiri hari dengan pujian hangat setiap kali Adik membuka mulut di suku kata pertama.",
            isCompleted = false
        )
    )

    private fun defaultPracticePhrases(): List<PracticePhraseEntity> = listOf(
        PracticePhraseEntity(
            id = 1,
            word1Syllable1 = "A",
            word1Syllable2 = "BI",
            word2Syllable1 = "MI",
            word2Syllable2 = "NUM",
            fullPhrase = "Abi minum",
            clinicalFocus = "Kata Jangkar Utama (Modal terkuat Adik!)",
            jawInstruction = "Vokal A membuka rahang lebar, lalu katupkan bibir di BI dan MI.",
            isAnchorBase = true,
            clinicalLevel = 4,
            practiceCount = 12,
            exampleLevel1 = "..num",
            exampleLevel2 = "I-num",
            exampleLevel3 = "Mi-num",
            exampleLevel4 = "Abi minum"
        ),
        PracticePhraseEntity(
            id = 2,
            word1Syllable1 = "A",
            word1Syllable2 = "BI",
            word2Syllable1 = "MA",
            word2Syllable2 = "KAN",
            fullPhrase = "Abi makan",
            clinicalFocus = "Target Utama: Ubah '..kan' menjadi 'Ma-kan'",
            jawInstruction = "Gunakan bukaan rahang dari 'A-bi' untuk langsung turun lebar di 'MA'!",
            isAnchorBase = true,
            clinicalLevel = 2,
            practiceCount = 6,
            exampleLevel1 = "..kan",
            exampleLevel2 = "A-kan",
            exampleLevel3 = "Ma-kan",
            exampleLevel4 = "Abi makan"
        ),
        PracticePhraseEntity(
            id = 3,
            word1Syllable1 = "A",
            word1Syllable2 = "BI",
            word2Syllable1 = "BU",
            word2Syllable2 = "KA",
            fullPhrase = "Abi buka",
            clinicalFocus = "Target Utama: Ubah '..ka' menjadi 'Bu-ka'",
            jawInstruction = "Setelah bibir rapat di 'BI', langsung monyongkan sedikit di 'BU' lalu buka lebar di 'KA'!",
            isAnchorBase = true,
            clinicalLevel = 2,
            practiceCount = 5,
            exampleLevel1 = "..ka",
            exampleLevel2 = "U-ka",
            exampleLevel3 = "Bu-ka",
            exampleLevel4 = "Abi buka"
        ),
        PracticePhraseEntity(
            id = 4,
            word1Syllable1 = "A",
            word1Syllable2 = "BI",
            word2Syllable1 = "BO",
            word2Syllable2 = "BO",
            fullPhrase = "Abi bobo",
            clinicalFocus = "Latihan bilabial B + vokal bulat O",
            jawInstruction = "Katupkan bibir dengan santai 'BO - BO', rahang turun setengah bulat.",
            isAnchorBase = true,
            clinicalLevel = 3,
            practiceCount = 7,
            exampleLevel1 = "..bo",
            exampleLevel2 = "O-bo",
            exampleLevel3 = "Bo-bo",
            exampleLevel4 = "Abi bobo"
        ),
        PracticePhraseEntity(
            id = 5,
            word1Syllable1 = "A",
            word1Syllable2 = "BI",
            word2Syllable1 = "MAN",
            word2Syllable2 = "DI",
            fullPhrase = "Abi mandi",
            clinicalFocus = "Latihan bilabial M + vokal A terbuka lebar",
            jawInstruction = "Rapatkan bibir 'Mmm' lalu buka rahang lebar 'MAN - DI'!",
            isAnchorBase = true,
            clinicalLevel = 2,
            practiceCount = 4,
            exampleLevel1 = "..di",
            exampleLevel2 = "An-di",
            exampleLevel3 = "Man-di",
            exampleLevel4 = "Abi mandi"
        ),
        PracticePhraseEntity(
            id = 6,
            word1Syllable1 = "MA",
            word1Syllable2 = "U",
            word2Syllable1 = "MI",
            word2Syllable2 = "NUM",
            fullPhrase = "Mau minum",
            clinicalFocus = "Tarik kata 'Ma-u' di depan jangkar 'Mi-num'",
            jawInstruction = "Buka rahang lebar di 'MA', bulatkan bibir di 'U', lalu sambung 'MI-NUM'.",
            isAnchorBase = false,
            clinicalLevel = 3,
            practiceCount = 8,
            exampleLevel1 = "..num",
            exampleLevel2 = "Au minum",
            exampleLevel3 = "Mau ..num",
            exampleLevel4 = "Mau minum"
        ),
        PracticePhraseEntity(
            id = 7,
            word1Syllable1 = "A",
            word1Syllable2 = "BIS",
            word2Syllable1 = "MI",
            word2Syllable2 = "NUM",
            fullPhrase = "Abis minum",
            clinicalFocus = "Jembatan artikulasi: 'A-bis' sangat mirip pola mulut 'A-bi'",
            jawInstruction = "Mulai persis seperti 'A-bi' lalu beri desis lembut 's' di ujung 'A-BIS'!",
            isAnchorBase = true,
            clinicalLevel = 4,
            practiceCount = 9,
            exampleLevel1 = "..bis minum",
            exampleLevel2 = "A-bi minum",
            exampleLevel3 = "A-bis",
            exampleLevel4 = "Abis minum"
        ),
        PracticePhraseEntity(
            id = 8,
            word1Syllable1 = "MA",
            word1Syllable2 = "U",
            word2Syllable1 = "SU",
            word2Syllable2 = "SU",
            fullPhrase = "Mau susu",
            clinicalFocus = "Latihan rahang turun di 'Ma-u' sebelum vokal bulat 'Su-su'",
            jawInstruction = "Turunkan dagu lebar saat ketuk tombol 'MA'!",
            isAnchorBase = false,
            clinicalLevel = 2,
            practiceCount = 4,
            exampleLevel1 = "..su",
            exampleLevel2 = "A-u ..su",
            exampleLevel3 = "Su-su",
            exampleLevel4 = "Mau susu"
        ),
        PracticePhraseEntity(
            id = 9,
            word1Syllable1 = "BU",
            word1Syllable2 = "RUNG",
            word2Syllable1 = "TU",
            word2Syllable2 = "A",
            fullPhrase = "Burung tua",
            clinicalFocus = "Jangkar Lagu Favorit 'Burung Kakaktua'",
            jawInstruction = "Bibir maju di 'BU' dan 'TU', lalu buka rahang 100% di ujung 'A'!",
            isAnchorBase = false,
            clinicalLevel = 3,
            practiceCount = 10,
            exampleLevel1 = "..ung ..a",
            exampleLevel2 = "U-rung u-a",
            exampleLevel3 = "Tu-a",
            exampleLevel4 = "Burung tua"
        ),
        PracticePhraseEntity(
            id = 10,
            word1Syllable1 = "MO",
            word1Syllable2 = "BIL",
            word2Syllable1 = "JA",
            word2Syllable2 = "LAN",
            fullPhrase = "Mobil jalan",
            clinicalFocus = "Target Utama: Ubah '..bil' menjadi 'Mo-bil'",
            jawInstruction = "Bulatkan bibir & turunkan rahang di 'MO', lalu buka lebar di 'JA'!",
            isAnchorBase = false,
            clinicalLevel = 1,
            practiceCount = 3,
            exampleLevel1 = "..bil ..lan",
            exampleLevel2 = "O-bil a-lan",
            exampleLevel3 = "Mo-bil",
            exampleLevel4 = "Mobil jalan"
        )
    )

    private fun defaultClinicalNotes(): List<ClinicalNoteEntity> = listOf(
        ClinicalNoteEntity(
            id = 1,
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            dateLabel = "2 Hari Lalu • Sesi Sore",
            sessionContext = "Permainan Toples Bening & Mobil-mobilan",
            jawStatus = "Muncul vokal bayangan awal (Level 2)",
            anchorWordUsed = "Abi minum -> Abi buka",
            observationText = "Saat dipancing setelah mengucapkan 'Abi minum', Adik mulai menurunkan dagu di awal kata menjadi 'A-bi U-ka' (sebelumnya hanya '..ka')."
        ),
        ClinicalNoteEntity(
            id = 2,
            timestamp = System.currentTimeMillis() - 86400000L,
            dateLabel = "Kemarin • Sesi Siang",
            sessionContext = "Bernyanyi Burung Kakaktua (Cloze Procedure)",
            jawStatus = "Rahang terbuka lebar & rileks",
            anchorWordUsed = "Lagu Burung Kakaktua",
            observationText = "Sangat antusias saat Abi berhenti mendadak selama 3 detik. Adik langsung menyambung 'TU-AAA!' dengan rahang terbuka lebar dan suara lantang."
        )
    )
}
