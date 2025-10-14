package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth

class Reward : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reward)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔙 Back button
        val backButton = findViewById<ImageView>(R.id.imageView3)
        backButton.setOnClickListener { onBackPressed() }

        // ☰ Menu icon
        val menuIcon = findViewById<ImageView>(R.id.imageView4)
        menuIcon.setOnClickListener { view ->
            val popupMenu = androidx.appcompat.widget.PopupMenu(this, view)
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
                        // Already here
                        true
                    }
                    R.id.menu_passes -> {
                        startActivity(Intent(this, PassesActivity::class.java))
                        true
                    }
                    R.id.menu_logout -> {
                        FirebaseAuth.getInstance().signOut()
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

        val unlockButton = findViewById<Button>(R.id.unlockButton)
        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val totalAmountText = findViewById<TextView>(R.id.totalAmountText)

        // ✅ Display username from email
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email ?: "User"
            val name = email.substringBefore("@") // Take the part before @
            welcomeText.text = "Hello, $name\nWelcome back!"
        } else {
            welcomeText.text = "Hello, User\nWelcome back!"
        }

        totalAmountText.text = "" // You can update this later with rewards balance

        // 🔓 Unlock button
        unlockButton.setOnClickListener {
            startActivity(Intent(this, RewardSavingsActivity::class.java))
        }
    }
}
