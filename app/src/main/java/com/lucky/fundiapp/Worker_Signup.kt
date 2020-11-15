package com.lucky.fundiapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_worker__signup.*

class Worker_Signup : AppCompatActivity() {

    private var user_input_status: Boolean = true
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var gender: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__signup)

        val intent = getIntent()

        button_next_step.setOnClickListener {
            check_user_input()
            if(user_input_status){
                val intent = Intent(this, Worker_Signup2:: class.java)
                startActivity(intent)
            }
        }
    }

    fun check_user_input() {

        /*reset incase there were problems with previous input*/
        user_input_status = true
        worker_firstname.setBackgroundColor(Color.WHITE)
        worker_lastname.setBackgroundColor(Color.WHITE)
        worker_email.setBackgroundColor(Color.WHITE)
        worker_phone.setBackgroundColor(Color.WHITE)
        worker_password.setBackgroundColor(Color.WHITE)
        worker_confirm_password.setBackgroundColor(Color.WHITE)

        /*Getting user input strings. Deleting whitespaces*/
        firstname = worker_firstname.text.toString().trim()
        lastname = worker_lastname.text.toString().trim()
        email = worker_email.text.toString().trim()
        phone = worker_phone.text.toString().trim()
        password = worker_password.text.toString().trim()
        var password2: String = worker_confirm_password.text.toString().trim()

        /*check whether any of the fields is not filled*/
        if(firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || phone.isEmpty() ||
            password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(applicationContext, "Please fill the highlighted fields", Toast.LENGTH_SHORT).show()
            user_input_status = false
            if(firstname.isEmpty()) { worker_firstname.setBackgroundColor(Color.RED) }
            if(lastname.isEmpty()) { worker_lastname.setBackgroundColor(Color.RED) }
            if(email.isEmpty()) { worker_email.setBackgroundColor(Color.RED) }
            if(phone.isEmpty()) { worker_phone.setBackgroundColor(Color.RED) }
            if(password.isEmpty()) { worker_password.setBackgroundColor(Color.RED) }
            if(password2.isEmpty()) { worker_confirm_password.setBackgroundColor(Color.RED) }
        }

        /*Check for gender*/
        if(user_input_status) {
            if(!(gender_male_worker.isChecked || gender_female_worker.isChecked)){
                Toast.makeText(applicationContext, "Please select gender", Toast.LENGTH_SHORT).show()
                user_input_status = false
            } else {
                if(gender_male_worker.isChecked) gender = "M" else gender = "F"
            }
        }

        /*Checking email format and length*/
        if(user_input_status) {
            if(!(email.matches("(.*)@(.*)\\.(.*)".toRegex())) || email.length < 10
                || email.startsWith("@") || email.endsWith("@")) {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                worker_email.setBackgroundColor(Color.RED)
                user_input_status = false
            }
        }

        /*Checking phone number format and length*/
        if(user_input_status){
            if(!(phone.length == 13) || !(phone.matches("\\+254(.*)".toRegex()))) {
                Toast.makeText(applicationContext, "Phone number is invalid", Toast.LENGTH_SHORT).show()
                worker_phone.setBackgroundColor(Color.RED)
                user_input_status = false
            }
        }

        /*Checking for password length*/
        if(user_input_status) {
            if(password.length < 6 || password2.length < 6) {
                Toast.makeText(applicationContext, "Password too short", Toast.LENGTH_SHORT).show()
                user_input_status = false
                if(password.length < 6) {worker_password.setBackgroundColor(Color.RED)}
                if(password2.length < 6) {worker_confirm_password.setBackgroundColor(Color.RED)}
            }
        }

        /*Checking whether passwords match*/
        if(user_input_status) {
            if(!(password.matches(Regex(password2)))) {
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                user_input_status = false
                worker_password.setBackgroundColor(Color.RED)
                worker_confirm_password.setBackgroundColor(Color.RED)
            }
        }
    }/*check_user_input() ends here*/
}
