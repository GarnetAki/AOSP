package ru.itmo.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import ru.itmo.notes.models.Folder

@Dao
interface FolderDAO {

    @Insert(onConflict = REPLACE)
    fun insert(folder: Folder)

    @Query("SELECT * FROM folders WHERE isDeleted = 0 ORDER BY pos ASC")
    fun getAll() : MutableList<Folder>

    @Query("SELECT * FROM folders WHERE isDeleted = 1 ORDER BY pos ASC")
    fun getAllBin() : MutableList<Folder>

    @Query("SELECT * FROM folders WHERE title = :title")
    fun getByTitle(title: String) : Folder?

    @Query("SELECT * FROM folders WHERE id = :id")
    fun getById(id: Int) : Folder?

    @Query("UPDATE folders SET pos = :pos WHERE id = :id")
    fun changePos(id: Int, pos: Int)

    @Query("UPDATE folders SET title = :title WHERE id = :id")
    fun update(id: Int, title: String)

    @Query("UPDATE folders SET isExpanded = 0 WHERE id = :id")
    fun setExpandedFalse(id: Int)

    @Query("UPDATE folders SET isExpanded = 1 WHERE id = :id")
    fun setExpandedTrue(id: Int)

    @Query("UPDATE folders SET isDeleted = 1 WHERE id = :id")
    fun moveToBin(id: Int)

    @Query("UPDATE folders SET isDeleted = 0 WHERE id = :id")
    fun restoreFromBin(id: Int)

    @Delete()
    fun delete(folder: Folder)
}