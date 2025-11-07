package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu // Import for PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CardManagementActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var cardsRecyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter

    private val cardList = mutableListOf<Pair<String, Card>>() // Stores (documentId, CardObject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_management)

        auth = FirebaseAuth.getInstance()
        cardsRecyclerView = findViewById(R.id.cardsRecyclerView)
        cardsRecyclerView.layoutManager = LinearLayoutManager(this)

        // 1. Setup Header Icons
        setupHeaderActions()

        // 2. Setup RecyclerView Adapter and Data Fetching (Existing Logic)
        cardAdapter = CardAdapter(cardList) { cardId ->
            val intent = Intent(this, DisplayCardActivity::class.java)
            intent.putExtra("cardId", cardId)
            startActivity(intent)
        }
        cardsRecyclerView.adapter = cardAdapter

        // 3. Set up the Add Card Button
        val addCardButton: Button = findViewById(R.id.addCardButton)
        addCardButton.setOnClickListener {
            startActivity(Intent(this, AddCardActivity::class.java))
        }

        fetchUserCards()
    }

    private fun setupHeaderActions() {
        // 🔙 Back icon
        findViewById<ImageView>(R.id.imageView3).setOnClickListener { finish() }

        // ☰ Menu icon (Copied from SettingsActivity.kt)
        val menuIcon = findViewById<ImageView>(R.id.imageView4)
        menuIcon.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.category_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_cards -> {
                        // Already in a card-related activity, could go to AddCard or just return
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

    private fun fetchUserCards() {
        // ... (The rest of your existing fetchUserCards function remains here)
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("cards")
            .whereEqualTo("userId", userId) // Assuming 'userId' is a field in your Firebase Card document
            .get()
            .addOnSuccessListener { result ->
                cardList.clear()
                for (document in result.documents) {
                    val card = document.toObject(Card::class.java)
                    if (card != null) {
                        // Store the document ID along with the Card object
                        cardList.add(Pair(document.id, card))
                    }
                }
                cardAdapter.notifyDataSetChanged()
                if (cardList.isEmpty()) {
                    Toast.makeText(this, "No cards found. Tap 'Add New Card' to begin.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("CardManagement", "Error fetching cards", e)
                Toast.makeText(this, "Failed to load cards: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}