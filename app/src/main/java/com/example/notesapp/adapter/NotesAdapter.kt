package com.example.notesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.NotesItemBinding
import com.example.notesapp.model.Note

class NotesAdapter(
    private val listOfNotes: ArrayList<Note>,
    private val listener: (Note?) -> Unit
) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfNotes[position], listener)
    }

    override fun getItemCount(): Int {
        return listOfNotes.size
    }

    class ViewHolder(private val binding: NotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, listener: (Note) -> Unit) {
            binding.noteTitle.text = note.title
            binding.noteDescription.text = note.note
            binding.root.setOnClickListener {
                listener(note)
            }
        }
    }
}