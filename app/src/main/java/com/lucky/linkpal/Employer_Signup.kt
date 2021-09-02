package com.lucky.linkpal

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
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
    private var longitude: Double? = null
    private var latitude: Double? = null
    private lateinit var phone_number: String
    private lateinit var password: String
    private lateinit var gender: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    companion object {
        const val REQUEST_LOCATION = 100
    }

    /*Updating location in the case that the device cannot access it. For whatever reason*/
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                longitude = location.longitude
                latitude = location.latitude
            }
        }
    }

    /*After the user has been prompted to switch location on, pick the response and work with whichever they chose*/
    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                pickLocation()
            }
            else {
                Toast.makeText(applicationContext, "Your location will be set to unknown.", Toast.LENGTH_SHORT).show()
                registerEmployer()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer__signup)

        /*Setting up a location request*/
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        button_sign_up_employer.setSafeOnClickListener {
            if (!validateNames() || !validatePhoneNumber() || !validatePassword() || !validateGender()) {
                return@setSafeOnClickListener
            }
            pickLocation()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                        pickLocation()
                    }
                } else {
                    Toast.makeText(applicationContext, "Case 2. Settings are on. But app has no access", Toast.LENGTH_SHORT).show()
                    registerEmployer()
                }
                return
            }
        }

    }

    private fun pickLocation() {
        /*GETTING CURRENT LOCATION SETTINGS
* We do this by LocationSettingsRequest.builder and adding a LocationRequest object to it as below*/
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        //Next, check whether the current location settings are satisfied as below
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        //All location settings are satisfied. The client can make location requests
        task.addOnSuccessListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)

            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            longitude = location.longitude
                            latitude = location.latitude
                        } else {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                            )
                        }
                    }
                registerEmployer()
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    /*SEND EMPLOYER DATA TO SERVER*/
    private fun registerEmployer() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.register,
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
                    Log.d("LTM_DEBUG", res)
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
                    emp["phone"] = phone_number
                    emp["password"] = password
                    emp["gender"] = gender
                    if (longitude != null && latitude != null) {
                        emp["longitude"] = longitude.toString()
                        emp["latitude"] = latitude.toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return emp
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}
