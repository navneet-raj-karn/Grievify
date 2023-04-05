package com.example.grievify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatBotActivate: ExtendedFloatingActionButton = findViewById(R.id.sell_button)
        chatBotActivate.setOnClickListener {
            startActivity(Intent(this, ChatBot::class.java))
        }
    }

}