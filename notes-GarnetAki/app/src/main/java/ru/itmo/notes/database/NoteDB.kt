package ru.itmo.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import ru.itmo.notes.models.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDB : RoomDatabase() {
    abstract fun noteDAO() : NoteDAO
}