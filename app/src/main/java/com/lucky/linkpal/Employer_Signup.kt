package com.lucky.linkpal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_employer__signup.*
import org.json.JSONException
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


        button_sign_up_employer.setSafeOnClickListener {
            checkUserInput()
            if(status) {registerEmployer()}
        }
    }

    private fun checkUserInput() {
        /*reset in case there were problems with previous input*/
        status = true
        employer_firstname.setHintTextColor(Color.parseColor("#737373"))
        employer_lastname.setHintTextColor(Color.parseColor("#737373"))
        employer_email.setHintTextColor(Color.parseColor("#737373"))
        employer_email.setTextColor(Color.BLACK)
        employer_phone.setHintTextColor(Color.parseColor("#737373"))
        employer_phone.setTextColor(Color.BLACK)
        employer_password.setHintTextColor(Color.parseColor("#737373"))
        employer_password.setTextColor(Color.BLACK)
        employer_confirm_password.setHintTextColor(Color.parseColor("#737373"))
        employer_confirm_password.setTextColor(Color.BLACK)

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
            if(firstname.isEmpty()) { employer_firstname.setHintTextColor(Color.RED) }
            if(lastname.isEmpty()) { employer_lastname.setHintTextColor(Color.RED) }
            if(email.isEmpty()) { employer_email.setHintTextColor(Color.RED) }
            if(phone.isEmpty()) { employer_phone.setHintTextColor(Color.RED) }
            if(password.isEmpty()) { employer_password.setHintTextColor(Color.RED) }
            if(password2.isEmpty()) { employer_confirm_password.setHintTextColor(Color.RED) }
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
                employer_email.setTextColor(Color.RED)
                status = false
            }
        }

        /*Checking phone number format and length*/
        if(status){
            if(phone.length != 10 || (!(phone.matches(Regex("07(.*)"))) && !(phone.matches(Regex("01(.*)"))))) {
                Toast.makeText(applicationContext, "Phone number is invalid", Toast.LENGTH_SHORT).show()
                employer_phone.setTextColor(Color.RED)
                status = false
            }
        }

        /*Checking for password length*/
        if(status) {
            if(password.length < 6 || password2.length < 6) {
                Toast.makeText(applicationContext, "Password too short", Toast.LENGTH_SHORT).show()
                status = false
                if(password.length < 6) {employer_password.setTextColor(Color.RED)}
                if(password2.length < 6) {employer_confirm_password.setTextColor(Color.RED)}
            }
        }

        /*Checking whether passwords match*/
        if(status) {
            if(!(password.matches(Regex(password2)))) {
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                status = false
                employer_password.setTextColor(Color.RED)
                employer_confirm_password.setTextColor(Color.RED)
            }
        }
    }/*check_user_input() ends here*/
    
    /*SEND EMPLOYER DATA TO SERVER*/
    private fun registerEmployer() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.user_register,
            Response.Listener { response ->

                val res = String(response.data)

                try {
                    val obj = JSONObject(res)

                    val msg : String = obj.getString("message")
                    val error = obj.getBoolean("error")
                    if (!error) {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Oops! An error occurred", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(applicationContext, "Check your internet connection. Or try again later.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            /*to post data using volley, we create a key-value pair, then with the getParams() we return the HashMap to the request object
            * for posting*/
            override fun getParams(): MutableMap<String, String> {
                val emp = HashMap<String, String>()
                try {
                    emp["firstName"] = firstname
                    emp["lastName"] = lastname
                    emp["email"] = email
                    emp["phone"] = phone
                    emp["password"] = password
                    emp["gender"] = gender
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return emp
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}
