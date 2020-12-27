package com.lucky.fundiapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.number.IntegerWidth
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lucky.fundiapp.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_worker__signup2.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Worker_Signup2 : AppCompatActivity() {

    private var status: Boolean = true
    private lateinit var userLocation: String
    private lateinit var userJobField: String
    private lateinit var jsonQueue: RequestQueue
    private val REQUEST_CODE = 100
    private lateinit var profileDescription: String

    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var gender: String
    private  var userJobField0: Int? = null
    private  var userLocation0: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__signup2)

        val intent = intent
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()
        email = intent.getStringExtra("email").toString()
        phone = intent.getStringExtra("phone").toString()
        password = intent.getStringExtra("password").toString()
        gender = intent.getStringExtra("gender").toString()

        jsonQueue = Volley.newRequestQueue(this)

        /*ON PROFILE PICTURE*/
        profile_pic.setSafeOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

        /*ON JOB FIELD*/
        listview_job_field.visibility = View.GONE

        listview_job_field.setOnItemClickListener { _, view, _, _ ->
            val selectedItem = view as LinearLayout
            val textViewJobField = selectedItem.getChildAt(1) as TextView
            val textViewJobField0 = selectedItem.getChildAt(0) as TextView
            val stringJobField = textViewJobField.text.toString()
            val stringJobField0 = textViewJobField0.text.toString()
            job_field.text = stringJobField
            userJobField0 = stringJobField0.toInt()/*A key we will send to database*/
            listview_job_field.visibility = View.GONE
        }

        job_field.setSafeOnClickListener {
            populateJobFieldMenu()
            listview_job_field.visibility = View.VISIBLE
        }

        /*ON LOCATION*/
        listview_location.visibility = View.GONE

        listview_location.setOnItemClickListener { _, view, _, _ ->
            val selectedItem = view as LinearLayout
            val textViewLocation = selectedItem.getChildAt(1) as TextView
            val textViewLocation0 = selectedItem.getChildAt(0) as TextView
            val stringLocation = textViewLocation.text.toString()
            val stringLocation0 = textViewLocation0.text.toString()
            location.text = stringLocation
            userLocation0 = stringLocation0.toInt()/*A key we will send to database*/
            listview_location.visibility = View.GONE
        }

        location.setSafeOnClickListener {
            populateLocationMenu()
            listview_location.visibility = View.VISIBLE
        }

        /*ON SIGN UP BUTTON*/
        button_sign_up_worker.setSafeOnClickListener {
            checkUserInput()
            if(status) {registerWorker()}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            profile_pic.setImageURI(data?.data)
        }
    }

    /*ON JOB FIELD*/
    private fun populateJobFieldMenu() {

        val specialtyReq = JsonObjectRequest(Request.Method.GET, URLs.specialty_get, null,
            Response.Listener { response ->
                try {
                    val specialtyList = ArrayList<HashMap<String, String>>()

                    val jsonArray = response.getJSONArray("specialty")

                    for (i in 0 until jsonArray.length()) {
                        val specialty = jsonArray.getJSONObject(i)
                        val id = specialty.getString("specialty_id")
                        val name = specialty.getString("name")

                        val mapSpecialty = HashMap<String, String>()

                        mapSpecialty["specialty_id"] = id
                        mapSpecialty["name"] = name

                        specialtyList.add(mapSpecialty)
                    }
                    val adapterSpecialty = SimpleAdapter(this, specialtyList , R.layout.activity_listview_jobfield,
                        arrayOf("specialty_id", "name"), intArrayOf(R.id.specialty_id, R.id.name))
                    listview_job_field.adapter = adapterSpecialty

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> error.printStackTrace()
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(applicationContext, "Check your internet connection. Or try again later.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        jsonQueue.add(specialtyReq)

    }

//
    /*ON LOCATION*/
    private  fun populateLocationMenu() {

    val locationReq = JsonObjectRequest(Request.Method.GET, URLs.location_get, null,
        Response.Listener { response ->
            try {
                val locationList = ArrayList<HashMap<String, String>>()
                val jsonArray = response.getJSONArray("location")

                for (i in 0 until jsonArray.length()) {
                    val location = jsonArray.getJSONObject(i)
                    val id = location.getString("location_id")
                    val name = location.getString("name")

                    val locationMap = HashMap<String, String>()
                    locationMap["location_id"] = id
                    locationMap["name"] = name

                    locationList.add(locationMap)
                }

                val adapterLocation = SimpleAdapter(this, locationList, R.layout.activity_listview_location,
                    arrayOf("location_id", "name"), intArrayOf(R.id.location_id, R.id.name))
                listview_location.adapter = adapterLocation
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
        Response.ErrorListener { error -> error.printStackTrace()
            if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                Toast.makeText(applicationContext, "Check your internet connection. Or try again later.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })

    jsonQueue.add(locationReq)
}

    /*ON SIGN UP BUTTON*/
    private fun checkUserInput() {
        /*reset in case there problems with previous user input*/
        status = true
        location.setBackgroundColor(Color.WHITE)
        job_field.setBackgroundColor(Color.WHITE)
        profile_description.setBackgroundColor(Color.WHITE)

        /*get user input Strings*/
        userLocation = location.text.toString()
        userJobField = job_field.text.toString()
        profileDescription = profile_description.text.toString().trim()

        /*check for an empty field*/
        if(status) {
            if(userLocation.isEmpty() || userJobField.isEmpty() || profileDescription.isEmpty()) {
                Toast.makeText(applicationContext, "Please fill the highlighted fields", Toast.LENGTH_SHORT).show()
                status = false
                if(userLocation.isEmpty()) {location.setBackgroundColor(Color.RED)}
                if(userJobField.isEmpty()) {job_field.setBackgroundColor(Color.RED)}
                if(profileDescription.isEmpty()) {profile_description.setBackgroundColor(Color.RED)}
            }
        }

        /*check for length of profile description*/
        if(status){
            if(profileDescription.length < 50) {
                Toast.makeText(applicationContext, "Profile description should be about 50 characters", Toast.LENGTH_SHORT).show()
                profile_description.setBackgroundColor(Color.RED)
                status = false
            }
        }

    }

    /*SEND WORKER DATA TO SERVER*/
    private fun registerWorker() {
        val requestQueue = Volley.newRequestQueue(this)

        val worker = JSONObject()

        try {
            worker.put("firstName", firstname)
            worker.put("lastName", lastname)
            worker.put("email", email)
            worker.put("phone", phone)
            worker.put("password", password)
            worker.put("gender", gender)
            worker.put("jobField", userJobField0)
            worker.put("location", userLocation0)
            worker.put("profileSummary", profileDescription)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val req = JsonObjectRequest(Request.Method.POST, URLs.worker_register, worker,
            Response.Listener { _ ->  Toast.makeText(applicationContext, "Registration Successful. You may" +
                    "log in to your account now", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error -> error.printStackTrace()
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(applicationContext, "Check your internet connection. Or try again later.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })

        requestQueue.add(req)
    }
}
