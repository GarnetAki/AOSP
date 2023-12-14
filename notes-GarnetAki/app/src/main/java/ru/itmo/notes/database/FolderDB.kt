package ru.itmo.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.itmo.notes.models.Folder

@Database(entities = [Folder::class], version = 1, exportSchema = false)
abstract class FolderDB : RoomDatabase() {
    abstract fun folderDAO() : FolderDAO
}