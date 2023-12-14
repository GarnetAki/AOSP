package ru.itmo.notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
class Note(): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
        get() = field

    @ColumnInfo(name = "title")
    var title: String = ""
        get() = field
        set(value) {
            field = value
        }

    @ColumnInfo(name = "pos")
    var pos: Int = 0
        get() = field
        set(value) {
            field = value
        }

    @ColumnInfo(name = "text")
    var text: String = ""
        get() = field
        set(value) {
            field = value
        }

    @ColumnInfo(name = "isDeleted")
    var isDeleted: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    @ColumnInfo(name = "folder")
    var folderId: Int = 0
        get() = field
        set(value) {
            field = value
        }
}