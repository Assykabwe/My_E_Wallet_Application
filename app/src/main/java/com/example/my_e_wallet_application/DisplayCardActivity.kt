package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DisplayCardActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var tvHolderVisual: TextView
    private lateinit var tvNumberVisual: TextView
    private lateinit var tvExpiryVisual: TextView
    private lateinit var tvCVVVisual: TextView

    private lateinit var tvHolderDetails: TextView
    private lateinit var tvNumberDetails: TextView
    private lateinit var tvExpiryDetails: TextView
    private lateinit var tvCVVDetails: TextView

    private lateinit var backIcon: ImageView
    private lateinit var menuIcon: ImageView

    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnAddNew: Button

    private var currentCardId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_card)

        // 🔹 Bind views
        tvHolderVisual = findViewById(R.id.tvCardHolderVisual)
        tvNumberVisual = findViewById(R.id.tvCardNumberVisual)
        tvExpiryVisual = findViewById(R.id.tvExpiryVisual)
        tvCVVVisual = findViewById(R.id.tvCVVVisual)

        tvHolderDetails = findViewById(R.id.tvCardHolderDetails)
        tvNumberDetails = findViewById(R.id.tvCardNumberDetails)
        tvExpiryDetails = findViewById(R.id.tvExpiryDetails)
        tvCVVDetails = findViewById(R.id.tvCVV)

        backIcon = findViewById(R.id.imageView3)
        menuIcon = findViewById(R.id.imageView4)

        btnUpdate = findViewById(R.id.btnUpdateCard)
        btnDelete = findViewById(R.id.btnDeleteCard)
        btnAddNew = findViewById(R.id.btnAddNewCard)

        // 🔙 Back button
        backIcon.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ☰ Menu
        menuIcon.setOnClickListener { view ->
            val popupMenu = androidx.appcompat.widget.PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.category_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_cards -> startActivity(Intent(this, AddCardActivity::class.java))
                    R.id.menu_rewards -> startActivity(Intent(this, Reward::class.java))
                    R.id.menu_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
                    R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                    R.id.menu_passes -> startActivity(Intent(this, PassesActivity::class.java))
                    R.id.menu_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.menu_help -> startActivity(Intent(this, FAQActivity::class.java))
                    R.id.menu_about -> Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                    else -> false
                }
                true
            }
            popupMenu.show()
        }

        // 🔹 Load card details
        val cardId = intent.getStringExtra("cardId")
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (cardId == null) {
            // Load latest card
            loadLatestCardForUser(userId)
        } else {
            loadCardById(userId, cardId)
        }

        setupButtonActions()
    }

    private fun loadCardById(userId: String, cardId: String) {
        db.collection("cards")
            .document(cardId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val cardUserId = document.getString("userId")
                    if (cardUserId != userId) {
                        Toast.makeText(this, "You don’t have permission to view this card.", Toast.LENGTH_SHORT).show()
                        finish()
                        return@addOnSuccessListener
                    }

                    val card = document.toObject(Card::class.java)
                    currentCardId = document.id
                    displayCardInfo(card)
                } else {
                    Toast.makeText(this, "Card not found.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load card: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("DisplayCardActivity", "Firestore error", e)
                finish()
            }
    }

    private fun loadLatestCardForUser(userId: String) {
        db.collection("cards")
            .whereEqualTo("userId", userId)
            .orderBy("expiryDate")
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents.first()
                    val card = doc.toObject(Card::class.java)
                    currentCardId = doc.id
                    displayCardInfo(card)
                } else {
                    Toast.makeText(this, "No cards found. Please add one.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AddCardActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading cards.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun displayCardInfo(card: Card?) {
        if (card == null) {
            Toast.makeText(this, "Invalid card data.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val maskedNumber = maskCardNumber(card.cardNumber)
        val maskedCVV = maskCVV(card.cvv)

        // 💳 Visual display
        tvHolderVisual.text = card.cardHolderName
        tvNumberVisual.text = maskedNumber.chunked(4).joinToString(" ")
        tvExpiryVisual.text = card.expiryDate
        tvCVVVisual.text = maskedCVV

        // 🧾 Detail section
        tvHolderDetails.text = "Holder: ${card.cardHolderName}"
        tvNumberDetails.text = "Number: ${maskedNumber.chunked(4).joinToString(" ")}"
        tvExpiryDetails.text = "Expiry: ${card.expiryDate}"
        tvCVVDetails.text = "CVV: $maskedCVV"
    }

    private fun setupButtonActions() {
        btnUpdate.setOnClickListener {
            if (currentCardId == null) {
                Toast.makeText(this, "No card selected to update.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("cardId", currentCardId)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            if (currentCardId == null) {
                Toast.makeText(this, "No card to delete.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.collection("cards").document(currentCardId!!).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Card deleted successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AddCardActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error deleting card: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnAddNew.setOnClickListener {
            startActivity(Intent(this, AddCardActivity::class.java))
        }
    }

    private fun maskCardNumber(number: String?): String {
        if (number.isNullOrEmpty()) return ""
        return if (number.length > 4) "*".repeat(number.length - 4) + number.takeLast(4) else number
    }

    private fun maskCVV(cvv: String?): String {
        if (cvv.isNullOrEmpty()) return ""
        return "*".repeat(cvv.length)
    }
}
