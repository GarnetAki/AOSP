package ru.itmo.notes.clickListeners

import androidx.cardview.widget.CardView
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note
import java.io.Serializable

interface BinFolderListClickListener {

    fun restoreFolderClick(folder: Folder)

    fun deleteFolderClick(folder: Folder)
}