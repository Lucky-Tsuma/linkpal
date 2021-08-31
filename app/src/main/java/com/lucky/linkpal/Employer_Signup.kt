package com.lucky.linkpal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.utils.REGEX
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.activity_employer__signup.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern


class Employer_Signup : AppCompatActivity() {
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var longitude: String
    private lateinit var latitude: String
    private lateinit var phone_number: String
    private lateinit var password: String
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer__signup)


        button_sign_up_employer.setSafeOnClickListener {
            if (!validateNames() || !validatePhoneNumber() || !validatePassword() || !validateGender()) {
                return@setSafeOnClickListener
            }
            registerEmployer()
        }
    }

    private fun validateNames(): Boolean {
        firstname = employer_firstname.editText?.text.toString().trim()
        lastname = employer_lastname.editText?.text.toString().trim()

        employer_firstname.error = null
        employer_lastname.error = null

        return when {
            firstname.isEmpty() -> {
                employer_firstname.error = "Cannot be empty"
                false
            }
            lastname.isEmpty() -> {
                employer_lastname.error = "Cannot be empty"
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        phone_number = employer_phone.editText?.text.toString().trim()
        employer_phone.error = null

        return if (phone_number.isEmpty()) {
            employer_phone.error = "Cannot be empty"
            false
        } else if (!REGEX.PHONE_PATTERN1.matcher(phone_number).matches() && !REGEX.PHONE_PATTERN2.matcher(
                phone_number
            ).matches()
        ) {
            employer_phone.error = "Invalid input"
            false
        } else if (REGEX.PHONE_PATTERN1.matcher(phone_number).matches() && phone_number.length != 10) {
            employer_phone.error = "Invalid input"
            false
        } else if (REGEX.PHONE_PATTERN2.matcher(phone_number).matches() && phone_number.length != 13) {
            employer_phone.error = "Invalid input"
            false
        } else {
            true
        }
    }

    private fun validatePassword(): Boolean {
        password = employer_password.editText?.text.toString().trim()
        val password2 = employer_confirm_password.editText?.text.toString().trim()

        employer_password.error = null
        employer_confirm_password.error = null

        return if (password.isEmpty()) {
            employer_password.error = "Cannot be empty"
            false
        } else if (!REGEX.PASSWORD_PATTERN.matcher(password).matches()) {
            employer_password.error = "Invalid password format"
            Toast.makeText(
                applicationContext,
                "Password should be at least 6 characters long.\nShould have  both digits and letters.",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else if (password2.isEmpty()) {
            employer_confirm_password.error = "Cannot be empty"
            false
        } else if (!REGEX.PASSWORD_PATTERN.matcher(password2).matches()) {
            employer_confirm_password.error = "Invalid password format"
            Toast.makeText(
                applicationContext,
                "Password should be at least 6 characters long.\nShould have  both digits and letters.",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else if (!(password.matches(Regex(password2)))) {
            Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validateGender(): Boolean {
        return if (!(gender_male_emp.isChecked || gender_female_emp.isChecked)) {
            Toast.makeText(applicationContext, "Please select gender", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            gender = if (gender_male_emp.isChecked) "M" else "F"
            true
        }
    }

    /*SEND EMPLOYER DATA TO SERVER*/
    private fun registerEmployer() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.user_register,
            Response.Listener { response ->

                val res = String(response.data)

                try {
                    val obj = JSONObject(res)

                    val msg: String = obj.getString("message")
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
                    Toast.makeText(
                        applicationContext,
                        "Check your internet connection. Or try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    emp["longitude"] = longitude
                    emp["latitude"] = latitude
                    emp["phone"] = phone_number
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
