package com.lucky.fundiapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_log_in.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        button_create_account.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        
        about.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }
    }
}
