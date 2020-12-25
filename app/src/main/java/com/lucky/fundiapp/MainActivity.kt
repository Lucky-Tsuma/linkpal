package com.lucky.fundiapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lucky.fundiapp.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_log_in.setSafeOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        button_create_account.setSafeOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        
        about.setSafeOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }
    }
}
