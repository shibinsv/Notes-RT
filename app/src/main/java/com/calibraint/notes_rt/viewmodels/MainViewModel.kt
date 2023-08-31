package com.calibraint.notes_rt.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calibraint.notes_rt.models.Note
import com.calibraint.notes_rt.models.NoteModel
import com.calibraint.notes_rt.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class MainViewModel : ViewModel() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    val noteList = MutableLiveData<ArrayList<NoteModel>?>()

    fun initViewModel() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(Constants.userData)
        noteList.value = arrayListOf()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentNoteData = arrayListOf<NoteModel>()
                try {
                    val snapShotObject: GenericTypeIndicator<HashMap<String?, Any?>?> =
                        object : GenericTypeIndicator<HashMap<String?, Any?>?>() {}
                    val noteHashmap = dataSnapshot.getValue(snapShotObject)
                    if (noteHashmap != null) {
                        val noteArrayList: ArrayList<Any?> = ArrayList(noteHashmap.values)
                        noteArrayList.forEachIndexed { index, noteDat ->
                            val mappedData = noteDat as HashMap<String, String>
                            currentNoteData.add(
                                NoteModel(
                                    noteId = noteHashmap.keys.elementAt(index) ?: "",
                                    title = mappedData[Constants.title] ?: "",
                                    content = mappedData[Constants.content] ?: ""
                                )
                            )
                        }
                        noteList.value = currentNoteData
                    }
                } catch (e: Exception) {
                    print(e)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        }
        reference.addValueEventListener(valueEventListener)
    }

    fun addNote(note: Note) {
        val key = reference.push().key ?: return
        reference.child(key).setValue(note)
    }

    fun editNote(updates: HashMap<String, Any>, onCompletion: () -> Unit, onError: () -> Unit) {
        try {
            val noteReference = database.reference.child(Constants.userData)
            noteReference.updateChildren(updates)
                .addOnSuccessListener { onCompletion() }
                .addOnFailureListener { onError() }
        } catch (e: Exception) {
            onError()
        }
    }

    fun deleteNote(id: String, onCompletion: () -> Unit, onError: () -> Unit) {
        try {
            val userReference = reference.child(id)
            userReference.removeValue()
                .addOnSuccessListener {
                    val deletedNote = noteList.value?.find { e -> e.noteId == id }
                    noteList.value?.remove(deletedNote)
                    if (noteList.value.isNullOrEmpty()) noteList.value = arrayListOf()
                    onCompletion()
                }
                .addOnFailureListener { onError() }
        } catch (e: Exception) {
            onError()
        }
    }
}