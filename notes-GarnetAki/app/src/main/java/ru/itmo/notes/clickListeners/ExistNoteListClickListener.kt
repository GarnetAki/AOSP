package ru.itmo.notes.clickListeners

import androidx.cardview.widget.CardView
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note
import java.io.Serializable

interface ExistNoteListClickListener {
    fun onClick(note: Note)

    fun onLongClick(note: Note, view: CardView)

    fun deleteNoteClick(note: Note)
}