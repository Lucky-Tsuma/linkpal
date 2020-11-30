package com.lucky.fundiapp

import android.graphics.Color
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
import kotlinx.android.synthetic.main.activity_worker__signup2.*
import org.json.JSONException
import java.util.*

class Worker_Signup2 : AppCompatActivity() {

    private var status: Boolean = true
    private lateinit var userLocation: String
    private lateinit var userJobField: String
    private lateinit var profileDescription: String
    private lateinit var jsonQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__signup2)

        jsonQueue = Volley.newRequestQueue(this)

        /*ON JOB FIELD*/
        listview_job_field.visibility = View.GONE

        listview_job_field.setOnItemClickListener { _, view, _, _ ->
            val selectedItem = view as LinearLayout
            val textViewJobField = selectedItem.getChildAt(1) as TextView
            val stringJobField = textViewJobField.text.toString()
            job_field.text = stringJobField
            listview_job_field.visibility = View.GONE
        }

        job_field.setOnClickListener {
            populateJobFieldMenu()
            listview_job_field.visibility = View.VISIBLE
        }

        /*ON LOCATION*/
        listview_location.visibility = View.GONE

        listview_location.setOnItemClickListener { _, view, _, _ ->
            val selectedItem = view as LinearLayout
            val textViewLocation = selectedItem.getChildAt(1) as TextView
            val stringLocation = textViewLocation.text.toString()
            location.text = stringLocation
            listview_location.visibility = View.GONE
        }

        location.setOnClickListener {
            populateLocationMenu()
            listview_location.visibility = View.VISIBLE
        }

        /*ON SIGN UP BUTTON*/
        button_sign_up_worker.setOnClickListener {
            checkUserInput()
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
            }, Response.ErrorListener { error -> VolleyLog.e("Error: ", error.message) })
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
        Response.ErrorListener { error -> VolleyLog.e("Error: ", error.message) })

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
}
