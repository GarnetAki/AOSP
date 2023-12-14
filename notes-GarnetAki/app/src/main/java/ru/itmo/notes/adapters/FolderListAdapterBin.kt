package ru.itmo.notes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ru.itmo.notes.MainActivity
import ru.itmo.notes.R
import ru.itmo.notes.clickListeners.BinFolderListClickListener
import ru.itmo.notes.clickListeners.BinNoteListClickListener
import ru.itmo.notes.clickListeners.ExistFolderListClickListener
import ru.itmo.notes.clickListeners.ExistNoteListClickListener
import ru.itmo.notes.database.NoteDB
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note


class FolderListAdapterBin(
    val context: Context,
    val list: MutableList<Folder>,
    val listener: BinFolderListClickListener,
    val databaseNote: NoteDB,
    val binNoteListClickListener: BinNoteListClickListener
) : RecyclerView.Adapter<FoldersViewHolderBin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersViewHolderBin {
        return FoldersViewHolderBin(LayoutInflater.from(context).inflate(R.layout.folder_list_bin, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FoldersViewHolderBin, position: Int) {
        holder.noteTitle.text = list[position].title
        val notes: MutableList<Note> = databaseNote.noteDAO().getAllBinByFolder(list[position].id)

        holder.restoreButton.setOnClickListener(View.OnClickListener {
            listener.restoreFolderClick(list[holder.layoutPosition])
        })

        holder.deleteButton.setOnClickListener(View.OnClickListener {
            listener.deleteFolderClick(list[holder.layoutPosition])
        })

        holder.recyclerView.setHasFixedSize(true)
        holder.recyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        var noteListAdapter = NoteListAdapterBin(context, notes, binNoteListClickListener)
        holder.recyclerView.adapter = noteListAdapter
    }
}

class FoldersViewHolderBin(itemView: View) : RecyclerView.ViewHolder(itemView){
    val notesLayout: androidx.cardview.widget.CardView = itemView.findViewById(R.id.folders_layout)
    val noteTitle: TextView = itemView.findViewById(R.id.folderListTitle)
    val deleteButton: ImageButton = itemView.findViewById(R.id.folderListBin)
    val restoreButton: ImageButton = itemView.findViewById(R.id.folderListRestore)
    val recyclerView: RecyclerView = itemView.findViewById(R.id.noteRecyclerView)
}