package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.pay.Pay
import com.google.android.gms.pay.PayClient
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class PassesActivity : AppCompatActivity() {

    private lateinit var payClient: PayClient
    private val SAVE_PASS_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passes)

        // Back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener { onBackPressed() }

        // Menu button
        findViewById<ImageView>(R.id.menuButton).setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.category_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_cards -> startActivity(Intent(this, AddCardActivity::class.java))
                    R.id.menu_rewards -> startActivity(Intent(this, Reward::class.java))
                    R.id.menu_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
                    R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                    R.id.menu_passes -> startActivity(Intent(this, PassesActivity::class.java))
                    R.id.menu_help -> startActivity(Intent(this, FAQActivity::class.java))
                    R.id.menu_about -> Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                    R.id.menu_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
            popup.show()
        }

        payClient = Pay.getClient(this)

        // Add to Google Wallet button
        findViewById<ImageButton>(R.id.addToWalletButton).setOnClickListener {
            fetchJwtAndAddPass()
        }
    }

    private fun fetchJwtAndAddPass() {
        val url = "http://10.0.2.2:3000/generatePassJWT"
        // Replace with your PC IP

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    val jwt = json.getString("jwt")
                    payClient.savePasses(jwt, this, SAVE_PASS_REQUEST_CODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing JWT", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Failed to get JWT: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SAVE_PASS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "✅ Pass added to Google Wallet!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ Failed to add pass.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
