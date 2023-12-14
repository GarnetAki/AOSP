package ru.itmo.notes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.itmo.notes.R
import ru.itmo.notes.clickListeners.BinNoteListClickListener
import ru.itmo.notes.models.Note


class NoteListAdapterBin(
    val context: Context,
    val list: MutableList<Note>,
    val listener: BinNoteListClickListener
) : RecyclerView.Adapter<NotesViewHolderBin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolderBin {
        return NotesViewHolderBin(LayoutInflater.from(context).inflate(R.layout.notes_list_bin, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotesViewHolderBin, position: Int) {
        holder.noteTitle.text = list[position] .title
        holder.noteText.text = list[position] .text
        holder.deleteButton.setOnClickListener(View.OnClickListener {
            listener.deleteNoteClick(list[holder.layoutPosition])
        })
        holder.restoreButton.setOnClickListener(View.OnClickListener {
            listener.restoreNoteClick(list[holder.layoutPosition])
        })
    }
}

class NotesViewHolderBin(itemView: View) : RecyclerView.ViewHolder(itemView){
    val notesLayout: androidx.cardview.widget.CardView = itemView.findViewById(R.id.notes_layout)
    val noteTitle: TextView = itemView.findViewById(R.id.noteListTitle)
    val noteText: TextView = itemView.findViewById(R.id.noteListText)
    val restoreButton: ImageButton = itemView.findViewById(R.id.noteListRestore)
    val deleteButton: ImageButton = itemView.findViewById(R.id.noteListBin)
}