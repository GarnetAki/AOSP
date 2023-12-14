package ru.itmo.notes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.itmo.notes.R
import ru.itmo.notes.clickListeners.ExistFolderListClickListener
import ru.itmo.notes.clickListeners.ExistNoteListClickListener
import ru.itmo.notes.models.Note


class NoteListAdapter(
    val context: Context,
    val list: MutableList<Note>,
    val listener: ExistNoteListClickListener
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.noteTitle.text = list[position] .title
        holder.noteText.text = list[position] .text
        holder.deleteButton.setOnClickListener(View.OnClickListener {
            listener.deleteNoteClick(list[holder.layoutPosition] as Note)
        })

        holder.notesLayout.setOnClickListener(View.OnClickListener {
            listener.onClick(list[holder.layoutPosition])
        })

        holder.notesLayout.setOnLongClickListener(View.OnLongClickListener{
            listener.onLongClick(list[holder.layoutPosition], holder.notesLayout)
            true
        })
    }
}

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val notesLayout: androidx.cardview.widget.CardView = itemView.findViewById(R.id.notes_layout)
    val noteTitle: TextView = itemView.findViewById(R.id.noteListTitle)
    val noteText: TextView = itemView.findViewById(R.id.noteListText)
    val deleteButton: ImageButton = itemView.findViewById(R.id.noteListBin)
}