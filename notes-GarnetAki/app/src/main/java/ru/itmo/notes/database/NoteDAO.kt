package ru.itmo.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import ru.itmo.notes.models.Note

@Dao
interface NoteDAO {

    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY pos ASC")
    fun getAll() : MutableList<Note>

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY pos ASC")
    fun getAllBin() : MutableList<Note>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND folder = :folderId ORDER BY pos ASC")
    fun getAllByFolder(folderId: Int) : MutableList<Note>

    @Query("SELECT * FROM notes WHERE isDeleted = 1 AND folder = :folderId ORDER BY pos ASC")
    fun getAllBinByFolder(folderId: Int) : MutableList<Note>

    @Query("UPDATE notes SET title = :title, text = :text, folder = :folderId WHERE ID = :id")
    fun update(id: Int, title: String, text: String, folderId: Int)

    @Query("UPDATE notes SET pos = :pos WHERE ID = :id")
    fun changePos(id: Int, pos: Int)

    @Query("UPDATE notes SET isDeleted = 1 WHERE ID = :id")
    fun moveToBin(id: Int)

    @Query("UPDATE notes SET isDeleted = 1 WHERE folder = :id")
    fun moveChildrenToBin(id: Int)

    @Query("UPDATE notes SET isDeleted = 0 WHERE folder = :id")
    fun restoreChildrenFromBin(id: Int)

    @Query("UPDATE notes SET isDeleted = 0 WHERE ID = :id")
    fun restoreFromBin(id: Int)

    @Delete()
    fun delete(note: Note)
}