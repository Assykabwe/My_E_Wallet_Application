package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddCardActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        // 🔙 Back button
        val backButton = findViewById<ImageView>(R.id.imageView3)
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ☰ Menu setup
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

        // 🔹 Bind input fields
        val etName = findViewById<EditText>(R.id.etCardHolderName)
        val etNumber = findViewById<EditText>(R.id.etCardNumber)
        val etExpiry = findViewById<EditText>(R.id.etExpiryDate)
        val etCVV = findViewById<EditText>(R.id.etCVV)
        val btnSave = findViewById<Button>(R.id.btnSaveCard)
        val btnViewCard = findViewById<Button>(R.id.btnViewCard)

        // 🔹 Handle Add button click
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val number = etNumber.text.toString().trim()
            val expiry = etExpiry.text.toString().trim()
            val cvv = etCVV.text.toString().trim()

            // ✅ Validate input fields
            if (name.isEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (number.length != 16 || !number.all { it.isDigit() }) {
                Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!expiry.matches(Regex("""^(0[1-9]|1[0-2])/[0-9]{2}$"""))) {
                Toast.makeText(this, "Expiry must be in MM/YY format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cvv.length !in 3..4 || !cvv.all { it.isDigit() }) {
                Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Disable button to prevent double click
            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            // 🔹 Prepare card data
            val cardData = hashMapOf(
                "cardHolderName" to name,
                "cardNumber" to number,
                "expiryDate" to expiry,
                "cvv" to cvv,
                "userId" to userId
            )

            // 🔹 Save to Firestore
            db.collection("cards")
                .add(cardData)
                .addOnSuccessListener { documentRef ->
                    Toast.makeText(this, "Card saved successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("AddCardActivity", "Saved card ID: ${documentRef.id}")

                    val intent = Intent(this, DisplayCardActivity::class.java)
                    intent.putExtra("cardId", documentRef.id)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving card: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AddCardActivity", "Firestore Error", e)
                }
                .addOnCompleteListener {
                    // 🔹 Re-enable button
                    btnSave.isEnabled = true
                    btnSave.text = "Add"
                }
        }

        // handle view card details
        btnViewCard.setOnClickListener {
            val intent = Intent(this, DisplayCardActivity::class.java)
            startActivity(intent)
        }

    }
}
