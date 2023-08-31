package com.calibraint.notes_rt.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.calibraint.notes_rt.R
import com.calibraint.notes_rt.databinding.ItemNotesBinding
import com.calibraint.notes_rt.interfaces.OnNoteAction
import com.calibraint.notes_rt.models.NoteModel

class NotesAdapter(private var data: ArrayList<NoteModel>?, val callback: OnNoteAction) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private lateinit var binding: ItemNotesBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        context = parent.context
        binding = ItemNotesBinding.inflate(LayoutInflater.from(context))
        return NoteViewHolder(binding)
    }

    fun updateView(updatedNoteData: ArrayList<NoteModel>?) {
        data = updatedNoteData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(data?.get(position))
    }

    override fun getItemCount(): Int = data?.size ?: 0

    inner class NoteViewHolder(itemView: ItemNotesBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val title: TextView = itemView.tvNoteTitle
        private val content: TextView = itemView.tvContent
        private val moreOption: ImageView = itemView.ivMore

        fun bind(note: NoteModel?) {
            title.text = note?.title
            content.text = note?.content
            moreOption.setOnClickListener {
                val popupMenu = PopupMenu(context, moreOption)
                popupMenu.inflate(R.menu.popup_menu) // Inflate your menu resource here

                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            callback.onEdit(note)
                            true
                        }

                        R.id.menu_delete -> {
                            callback.onDelete(note?.noteId)
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }
}
