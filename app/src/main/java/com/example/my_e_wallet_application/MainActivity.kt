package com.example.my_e_wallet_application

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find views
        val loginButton: Button = findViewById(R.id.login)
        val signupButton: Button = findViewById(R.id.signup)
        val logo: ImageView = findViewById(R.id.imageView)

        // Click navigation
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        logo.setOnClickListener {
            Toast.makeText(this, "Welcome to E-Wallet!", Toast.LENGTH_SHORT).show()
        }

        // Apply scale animation on press
        animatePress(loginButton)
        animatePress(signupButton)
        animatePress(logo)
    }

    private fun animatePress(view: View) {
        val scaleDownX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.95f)
        val scaleDownY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.95f)
        val scaleUpX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
        val scaleUpY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> ObjectAnimator.ofPropertyValuesHolder(v, scaleDownX, scaleDownY).apply { duration = 100; start() }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> ObjectAnimator.ofPropertyValuesHolder(v, scaleUpX, scaleUpY).apply { duration = 100; start() }
            }
            false
        }
    }
}
