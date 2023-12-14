package ru.itmo.notes.clickListeners

import androidx.cardview.widget.CardView
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note
import java.io.Serializable

interface BinNoteListClickListener {

    fun restoreNoteClick(note: Note)

    fun deleteNoteClick(note: Note)
}