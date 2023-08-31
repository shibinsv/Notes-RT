package com.calibraint.notes_rt.ui

import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.calibraint.notes_rt.R
import com.calibraint.notes_rt.databinding.ActivityMainBinding
import com.calibraint.notes_rt.interfaces.OnNoteAction
import com.calibraint.notes_rt.models.Note
import com.calibraint.notes_rt.models.NoteModel
import com.calibraint.notes_rt.utils.Constants
import com.calibraint.notes_rt.utils.HelperFunctions
import com.calibraint.notes_rt.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnNoteAction {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NotesAdapter

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.apply {
            viewModel.apply {
                initViewModel()
                adapter = NotesAdapter(noteList.value, this@MainActivity)
                rvNotes.adapter = adapter
                fabAddNote.setOnClickListener { addNote() }
                noteList.observe(this@MainActivity) { data ->
                    showVisibility(rvNotes, !data.isNullOrEmpty())
                    showVisibility(tvNoNotes, data.isNullOrEmpty())
                    adapter.updateView(data)
                }
            }
        }
    }

    private fun showVisibility(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    private fun showToast(message: String) {
        scope.launch { HelperFunctions.showToast(this@MainActivity, message) }
    }

    private fun addNote() {
        showVisibility(binding.tvNoNotes, false)
        HelperFunctions.showCustomDialog(
            this,
            getString(R.string.add_note),
            getString(R.string.add),
            positiveAction = { title, note ->
                showVisibility(binding.tvNoNotes, true)
                viewModel.addNote(Note(title, note))
            },
            onCancelAction = {
                showVisibility(binding.tvNoNotes, viewModel.noteList.value.isNullOrEmpty())
            }
        )
    }

    override fun onEdit(note: NoteModel?) {
        HelperFunctions.showCustomDialog(
            this,
            getString(R.string.edit_note),
            getString(R.string.edit),
            note,
            positiveAction = { title, noteContent ->
                val userKey = note?.noteId
                val updates = hashMapOf<String, Any>(
                    "/$userKey/${Constants.title}" to title,
                    "/$userKey/${Constants.content}" to noteContent
                )
                viewModel.editNote(
                    updates,
                    { showToast(getString(R.string.data_updated)) },
                    { showToast(getString(R.string.try_again)) }
                )

            }, onCancelAction = {}
        )
    }

    override fun onDelete(id: String?) {
        id?.let {
            HelperFunctions.showDialog(
                this,
                getString(R.string.delete_note),
                getString(R.string.proceed_delete),
                getString(R.string.delete),
                getString(R.string.cancel)
            ) {
                viewModel.deleteNote(id,
                    onCompletion = {
                        showToast(getString(R.string.note_deleted))
                        adapter.updateView(viewModel.noteList.value)
                    },
                    onError = { showToast(getString(R.string.try_again)) }
                )
            }
        } ?: run {
            showToast(getString(R.string.try_again))
        }
    }
}
