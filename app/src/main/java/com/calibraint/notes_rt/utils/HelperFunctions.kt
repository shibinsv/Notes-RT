package com.calibraint.notes_rt.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.calibraint.notes_rt.R
import com.calibraint.notes_rt.databinding.DialogAddNoteBinding
import com.calibraint.notes_rt.models.NoteModel

object HelperFunctions {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        positiveAction: () -> Unit,
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { dialogInterface, _ ->
                dialogInterface.dismiss()
                positiveAction()
            }.setNegativeButton(negativeText) { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }

    fun showCustomDialog(
        context: Context,
        title: String,
        positiveText: String,
        note: NoteModel? = null,
        positiveAction: (title: String, note: String) -> Unit,
        onCancelAction: () -> Unit,
    ) {
        val dialogView = DialogAddNoteBinding.inflate(LayoutInflater.from(context))
        dialogView.apply {
            note?.let {
                etNoteTitle.setText(note.title)
                etNoteContent.setText(note.content)
            }
            val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setView(root)
                .setPositiveButton(positiveText) { _, _ ->
                    positiveAction(etNoteTitle.text.toString(), etNoteContent.text.toString())
                }.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> onCancelAction() }
                .create()
            dialog.show()
        }
    }
}