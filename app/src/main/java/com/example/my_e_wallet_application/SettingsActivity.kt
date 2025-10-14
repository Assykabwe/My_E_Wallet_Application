package com.example.my_e_wallet_application

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 🔙 Back icon
        findViewById<ImageView>(R.id.imageView3).setOnClickListener { finish() }

        // ☰ Menu icon
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
                        // Already in Settings, just return
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

        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)

        // Profile
        val profileSection = findViewById<LinearLayout>(R.id.profileSection)
        profileSection.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
        }

        // Cards
        val cardsSection = findViewById<LinearLayout>(R.id.cardsSection)
        cardsSection.setOnClickListener {
            Toast.makeText(this, "Cards clicked", Toast.LENGTH_SHORT).show()
        }

        // Language
        val languageSection = findViewById<LinearLayout>(R.id.languageSection)
        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)

        val languages = arrayOf("English", "Zulu", "Afrikaans")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.setSelection(sharedPreferences.getInt("languageIndex", 0))
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                sharedPreferences.edit().putInt("languageIndex", position).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Notifications
        val notificationSwitch = findViewById<Switch>(R.id.notificationSwitch)
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notificationsEnabled", true)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notificationsEnabled", isChecked).apply()
        }

        // Contact Us
        val contactSection = findViewById<LinearLayout>(R.id.contactSection)
        contactSection.setOnClickListener {
            Toast.makeText(this, "Contact us at support@billbliss.com", Toast.LENGTH_LONG).show()
        }
    }
}
