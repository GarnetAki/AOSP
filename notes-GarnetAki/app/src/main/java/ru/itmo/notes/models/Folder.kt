package ru.itmo.notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "folders", indices = [Index(value = ["title"], unique = true)])
class Folder(): Serializable {
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

    @ColumnInfo(name = "isExpanded")
    var isExpanded: Boolean = false
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
}