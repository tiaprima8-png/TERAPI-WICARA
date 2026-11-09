package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        RoutineCheckEntity::class,
        PracticePhraseEntity::class,
        ClinicalNoteEntity::class,
        AppStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AbiBicaraDatabase : RoomDatabase() {
    abstract fun abiBicaraDao(): AbiBicaraDao

    companion object {
        @Volatile
        private var INSTANCE: AbiBicaraDatabase? = null

        fun getInstance(context: Context): AbiBicaraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AbiBicaraDatabase::class.java,
                    "abibicara_pro_clinical.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
