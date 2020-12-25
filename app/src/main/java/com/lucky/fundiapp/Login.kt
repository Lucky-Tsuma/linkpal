package com.lucky.fundiapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lucky.fundiapp.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private  var  status: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        link_sign_up.setSafeOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        button_employer_login.setSafeOnClickListener {
            checkUserInput()
        }

        button_worker_login.setSafeOnClickListener {
            checkUserInput()
        }
    }

    private fun checkUserInput() {

        /*reset in case there was previous wrong input*/
        status = true
        email_login.setBackgroundColor(Color.WHITE)
        password_login.setBackgroundColor(Color.WHITE)

        /*getting user input string, deleting whitespaces as well*/
        val email: String = email_login.text.toString().trim()
        val password: String = password_login.text.toString().trim()

        /*Check whether any of the fields is not filled*/
        if(email.isEmpty() || password.isEmpty() ) {
            Toast.makeText(applicationContext, "Please fill the highlighted fields", Toast.LENGTH_SHORT).show()
            status = false
            if(email.isEmpty()) {email_login.setBackgroundColor(Color.RED)}
            if(password.isEmpty()) {password_login.setBackgroundColor(Color.RED)}
        }

        /*Checking email format and length*/
        if(status){
            if(!(email.matches("(.*)@(.*)\\.(.*)".toRegex())) || email.length < 10 ||
                email.startsWith("@") || email.endsWith("@")) {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                email_login.setBackgroundColor(Color.RED)
                status = false
            }
        }

        /*Checking for password length*/
        if(status) {
            if(password.length < 6) {
                Toast.makeText(applicationContext, "Password too short", Toast.LENGTH_SHORT).show()
                password_login.setBackgroundColor(Color.RED)
                status = false
            }
        }
    }

}
