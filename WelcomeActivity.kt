package com.example.ssosign_inpage

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.ssosign_inpage.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EMAIL = "user_email"
    }

    private lateinit var binding: ActivityWelcomeBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Not signed in (e.g. session revoked)? Back to the sign-in screen.
        val user = auth.currentUser
        if (user == null) {
            goToSignIn()
            return
        }

        binding.userEmail.text = user.email
            ?: intent.getStringExtra(EXTRA_EMAIL)
            ?: user.displayName
            ?: "No email provided"

        binding.signOutButton.setOnClickListener { signOut() }
    }

    private fun signOut() {
        auth.signOut()
        // Also clear Credential Manager state so the Google account chooser resets
        lifecycleScope.launch {
            try {
                CredentialManager.create(this@WelcomeActivity)
                    .clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {
                // Non-fatal: Firebase session is already cleared
            }
            Toast.makeText(this@WelcomeActivity, R.string.msg_signed_out, Toast.LENGTH_SHORT).show()
            goToSignIn()
        }
    }

    private fun goToSignIn() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
