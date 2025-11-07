package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
// 💡 FIX APPLIED: Used an alias (GmsGoogleSignIn) for the imported class
import com.google.android.gms.auth.api.signin.GoogleSignIn as GmsGoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

// Class name remains GoogleSignInActivity
class GoogleSignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("141915818721-pjcfprs4t3uqkh0codalj7s8f4dpadsb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // 💡 Use the alias: GmsGoogleSignIn.getClient()
        googleSignInClient = GmsGoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<com.google.android.gms.common.SignInButton>(R.id.btnGoogleSignIn)
        signInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // 💡 Use the alias: GmsGoogleSignIn.getSignedInAccountFromIntent()
            val task: Task<GoogleSignInAccount> = GmsGoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                sendTokenToBackend(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendTokenToBackend(idToken: String) {
        // Send idToken for verification and login
        Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}