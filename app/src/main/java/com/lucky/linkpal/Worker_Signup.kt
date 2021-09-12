package com.lucky.linkpal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lucky.linkpal.utils.REGEX
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_employer__signup.*
import kotlinx.android.synthetic.main.activity_worker__signup.*

class Worker_Signup : AppCompatActivity() {

    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var phone_number: String
    private lateinit var password: String
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__signup)

        button_next_step.setSafeOnClickListener {
            if (!validateNames() || !validatePhoneNumber() || !validatePassword() || !validateGender()) {
                return@setSafeOnClickListener
            }
            val intent = Intent(this, Worker_Signup2::class.java)
            intent.putExtra("firstname", firstname)
            intent.putExtra("lastname", lastname)
            intent.putExtra("phone", phone_number)
            intent.putExtra("password", password)
            intent.putExtra("gender", gender)
            startActivity(intent)
        }
    }

    private fun validateNames(): Boolean {
        firstname = worker_firstname.editText?.text.toString().trim()
        lastname = worker_lastname.editText?.text.toString().trim()

        worker_firstname.error = null
        worker_lastname.error = null

        return when {
            firstname.isEmpty() -> {
                worker_firstname.error = "Cannot be empty"
                false
            }
            lastname.isEmpty() -> {
                worker_lastname.error = "Cannot be empty"
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        phone_number = worker_phone.editText?.text.toString().trim()
        worker_phone.error = null

        return if (phone_number.isEmpty()) {
            worker_phone.error = "Cannot be empty"
            false
        } else if (!REGEX.PHONE_PATTERN1.matcher(phone_number)
                .matches() && !REGEX.PHONE_PATTERN2.matcher(
                phone_number
            ).matches()
        ) {
            worker_phone.error = "Invalid input"
            false
        } else if (REGEX.PHONE_PATTERN1.matcher(phone_number)
                .matches() && phone_number.length != 10
        ) {
            worker_phone.error = "Invalid input"
            false
        } else if (REGEX.PHONE_PATTERN2.matcher(phone_number)
                .matches() && phone_number.length != 13
        ) {
            worker_phone.error = "Invalid input"
            false
        } else {
            true
        }
    }

    private fun validatePassword(): Boolean {
        password = worker_password.editText?.text.toString().trim()
        val password2 = worker_confirm_password.editText?.text.toString().trim()

        worker_password.error = null
        worker_confirm_password.error = null

        return if (password.isEmpty()) {
            worker_password.error = "Cannot be empty"
            false
        } else if (!REGEX.PASSWORD_PATTERN.matcher(password).matches()) {
            worker_password.error = "Invalid password format"
            Toast.makeText(
                applicationContext,
                "Password should be at least 6 characters long.\nShould have  both digits and letters.",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else if (password2.isEmpty()) {
            worker_confirm_password.error = "Cannot be empty"
            false
        } else if (!REGEX.PASSWORD_PATTERN.matcher(password2).matches()) {
            worker_confirm_password.error = "Invalid password format"
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
        return if (!(gender_male_worker.isChecked || gender_female_worker.isChecked)) {
            Toast.makeText(applicationContext, "Please select gender", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            gender = if (gender_male_worker.isChecked) "M" else "F"
            true
        }
    }
}
