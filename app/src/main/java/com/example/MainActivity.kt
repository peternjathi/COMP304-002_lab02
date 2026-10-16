package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * MainActivity acts as the compatibility entry point for standard Android Studio launches,
 * redirecting seamlessly to HomeActivity as specified by the COMP-304 Lab #2 requirements.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
