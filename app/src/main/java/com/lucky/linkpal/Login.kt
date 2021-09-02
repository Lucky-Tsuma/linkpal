package com.lucky.linkpal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.utils.REGEX
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var phone_number: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        link_sign_up.setSafeOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        button_user_login.setSafeOnClickListener {
            if (!validatePhoneNumber() || !validatePassword()) {
                return@setSafeOnClickListener
            }
            loginUser()
        }
    }

    private fun validatePhoneNumber(): Boolean {
        phone_number = phone_login.editText?.text.toString().trim()

        return if (phone_number.isEmpty()) {
            phone_login.error = "Cannot be empty"
            false
        } else if (!REGEX.PHONE_PATTERN1.matcher(phone_number)
                .matches() && !REGEX.PHONE_PATTERN2.matcher(
                phone_number
            ).matches()
        ) {
            phone_login.error = "Invalid input"
            false
        } else if (REGEX.PHONE_PATTERN1.matcher(phone_number)
                .matches() && phone_number.length != 10
        ) {
            phone_login.error = "Invalid input"
            false
        } else if (REGEX.PHONE_PATTERN2.matcher(phone_number)
                .matches() && phone_number.length != 13
        ) {
            phone_login.error = "Invalid input"
            false
        } else {
            phone_login.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        password = password_login.editText?.text.toString().trim()

        return if (password.isEmpty()) {
            password_login.error = "Failed, cannot be empty"
            false
        } else if (!REGEX.PASSWORD_PATTERN.matcher(password).matches()) {
            password_login.error = "Invalid password format."
            false
        } else {
            password_login.error = null
            true
        }
    }

    private fun loginUser() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.login,
            Response.Listener { response ->
                val res = String(response.data)
                try {
                    val obj = JSONObject(res)

                    val error = obj.getBoolean("error")
                    val msg: String = obj.getString("message")
                    if (!error) {
                        val userType: String = obj.getString("userType")
                        val user_id = obj.getInt("user_id")
                        val firstname = obj.getString("firstname")
                        val lastname = obj.getString("lastname")
                        val phone_number = obj.getString("phone_number")
                        if (userType == "employer") {
                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

                            val sh: SharedPreferences =
                                getSharedPreferences("sharedPref", MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sh.edit()
                            editor.putInt("user_id", user_id)
                            editor.putString("firstname", firstname)
                            editor.putString("lastname", lastname)
                            editor.putString("phone_number", phone_number)
                            editor.apply()

                            val intentEmployer = Intent(this, Employer_Homepage::class.java)
                            startActivity(intentEmployer)
                        } else {
                            val profile_pic = obj.getString("profile_pic")
                            val rating = obj.getDouble("rating")

                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

                            val sh: SharedPreferences =
                                getSharedPreferences("sharedPref", MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sh.edit()
                            editor.putInt("user_id", user_id)
                            editor.putString("firstname", firstname)
                            editor.putString("lastname", lastname)
                            editor.putString("phone_number", phone_number)
                            editor.putString("profile_pic", profile_pic)
                            editor.putFloat("rating", rating.toFloat())
                            editor.apply()

                            val intentWorker = Intent(this, Worker_Homepage::class.java)
                            startActivity(intentWorker)
                        }
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
                val loginDetails = HashMap<String, String>()
                try {
                    loginDetails["phone_number"] = phone_number
                    loginDetails["password"] = password
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return loginDetails
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

}
