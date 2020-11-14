package com.lucky.fundiapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private  var  user_input_status: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        link_sign_up.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        button_employer_login.setOnClickListener {
            check_user_input()
        }

        button_worker_login.setOnClickListener {
            check_user_input()
        }
    }

    fun check_user_input() {

        /*reset incase there was previous wrong input*/
        user_input_status = true
        email_login.setBackgroundColor(Color.WHITE)
        password_login.setBackgroundColor(Color.WHITE)

        /*getting user input string, deleting whitespaces as well*/
        val email_address: String = email_login.text.toString().trim()
        val password: String = password_login.text.toString().trim()

        /*Check whether any of the fields is not filled*/
        if(email_address.length == 0 || password.length == 0) {
            Toast.makeText(applicationContext, "Please fill the highlighted fields", Toast.LENGTH_SHORT).show()
            user_input_status = false
            if(email_address.length == 0) {email_login.setBackgroundColor(Color.RED)}
            if(password.length == 0) {password_login.setBackgroundColor(Color.RED)}
        }

        /*Checking email format and length*/
        if(user_input_status){
            if(!(email_address.matches("(.*)@(.*)\\.(.*)".toRegex())) || email_address.length < 10 ||
                email_address.startsWith("@") || email_address.endsWith("@")) {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                email_login.setBackgroundColor(Color.RED)
                user_input_status = false
            }
        }

        /*Checking for password length*/
        if(user_input_status) {
            if(password.length < 6) {
                Toast.makeText(applicationContext, "Password too short", Toast.LENGTH_SHORT).show()
                password_login.setBackgroundColor(Color.RED)
                user_input_status = false
            }
        }
    }

}
