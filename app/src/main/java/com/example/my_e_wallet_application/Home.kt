package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_e_wallet_application.TransactionsActivity
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔙 Back button (optional, usually Home is the root)
        val backButton = findViewById<ImageView>(R.id.imageView3)
        backButton.setOnClickListener { onBackPressed() }

        // ☰ Menu button
        val menuButton = findViewById<ImageView>(R.id.imageView4)
        menuButton.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.category_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
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
                    R.id.menu_help -> {
                        startActivity(Intent(this, FAQActivity::class.java))
                        true
                    }
                    R.id.menu_about -> {
                        Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.menu_logout -> {
                        // Logout from Firebase
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // 🔹 Find all card views by ID
        val cardCards = findViewById<MaterialCardView>(R.id.card_cards)
        val cardRewards = findViewById<MaterialCardView>(R.id.card_rewards)
        val cardTransactions = findViewById<MaterialCardView>(R.id.card_transactions)
        val cardSettings = findViewById<MaterialCardView>(R.id.card_settings)
        val cardPasses = findViewById<MaterialCardView>(R.id.card_passes)
        val cardLogout = findViewById<MaterialCardView>(R.id.card_logout)

        // 🔹 Click listeners for grid cards
        cardCards.setOnClickListener { startActivity(Intent(this, AddCardActivity::class.java)) }
        cardRewards.setOnClickListener { startActivity(Intent(this, Reward::class.java)) }
        cardTransactions.setOnClickListener { startActivity(Intent(this, TransactionsActivity::class.java)) }
        cardSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        cardPasses.setOnClickListener { startActivity(Intent(this, PassesActivity::class.java)) }

        cardLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

