package ru.itmo.notes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import ru.itmo.notes.adapters.FolderListAdapter
import ru.itmo.notes.adapters.FolderListAdapterBin
import ru.itmo.notes.adapters.NoteListAdapterBin
import ru.itmo.notes.clickListeners.BinFolderListClickListener
import ru.itmo.notes.clickListeners.BinNoteListClickListener
import ru.itmo.notes.clickListeners.ExistFolderListClickListener
import ru.itmo.notes.clickListeners.ExistNoteListClickListener
import ru.itmo.notes.database.FolderDB
import ru.itmo.notes.database.NoteDB
import ru.itmo.notes.models.Folder
import ru.itmo.notes.models.Note

class MainActivity : ComponentActivity() {
    var notes: MutableList<Note> = ArrayList()
    var folders: MutableList<Folder> = ArrayList()
    lateinit var recyclerView: RecyclerView
    lateinit var folderListAdapter: FolderListAdapter
    lateinit var databaseNotes: NoteDB
    lateinit var databaseFolders: FolderDB
    lateinit var tmp: Context
    var isRedact: Boolean = false
    var objectToRedact: Int = 0
    var search: String = ""

    private var existFolderListClickListener = object: ExistFolderListClickListener{

        override fun onClick(folder: Folder) {
            val title = folder.title
            setContentView(R.layout.add_folder)
            val titleObj: EditText = findViewById(R.id.folderTitle)
            titleObj.setText(title)
            isRedact = true
            objectToRedact = folder.id
        }

        override fun onLongClick(folder: Folder, view: CardView) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun deleteFolderClick(folder: Folder) {
            databaseFolders.folderDAO().moveToBin(folder.id)
            databaseNotes.noteDAO().moveChildrenToBin(folder.id)
            recyclerView = findViewById(R.id.recyclerView)
            folders = databaseFolders.folderDAO().getAll()
            folderListAdapter.notifyDataSetChanged()
            updateMainView()

        }

        @SuppressLint("NotifyDataSetChanged")
        override fun expandFolderClick(folder: Folder) {
            folders = databaseFolders.folderDAO().getAll()
            updateMainView()
            if (databaseFolders.folderDAO().getById(folder.id)!!.isExpanded){
                databaseFolders.folderDAO().setExpandedFalse(folder.id)
            }
            else{
                databaseFolders.folderDAO().setExpandedTrue(folder.id)
            }
            folders = databaseFolders.folderDAO().getAll()
            updateMainView()
        }
    }

    private var binFolderListClickListener = object: BinFolderListClickListener {

        @SuppressLint("NotifyDataSetChanged")
        override fun restoreFolderClick(folder: Folder) {
            databaseNotes.noteDAO().restoreChildrenFromBin(folder.id)
            databaseFolders.folderDAO().restoreFromBin(folder.id)
            updateBinView()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun deleteFolderClick(folder: Folder) {
            for (note in databaseNotes.noteDAO().getAllBinByFolder(folder.id))
                databaseNotes.noteDAO().delete(note)

            databaseFolders.folderDAO().delete(folder)
            updateBinView()
        }
    }

    private var existNoteListClickListener = object: ExistNoteListClickListener {

        override fun onClick(note: Note) {
            val title = note.title
            val text = note.text
            val folderId = note.folderId
            setContentView(R.layout.add_note)
            val titleObj: EditText = findViewById(R.id.noteTitle)
            val textObj: EditText = findViewById(R.id.noteText)
            val spinner: Spinner = findViewById(R.id.static_spinner)
            titleObj.setText(title)
            textObj.setText(text)
            val adapter: ArrayAdapter<String> = ArrayAdapter(tmp, R.layout.spinner_item, folders.map{ x -> x.title })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(adapter.getPosition(databaseFolders.folderDAO().getById(folderId)!!.title))
            isRedact = true
            objectToRedact = note.id
        }

        override fun onLongClick(note: Note, view: CardView) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun deleteNoteClick(note: Note) {
            databaseNotes.noteDAO().moveToBin(note.id)
            recyclerView = findViewById(R.id.recyclerView)
            notes = databaseNotes.noteDAO().getAll()
            folderListAdapter.notifyDataSetChanged()
            updateMainView()
        }
    }

    private var binNoteListClickListener = object: BinNoteListClickListener {

        override fun restoreNoteClick(note: Note) {
            if (databaseFolders.folderDAO().getById(note.folderId)!!.isDeleted){
                databaseNotes.noteDAO().update(note.id, note.title, note.text,  databaseFolders.folderDAO().getByTitle("Main")!!.id)
                databaseNotes.noteDAO().restoreFromBin(note.id)
            }else{
                databaseNotes.noteDAO().restoreFromBin(note.id)
            }
            updateBinView()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun deleteNoteClick(note: Note) {
            databaseNotes.noteDAO().delete(note)
            updateBinView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        recyclerView = findViewById(R.id.recyclerView)

        val nameN = "Notes DB"
        val nameF = "Folders DB"

        tmp = this
        databaseNotes = Room.databaseBuilder(this.applicationContext, NoteDB::class.java, nameN)
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
        databaseFolders = Room.databaseBuilder(this.applicationContext, FolderDB::class.java, nameF)
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()

        if (databaseFolders.folderDAO().getByTitle("Main") == null){
            val folderTmp = Folder()
            folderTmp.title = "Main"
            folderTmp.pos = databaseFolders.folderDAO().getAll().size
            folderTmp.isDeleted = false
            folderTmp.isExpanded = true
            databaseFolders.folderDAO().insert(folderTmp)
        }

        notes = databaseNotes.noteDAO().getAll()
        folders = databaseFolders.folderDAO().getAll()
        updateMainView()
    }

    fun updateMainView(){
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        folderListAdapter = FolderListAdapter(tmp, folders, existFolderListClickListener, databaseNotes, existNoteListClickListener)
        val notesTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val initial = viewHolder.adapterPosition
                val final = target.adapterPosition
                val firstNote = folders.get(initial)
                val secondNote = folders.get(final)

                databaseFolders.folderDAO().changePos(firstNote.id, final)
                databaseFolders.folderDAO().changePos(secondNote.id, initial)
                folderListAdapter.notifyItemMoved(initial, final)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        })

        recyclerView.adapter = folderListAdapter
        notesTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun goToBin(view: View){
        setContentView(R.layout.bin_page)
        updateBinView()
    }

    fun updateBinView(){
        val recyclerViewNote: RecyclerView = findViewById(R.id.recyclerViewNotes)
        val recyclerViewFolder: RecyclerView = findViewById(R.id.recyclerViewFolders)
        recyclerViewFolder.setHasFixedSize(true)
        recyclerViewFolder.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        val folderListAdapterBin = FolderListAdapterBin(tmp, databaseFolders.folderDAO().getAllBin(), binFolderListClickListener, databaseNotes, binNoteListClickListener)
        recyclerViewFolder.adapter = folderListAdapterBin

        val notesBinTmp = databaseNotes.noteDAO().getAllBin()
        val notesBin: MutableList<Note> = ArrayList()
        for (noteTmp in notesBinTmp){
            if (databaseFolders.folderDAO().getById(noteTmp.folderId) != null){
                if (!databaseFolders.folderDAO().getById(noteTmp.folderId)!!.isDeleted)
                    notesBin.add(noteTmp)
            }
        }
        recyclerViewNote.setHasFixedSize(true)
        recyclerViewNote.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        val noteListAdapterBin = NoteListAdapterBin(tmp, notesBin, binNoteListClickListener)
        recyclerViewNote.adapter = noteListAdapterBin
    }

    fun goToAddNote(view: View){
        setContentView(R.layout.add_note)
        val staticSpinner: Spinner = findViewById(R.id.static_spinner)
        val adapter: ArrayAdapter<String> = ArrayAdapter(tmp, R.layout.spinner_item, folders.map{ x -> x.title })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        staticSpinner.adapter = adapter
    }

    fun goToAddFolder(view: View){
        setContentView(R.layout.add_folder)
    }

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    fun createFolder(view: View){
        val title: EditText = findViewById(R.id.folderTitle)
        val titleStr : String = title.text.toString()

        if (title.text.isEmpty()){
            AlertDialog.Builder(this)
                .setTitle("Warn")
                .setMessage("Title of folder must be initialized!")
                .setCancelable(true)
                .create()
                .show()
            return
        }

        if (isRedact){
            if (databaseFolders.folderDAO().getByTitle(titleStr) != null){
                AlertDialog.Builder(this)
                    .setTitle("Warn")
                    .setMessage("Title of folder must be unique!")
                    .setCancelable(true)
                    .create()
                    .show()
            }else{
                databaseFolders.folderDAO().update(objectToRedact, titleStr)
            }
        }else{
            if (databaseFolders.folderDAO().getByTitle(titleStr) != null){
                AlertDialog.Builder(this)
                    .setTitle("Warn")
                    .setMessage("Title of folder must be unique!")
                    .setCancelable(true)
                    .create()
                    .show()
            }else {
                val folder = Folder()
                folder.title = titleStr
                folder.isDeleted = false
                folder.pos = folders.size
                folder.isExpanded = true

                databaseFolders.folderDAO().insert(folder)
            }
        }

        setContentView(R.layout.main_activity)
        recyclerView = findViewById(R.id.recyclerView)
        folders = databaseFolders.folderDAO().getAll()
        folderListAdapter.notifyDataSetChanged()
        updateMainView()
        isRedact = false
    }

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    fun createNote(view: View){
        val title: EditText = findViewById(R.id.noteTitle)
        val content: EditText = findViewById(R.id.noteText)
        val spinner: Spinner = findViewById(R.id.static_spinner)
        val titleStr : String = title.text.toString()
        var textStr : String = content.text.toString()
        val folderName : String = (spinner.selectedItem as String)

        if (title.text.isEmpty()){
            AlertDialog.Builder(this)
                .setTitle("Warn")
                .setMessage("Title of note must be initialized!")
                .setCancelable(true)
                .create()
                .show()
            return
        }

        if (isRedact){
            databaseNotes.noteDAO().update(objectToRedact, titleStr, textStr, databaseFolders.folderDAO().getByTitle(folderName)!!.id)
        }else{
            val note = Note()
            note.title = titleStr
            note.text = textStr
            note.isDeleted = false
            note.folderId = databaseFolders.folderDAO().getByTitle(folderName)!!.id
            note.pos = notes.size
            databaseNotes.noteDAO().insert(note)
        }

        setContentView(R.layout.main_activity)
        recyclerView = findViewById(R.id.recyclerView)
        notes = databaseNotes.noteDAO().getAll()
        folderListAdapter.notifyDataSetChanged()
        updateMainView()
        isRedact = false
    }

    @SuppressLint("NotifyDataSetChanged")
    fun cancel(view: View){
        setContentView(R.layout.main_activity)
        recyclerView = findViewById(R.id.recyclerView)
        folders = databaseFolders.folderDAO().getAll()
        folderListAdapter.notifyDataSetChanged()
        updateMainView()
        isRedact = false
    }

    fun search(view: View){
        val title: EditText = findViewById(R.id.searchText)
        if (title.text.isNotEmpty()){
            search = title.text.toString()
            var foldersTmp: MutableList<Folder> = ArrayList()
            for (folder in folders){
                if (folder.title.contains(search))
                    foldersTmp.add(folder)
            }
            folders = foldersTmp
        }else{
            folders = databaseFolders.folderDAO().getAll()
        }
        updateMainView()
    }
}