package com.example.my_e_wallet_application

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth


class RewardSavingsActivity : AppCompatActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var tabLayout: TabLayout
    private lateinit var savingsHeader: TextView
    private lateinit var spendingHeader: TextView
    private lateinit var milestoneHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_savings)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔙 Back button
        findViewById<ImageView>(R.id.imageView3).setOnClickListener { onBackPressed() }

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

        // Bind views
        scrollView = findViewById(R.id.rewardScrollView)
        tabLayout = findViewById(R.id.rewardTabs)
        savingsHeader = findViewById(R.id.savingsHeader)
        spendingHeader = findViewById(R.id.spendingHeader)
        milestoneHeader = findViewById(R.id.milestoneHeader)

        // Tab click listener to scroll to sections
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) = scrollToTab(tab)
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) = scrollToTab(tab)
        })

        // Setup reward toggle functionality
        setupRewardToggle()
    }

    // Smooth scroll to a view inside the ScrollView
    private fun scrollToView(targetView: View) {
        scrollView.post {
            scrollView.smoothScrollTo(0, targetView.top)
        }
    }

    private fun scrollToTab(tab: TabLayout.Tab?) {
        when(tab?.position) {
            0 -> scrollToView(savingsHeader)
            1 -> scrollToView(spendingHeader)
            2 -> scrollToView(milestoneHeader)
        }
    }

    // Toggle reward cards between Locked and Unlocked
    private fun setupRewardToggle() {
        val rewards = listOf(
            Triple(findViewById<ImageView>(R.id.checkFirstDeposit), findViewById<TextView>(R.id.savingsHeader), "Unlocked"),
            Triple(findViewById<ImageView>(R.id.checkR1000Saved), findViewById<TextView>(R.id.savingsHeader), "Locked"),
            Triple(findViewById<ImageView>(R.id.check1MonthStreak), findViewById<TextView>(R.id.savingsHeader), "Locked"),
            Triple(findViewById<ImageView>(R.id.check10Transactions), findViewById<TextView>(R.id.savingsHeader), "Locked"),
            Triple(findViewById<ImageView>(R.id.checkWeeklyGoal), findViewById<TextView>(R.id.savingsHeader), "Locked"),
            Triple(findViewById<ImageView>(R.id.check7DayStreak), findViewById<TextView>(R.id.savingsHeader), "Locked"),
            Triple(findViewById<ImageView>(R.id.checkFirstExpense), findViewById<TextView>(R.id.spendingHeader), "Unlocked"),
            Triple(findViewById<ImageView>(R.id.checkSaved1000Total), findViewById<TextView>(R.id.milestoneHeader), "Locked")
        )

        for ((icon, _, defaultStatus) in rewards) {
            // Initialize icon state
            icon.tag = if (defaultStatus == "Unlocked") "unlocked" else "locked"
            icon.setColorFilter(
                if (defaultStatus == "Unlocked") resources.getColor(R.color.green, null)
                else resources.getColor(R.color.gray, null)
            )

            // Toggle on click
            icon.setOnClickListener {
                if (icon.tag == "locked") {
                    icon.setColorFilter(resources.getColor(R.color.green, null))
                    icon.tag = "unlocked"
                } else {
                    icon.setColorFilter(resources.getColor(R.color.gray, null))
                    icon.tag = "locked"
                }
            }
        }
    }
}
