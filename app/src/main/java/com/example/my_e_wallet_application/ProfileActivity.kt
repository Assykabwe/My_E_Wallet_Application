package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView // Added for header icons
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu // Added for menu logic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ProfileActivity"

    private lateinit var tvProfileEmail: TextView
    private lateinit var etProfileName: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to view your profile.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 1. Setup Header Icons (NEW INTEGRATION)
        setupHeaderActions()

        // Bind Views
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        etProfileName = findViewById(R.id.etProfileName)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        // 2. Load Initial Data
        loadUserProfile(userId)

        // 3. Setup Update Button
        btnUpdateProfile.setOnClickListener {
            updateUserProfile(userId)
        }

        // 4. Setup Change Password Button
        btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Change Password feature is not yet implemented.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- NEW FUNCTION: Header Logic ---
    private fun setupHeaderActions() {
        // 🔙 Back icon (R.id.imageView3 is the ID from your activity_profile.xml header)
        findViewById<ImageView>(R.id.imageView3).setOnClickListener { finish() }

        // ☰ Menu icon (R.id.imageView4 is the ID from your activity_profile.xml header)
        val menuIcon = findViewById<ImageView>(R.id.imageView4)
        menuIcon.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.category_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_cards -> {
                        startActivity(Intent(this, AddCardActivity::class.java))
                        true
                    }
                    R.id.menu_rewards -> {
                        startActivity(Intent(this, Reward::class.java))
                        true
                    }
                    R.id.menu_transactions -> {
                        startActivity(Intent(this, TransactionsActivity::class.java))
                        true
                    }
                    R.id.menu_settings -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        true
                    }
                    R.id.menu_passes -> {
                        startActivity(Intent(this, PassesActivity::class.java))
                        true
                    }
                    R.id.menu_logout -> {
                        auth.signOut()
                        Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.menu_help -> {
                        startActivity(Intent(this, FAQActivity::class.java))
                        true
                    }
                    R.id.menu_about -> {
                        Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
    // --- End of NEW Function ---

    private fun loadUserProfile(userId: String) {
        // ... (Existing implementation remains the same)
        val userEmail = auth.currentUser?.email ?: "Email not found"
        tvProfileEmail.text = userEmail

        // Fetch user document from Firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    etProfileName.setText(user?.fullName)
                } else {
                    Log.d(TAG, "No user profile found in Firestore for $userId")
                    // Optionally, prompt user to complete profile
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching user profile", e)
                Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserProfile(userId: String) {
        // ... (Existing implementation remains the same)
        val newFullName = etProfileName.text.toString().trim()

        if (newFullName.isEmpty()) {
            etProfileName.error = "Name cannot be empty"
            return
        }

        // Prepare data for update
        val updates = mapOf(
            "fullName" to newFullName
        )

        // Update the user document in Firestore
        db.collection("users").document(userId).update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully! ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating profile", e)
                Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}