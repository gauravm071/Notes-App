package com.example.notesapp.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.Constants.LOGGED_IN_KEY
import com.example.notesapp.Constants.NOTES_SHARED_PREF
import com.example.notesapp.db.FeedReaderContract
import com.example.notesapp.db.FeedReaderDbHelper
import com.example.notesapp.R
import com.example.notesapp.adapter.NotesAdapter
import com.example.notesapp.databinding.FragmentHomepageBinding
import com.example.notesapp.model.Note
import java.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson

class Homepage : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentHomepageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
        val editor= getSharedPref().edit()
        editor.putBoolean(LOGGED_IN_KEY,true).apply()
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_homepage_to_addNoteScreen)
        }
        binding.homepageToolbar.rightIcon.setOnClickListener {
            signOut()
        }
        getNotes()
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                val editor= getSharedPref().edit()
                editor.putBoolean(LOGGED_IN_KEY,false).apply()
                findNavController().navigate(R.id.action_homepage_to_loginScreen2)
            }
    }

    private fun getNotes() {
        val dbHelper = FeedReaderDbHelper(requireContext())
        val db = dbHelper.writableDatabase
        val listOfNotes: ArrayList<Note> = ArrayList<Note>()
        val selectQuery = "SELECT *FROM " + FeedReaderContract.FeedEntry.TABLE_NAME
        val cursor = db.rawQuery(selectQuery, null)
        while (cursor.moveToNext()) {
            val title = cursor.getString(1)
            val note = cursor.getString(2)
            val id= cursor.getInt(0)
            listOfNotes.add(Note(title, note, id))
        }
        binding.emptyNoteAnim.isVisible = listOfNotes.isEmpty()
        binding.notesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.notesRecyclerView.adapter = NotesAdapter(listOfNotes) { note ->
            val gson = Gson()
            val bundle = bundleOf("note" to gson.toJson(note))
            findNavController().navigate(R.id.action_homepage_to_addNoteScreen,bundle)
        }
    }

    private fun getSharedPref(): SharedPreferences {
        return requireActivity().getSharedPreferences(NOTES_SHARED_PREF, Context.MODE_PRIVATE)
    }
}