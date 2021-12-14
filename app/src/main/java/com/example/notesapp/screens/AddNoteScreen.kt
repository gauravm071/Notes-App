package com.example.notesapp.screens

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentAddNoteScreenBinding
import com.example.notesapp.db.FeedReaderContract
import com.example.notesapp.db.FeedReaderDbHelper
import com.example.notesapp.model.Note
import com.google.gson.Gson

class AddNoteScreen : Fragment() {
    private lateinit var binding: FragmentAddNoteScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val note = arguments?.getString("note")
        var result: Note? = null
        if (note != null) {
            result = Gson().fromJson(note, Note::class.java)
            binding.apply {
                titleTextInputEditText.setText(result.title)
                writeNoteTextView.setText(result.note)
            }
        }
        setListener(result)
    }

    private fun setListener(result: Note?) {
        binding.addNotesToolbar.saveIcon.setOnClickListener {
            if (result != null) {
                updateData(
                    result.id, binding.titleTextInputEditText.text.toString(),
                    binding.writeNoteTextView.text.toString()
                )
            } else {
                addNotes(
                    binding.titleTextInputEditText.text.toString(),
                    binding.writeNoteTextView.text.toString()
                )
            }
            Toast.makeText(requireContext(), getString(R.string.note_added), Toast.LENGTH_SHORT).show()
        }

        binding.addNotesToolbar.deleteIcon.setOnClickListener {
            if (result != null) {
                deleteData(result.id)
            } else {
                findNavController().popBackStack()
            }
            Toast.makeText(requireContext(), getString(R.string.note_deleted), Toast.LENGTH_SHORT).show()

        }
    }

    private fun addNotes(title: String, note: String) {
        val db = getDb()
        val values = getContentValues(title, note)
        db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
        db?.close()
        findNavController().popBackStack(R.id.addNoteScreen, true)
    }

    private fun updateData(id: Int, title: String, note: String) {
        val db = getDb()
        val values = getContentValues(title, note)
        val key = arrayOf(id.toString())
        db?.update(FeedReaderContract.FeedEntry.TABLE_NAME, values, "_id = ?", key)
        db?.close()
        findNavController().popBackStack(R.id.addNoteScreen, true)
    }

    private fun deleteData(id: Int) {
        val db = getDb()
        val key = arrayOf(id.toString())
        db?.delete(FeedReaderContract.FeedEntry.TABLE_NAME, "_id = ?", key)
        db?.close()
        findNavController().popBackStack(R.id.addNoteScreen, true)
    }

    private fun getContentValues(title: String, note: String): ContentValues {
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, note)
        }
        return values
    }

    private fun getDb(): SQLiteDatabase? {
        val dbHelper = FeedReaderDbHelper(requireContext())
        return dbHelper.writableDatabase
    }
}