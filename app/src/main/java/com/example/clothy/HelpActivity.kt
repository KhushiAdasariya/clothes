package com.example.clothy

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
