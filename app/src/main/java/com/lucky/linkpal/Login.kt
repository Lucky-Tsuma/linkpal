package com.lucky.linkpal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    private var status: Boolean = true
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        link_sign_up.setSafeOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        button_user_login.setSafeOnClickListener {
            checkUserInput()
            if (status) {
                loginUser()
            }
        }
    }

    private fun checkUserInput() {

        /*reset in case there was previous wrong input*/
        status = true
        email_login.setHintTextColor(Color.parseColor("#737373"))
        email_login.setTextColor(Color.BLACK)
        password_login.setHintTextColor(Color.parseColor("#737373"))
        password_login.setTextColor(Color.BLACK)

        /*getting user input string, deleting whitespaces as well*/
        email = email_login.text.toString().trim()
        password = password_login.text.toString().trim()

        /*Check whether any of the fields is not filled*/
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(applicationContext, "Please fill the highlighted fields", Toast.LENGTH_SHORT).show()
            status = false
            if (email.isEmpty()) {
                email_login.setHintTextColor(Color.RED)
            }
            if (password.isEmpty()) {
                password_login.setHintTextColor(Color.RED); password_login.setTextColor(Color.RED)
            }
        }

        /*Checking email format and length*/
        if (status) {
            if (!(email.matches("(.*)@(.*)\\.(.*)".toRegex())) || email.length < 10 ||
                    email.startsWith("@") || email.endsWith("@")) {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                email_login.setTextColor(Color.RED)
                status = false
            }
        }

        /*Checking for password length*/
        if (status) {
            if (password.length < 6) {
                Toast.makeText(applicationContext, "Password too short", Toast.LENGTH_SHORT).show()
                password_login.setTextColor(Color.RED)
                status = false
            }
        }
    }

    private fun loginUser() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.login,
                Response.Listener { response ->
                    val res = String(response.data)
                    try {
                        val obj = JSONObject(res)

                        val msg: String = obj.getString("message")
                        val error = obj.getBoolean("error")
                        if (!error) {
                            val userType: String = obj.getString("userType")
                            if (userType == "employer") {
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                                val intentEmployer = Intent(this, Employer_Homepage::class.java)
                                startActivity(intentEmployer)
                            } else {
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(applicationContext, "Check your internet connection. Or try again later.", Toast.LENGTH_SHORT).show()
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
                    loginDetails["email"] = email
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
