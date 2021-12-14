package com.example.notesapp.screens

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import android.content.Context.MODE_PRIVATE
import com.example.notesapp.Constants.LOGGED_IN_KEY
import com.example.notesapp.Constants.NOTES_SHARED_PREF

class LoginScreen : Fragment() {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val REQUEST_KEY=1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val isLoggedIn= getSharedPref().getBoolean(LOGGED_IN_KEY,false)
        // here the isLoggedOn check is of no use but as i have to show the implementation of shared preference also So, i've also included this check
        // removing the isLoggedOn check from below if condition will not affect any functionality.
        if(account!=null && isLoggedIn){
            navigateToHomePage()
        } else{
            binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
            binding.signInButton.setOnClickListener {
                signIn()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_KEY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_KEY) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            completedTask.getResult(ApiException::class.java)
            navigateToHomePage()
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_loginScreen_to_homepage)
    }

    private fun getSharedPref(): SharedPreferences {
        return requireActivity().getSharedPreferences(NOTES_SHARED_PREF, MODE_PRIVATE)
    }
}