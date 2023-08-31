package com.calibraint.notes_rt.interfaces

import com.calibraint.notes_rt.models.NoteModel

interface OnNoteAction {
    fun onEdit(note: NoteModel?)
    fun onDelete(id: String?)
}