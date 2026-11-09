package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AbiBicaraDao {

    // Routine Checks
    @Query("SELECT * FROM routine_checks ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<RoutineCheckEntity>>

    @Query("SELECT COUNT(*) FROM routine_checks")
    suspend fun getRoutineCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineCheckEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineCheckEntity)

    @Query("UPDATE routine_checks SET isCompleted = 0, completedAt = NULL")
    suspend fun resetAllRoutines()

    // Practice Phrases (Syllable Tap-Pad & 4-Level Clinical Tracker)
    @Query("SELECT * FROM practice_phrases ORDER BY id ASC")
    fun getAllPhrases(): Flow<List<PracticePhraseEntity>>

    @Query("SELECT COUNT(*) FROM practice_phrases")
    suspend fun getPhraseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrases(phrases: List<PracticePhraseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(phrase: PracticePhraseEntity): Long

    @Update
    suspend fun updatePhrase(phrase: PracticePhraseEntity)

    // Clinical Notes
    @Query("SELECT * FROM clinical_notes ORDER BY timestamp DESC")
    fun getAllClinicalNotes(): Flow<List<ClinicalNoteEntity>>

    @Query("SELECT COUNT(*) FROM clinical_notes")
    suspend fun getClinicalNoteCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinicalNote(note: ClinicalNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinicalNotes(notes: List<ClinicalNoteEntity>)

    @Query("DELETE FROM clinical_notes WHERE id = :noteId")
    suspend fun deleteClinicalNote(noteId: Int)

    // App Stats
    @Query("SELECT * FROM app_stats WHERE id = 1")
    fun getAppStats(): Flow<AppStatsEntity?>

    @Query("SELECT * FROM app_stats WHERE id = 1")
    suspend fun getAppStatsOnce(): AppStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppStats(stats: AppStatsEntity)
}
