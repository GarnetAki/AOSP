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
import ru.itmo.notes.R
import ru.itmo.notes.clickListeners.ExistFolderListClickListener
import ru.itmo.notes.clickListeners.ExistNoteListClickListener
import ru.itmo.notes.database.NoteDB
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note


class FolderListAdapter(
    val context: Context,
    val list: MutableList<Folder>,
    val listener: ExistFolderListClickListener,
    val databaseNote: NoteDB,
    val existNoteListClickListener: ExistNoteListClickListener
) : RecyclerView.Adapter<FoldersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersViewHolder {
        return FoldersViewHolder(LayoutInflater.from(context).inflate(R.layout.folder_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FoldersViewHolder, position: Int) {
        holder.noteTitle.text = list[position].title
        val notes: MutableList<Note> = databaseNote.noteDAO().getAllByFolder(list[position].id)

        holder.expandButton.setOnClickListener(View.OnClickListener {
            listener.expandFolderClick(list[position])
        })

        if (list[position].title == "Main"){
            holder.deleteButton.visibility = View.INVISIBLE;
        }else{
            holder.deleteButton.setOnClickListener(View.OnClickListener {
                listener.deleteFolderClick(list[holder.layoutPosition])
            })
            holder.notesLayout.setOnClickListener(View.OnClickListener {
                listener.onClick(list[holder.layoutPosition])
            })
        }

        holder.notesLayout.setOnLongClickListener(View.OnLongClickListener{
            listener.onLongClick(list[holder.layoutPosition], holder.notesLayout)
            true
        })

        if (list[position].isExpanded){
            holder.recyclerView.visibility = View.VISIBLE
            holder.recyclerView.setHasFixedSize(true)
            holder.recyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
            var noteListAdapter = NoteListAdapter(context, notes, existNoteListClickListener)
            var notesTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val initial = viewHolder.adapterPosition
                    val final = target.adapterPosition
                    var firstNote = notes.get(initial)
                    var secondNote = notes.get(final)

                    databaseNote.noteDAO().changePos(firstNote.id, final)
                    databaseNote.noteDAO().changePos(secondNote.id, initial)
                    noteListAdapter.notifyItemMoved(initial, final)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }
            })
            holder.recyclerView.adapter = noteListAdapter
            notesTouchHelper.attachToRecyclerView(holder.recyclerView)
        }else{
            holder.recyclerView.visibility = View.INVISIBLE
        }
    }
}

class FoldersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val notesLayout: androidx.cardview.widget.CardView = itemView.findViewById(R.id.folders_layout)
    val noteTitle: TextView = itemView.findViewById(R.id.folderListTitle)
    val deleteButton: ImageButton = itemView.findViewById(R.id.folderListBin)
    val expandButton: ImageButton = itemView.findViewById(R.id.folderListExpand)
    val recyclerView: RecyclerView = itemView.findViewById(R.id.noteRecyclerView)
}