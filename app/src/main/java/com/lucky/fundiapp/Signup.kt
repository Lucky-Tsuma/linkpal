package com.lucky.fundiapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_signup.*

class Signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        button_account_employer.setOnClickListener {
            val intent = Intent(this, Employer_Signup::class.java)
            startActivity(intent)
        }

        button_account_worker.setOnClickListener {
            val intent = Intent(this, Worker_Signup::class.java)
            startActivity(intent)
        }
    }
}
