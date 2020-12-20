package com.lucky.fundiapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_employer__signup.*
import org.json.JSONObject


class Employer_Signup : AppCompatActivity() {

    private var status: Boolean = true
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer__signup)


        button_sign_up_employer.setOnClickListener {
            checkUserInput()
            if(status) {registerEmployer()}
        }

    }

    private fun checkUserInput() {
        /*reset in case there were problems with previous input*/
        status = true
        employer_firstname.setBackgroundColor(Color.WHITE)
        employer_lastname.setBackgroundColor(Color.WHITE)
        employer_email.setBackgroundColor(Color.WHITE)
        employer_phone.setBackgroundColor(Color.WHITE)
        employer_password.setBackgroundColor(Color.WHITE)
        employer_confirm_password.setBackgroundColor(Color.WHITE)

        /*Getting user input strings. Deleting whitespaces*/
        firstname = employer_firstname.text.toString().trim()
        lastname = employer_lastname.text.toString().trim()
        email = employer_email.text.toString().trim()
        phone = employer_phone.text.toString().trim()
        password = employer_password.text.toString().trim()
        var password2: String = employer_confirm_password.text.toString().trim()

        /*check whether any of the fields is not filled*/
        if(firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || phone.isEmpty() ||
            password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(applicationContext, "Please fill the highlighted fields", Toast.LENGTH_SHORT).show()
            status = false
            if(firstname.isEmpty()) { employer_firstname.setBackgroundColor(Color.RED) }
            if(lastname.isEmpty()) { employer_lastname.setBackgroundColor(Color.RED) }
            if(email.isEmpty()) { employer_email.setBackgroundColor(Color.RED) }
            if(phone.isEmpty()) { employer_phone.setBackgroundColor(Color.RED) }
            if(password.isEmpty()) { employer_password.setBackgroundColor(Color.RED) }
            if(password2.isEmpty()) { employer_confirm_password.setBackgroundColor(Color.RED) }
        }

        /*Check for gender*/
        if(status) {
            if(!(gender_male_emp.isChecked || gender_female_emp.isChecked)){
                Toast.makeText(applicationContext, "Please select gender", Toast.LENGTH_SHORT).show()
                status = false
            } else {
                if(gender_male_emp.isChecked) gender = "M" else gender = "F"
            }
        }

        /*Checking email format and length*/
        if(status) {
            if(!(email.matches("(.*)@(.*)\\.(.*)".toRegex())) || email.length < 10
                || email.startsWith("@") || email.endsWith("@")) {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                employer_email.setBackgroundColor(Color.RED)
                status = false
            }
        }

        /*Checking phone number format and length*/
        if(status){
            if(!(phone.length == 13) || !(phone.matches("\\+254(.*)".toRegex()))) {
                Toast.makeText(applicationContext, "Phone number is invalid", Toast.LENGTH_SHORT).show()
                employer_phone.setBackgroundColor(Color.RED)
                status = false
            }
        }

        /*Checking for password length*/
        if(status) {
            if(password.length < 6 || password2.length < 6) {
                Toast.makeText(applicationContext, "Password too short", Toast.LENGTH_SHORT).show()
                status = false
                if(password.length < 6) {employer_password.setBackgroundColor(Color.RED)}
                if(password2.length < 6) {employer_confirm_password.setBackgroundColor(Color.RED)}
            }
        }

        /*Checking whether passwords match*/
        if(status) {
            if(!(password.matches(Regex(password2)))) {
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                status = false
                employer_password.setBackgroundColor(Color.RED)
                employer_confirm_password.setBackgroundColor(Color.RED)
            }
        }
    }/*check_user_input() ends here*/
    
    /*SEND EMPLOYER DATA TO SERVER*/
    private fun registerEmployer() {
        val requestQueue = Volley.newRequestQueue(this)

        val emp = JSONObject()

        try {
            emp.put("firstName", firstname)
            emp.put("lastName", lastname)
            emp.put("email", email)
            emp.put("phone", phone)
            emp.put("password", password)
            emp.put("gender", gender)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val req = JsonObjectRequest(Request.Method.POST, URLs.emp_register, emp,
            Response.Listener { _ ->  Toast.makeText(applicationContext, "Registration successful. You may log in" +
                    "to your account now", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error -> error.printStackTrace()
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            })

        requestQueue.add(req)
    }

}
