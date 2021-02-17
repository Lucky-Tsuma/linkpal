package com.lucky.linkpal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lucky.linkpal.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_signup.*

class Signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        button_hire.setSafeOnClickListener {
            val intent = Intent(this, Employer_Signup::class.java)
            startActivity(intent)
        }

        button_find_job.setSafeOnClickListener {
            val intent = Intent(this, Worker_Signup::class.java)
            startActivity(intent)
        }
    }
}
