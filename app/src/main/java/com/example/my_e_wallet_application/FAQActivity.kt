package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class FAQActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faqactivity)

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Back Button
        findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            finish() // Go back to previous screen
        }

        // 🔹 Menu Button
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
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
                        startActivity(Intent(this, SettingsActivity::class.java))
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
    }
}
