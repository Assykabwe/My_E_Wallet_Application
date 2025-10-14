package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TransactionsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val transactions: MutableList<String> = mutableListOf("-25.00 Transport", "+500.00 Salary")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        // 🔹 Header buttons
        val backIcon = findViewById<ImageView>(R.id.imageView3)

        // Back button: finishes activity
        backIcon.setOnClickListener {
            finish()
        }

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
        // 🔹 Transaction list
        listView = findViewById(R.id.transactionList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, transactions)
        listView.adapter = adapter

        // 🔹 Action buttons
        val payBtn: LinearLayout = findViewById(R.id.payBtn)
        val transferBtn: LinearLayout = findViewById(R.id.transferBtn)
        val reportBtn: LinearLayout = findViewById(R.id.reportBtn)

        payBtn.setOnClickListener {
            transactions.add("-100.00 Payment")
            adapter.notifyDataSetChanged()
        }

        transferBtn.setOnClickListener {
            transactions.add("-50.00 Transfer")
            adapter.notifyDataSetChanged()
        }

        reportBtn.setOnClickListener {
            Toast.makeText(this, "Report generated!", Toast.LENGTH_SHORT).show()
        }
    }
}
