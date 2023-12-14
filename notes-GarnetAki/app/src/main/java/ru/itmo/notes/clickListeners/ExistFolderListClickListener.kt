package ru.itmo.notes.clickListeners

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note
import java.io.Serializable

interface ExistFolderListClickListener {
    fun onClick(folder: Folder)

    fun onLongClick(folder: Folder, view: CardView)

    fun expandFolderClick(folder: Folder)

    fun deleteFolderClick(folder: Folder)
}