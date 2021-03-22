package com.lucky.linkpal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_post__a__job.*
import org.json.JSONException
import org.json.JSONObject

class Post_A_Job : AppCompatActivity() {
    private var status: Boolean = true
    private var user_id: Int? = null
    private lateinit var job_type: String
    private lateinit var job_location: String
    private lateinit var job_description: String
    private var amount: Int? = null
    private lateinit var jsonQueue: RequestQueue
    private var job_location_0: Int? = null
    private var job_type_0: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post__a__job)

        val sh: SharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)

        jsonQueue = Volley.newRequestQueue(this)

        list_job_type.visibility = View.GONE
        txtView_job_type.setSafeOnClickListener {
            populateJobFieldMenu()
            list_job_type.visibility = View.VISIBLE
        }

        list_job_type.setOnItemClickListener { _, view, _, _ ->
            val selectedItem = view as LinearLayout
            val textViewJobType = selectedItem.getChildAt(1) as TextView
            val textViewJobType0 = selectedItem.getChildAt(0) as TextView
            val stringJobType = textViewJobType.text.toString()
            val stringJobType0 = textViewJobType0.text.toString()
            txtView_job_type.text = stringJobType
            job_type_0 = stringJobType0.toInt()/*A key we will send to database*/
            list_job_type.visibility = View.GONE
        }

        list_location.setOnItemClickListener { _, view, _, _ ->
            val selectedItem = view as LinearLayout
            val textViewLocation = selectedItem.getChildAt(1) as TextView
            val textViewLocation0 = selectedItem.getChildAt(0) as TextView
            val stringLocation = textViewLocation.text.toString()
            val stringLocation0 = textViewLocation0.text.toString()
            txtView_job_location.text = stringLocation
            job_location_0 = stringLocation0.toInt()/*A key we will send to database*/
            list_location.visibility = View.GONE
        }

        list_location.visibility = View.GONE
        txtView_job_location.setSafeOnClickListener {
            populateLocationMenu()
            list_location.visibility = View.VISIBLE
        }

        button_post.setSafeOnClickListener {
            checkUserInput()
            if (status) {
                postJob()
            }
        }

    }

    /*ON JOB TYPE*/
    private fun populateJobFieldMenu() {

        val specialtyReq = JsonObjectRequest(
            Request.Method.GET, URLs.specialty_get, null,
            Response.Listener { response ->
                try {
                    val specialtyList = ArrayList<HashMap<String, String>>()

                    val jsonArray = response.getJSONArray("specialty")

                    for (i in 0 until jsonArray.length()) {
                        val specialty = jsonArray.getJSONObject(i)
                        val id = specialty.getString("specialty_id")
                        val name = specialty.getString("specialty_name")

                        val mapSpecialty = HashMap<String, String>()

                        mapSpecialty["specialty_id"] = id
                        mapSpecialty["name"] = name

                        specialtyList.add(mapSpecialty)
                    }
                    val adapterSpecialty = SimpleAdapter(
                        this, specialtyList, R.layout.activity_listview_jobfield,
                        arrayOf("specialty_id", "name"), intArrayOf(R.id.specialty_id, R.id.name)
                    )
                    list_job_type.adapter = adapterSpecialty

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                error.printStackTrace()
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(
                        applicationContext,
                        "Check your internet connection. Or try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        jsonQueue.add(specialtyReq)

    }

    /*ON LOCATION*/
    private fun populateLocationMenu() {

        val locationReq = JsonObjectRequest(
            Request.Method.GET, URLs.location_get, null,
            Response.Listener { response ->
                try {
                    val locationList = ArrayList<HashMap<String, String>>()
                    val jsonArray = response.getJSONArray("location")

                    for (i in 0 until jsonArray.length()) {
                        val location = jsonArray.getJSONObject(i)
                        val id = location.getString("location_id")
                        val name = location.getString("location_name")

                        val locationMap = HashMap<String, String>()
                        locationMap["location_id"] = id
                        locationMap["name"] = name

                        locationList.add(locationMap)
                    }

                    val adapterLocation = SimpleAdapter(
                        this, locationList, R.layout.activity_listview_location,
                        arrayOf("location_id", "name"), intArrayOf(R.id.location_id, R.id.name)
                    )
                    list_location.adapter = adapterLocation
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(
                        applicationContext,
                        "Check your internet connection. Or try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })

        jsonQueue.add(locationReq)
    }

    private fun checkUserInput() {
        /*reset in case there was previous wrong input*/
        status = true
        txtView_job_type.setHintTextColor(Color.parseColor("#737373"))
        txtView_job_location.setHintTextColor(Color.parseColor("#737373"))
        editTxt_job_description.setHintTextColor(Color.parseColor("#737373"))
        editTxt_amount.setHintTextColor(Color.parseColor("#737373"))
        editTxt_job_description.setTextColor(Color.BLACK)
        editTxt_amount.setTextColor(Color.BLACK)

        /*Get user input and trim it*/
        job_type = txtView_job_type.text.toString()
        job_location = txtView_job_location.text.toString()
        job_description = editTxt_job_description.text.toString().trim()
        amount = if (editTxt_amount.text.toString() == "") {
            0
        } else {
            editTxt_amount.text.toString().toInt()
        }

        if (job_type.isEmpty() || job_location.isEmpty() || job_description.isEmpty() || editTxt_amount.text.toString() == "") {
            Toast.makeText(
                applicationContext,
                "Please fill the highlighted fields",
                Toast.LENGTH_SHORT
            ).show()
            status = false
            if (job_type.isEmpty()) {
                txtView_job_type.setHintTextColor(Color.RED)
            }
            if (job_location.isEmpty()) {
                txtView_job_location.setHintTextColor(Color.RED)
            }
            if (job_description.isEmpty()) {
                editTxt_job_description.setHintTextColor(Color.RED)
            }
            if (editTxt_amount.text.toString() == "") {
                editTxt_amount.setHintTextColor(Color.RED)
            }
        }

        if (status) {
            if (amount!! <= 0 || amount!! < 500) {
                status = false
                Toast.makeText(
                    applicationContext,
                    "Amount cannot be less than 500",
                    Toast.LENGTH_SHORT
                ).show()
                editTxt_amount.setHintTextColor(Color.RED)
            }
        }

        if (status) {
            if (job_description.length < 20) {
                status = false
                Toast.makeText(
                    applicationContext,
                    "Description should be at least 20 characters",
                    Toast.LENGTH_SHORT
                ).show()
                editTxt_job_description.setTextColor(Color.RED)
            }
        }
    }

    private fun postJob() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.post_job,
            Response.Listener { response ->

                val res = String(response.data)

                try {
                    val obj = JSONObject(res)
                    val msg: String = obj.getString("message")
                    val error: Boolean = obj.getBoolean("error")

                    if (!error) {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Employer_Homepage::class.java)
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
            override fun getParams(): MutableMap<String, String> {
                val job = HashMap<String, String>()
                try {
                    job["user_id"] = user_id.toString()
                    job["amount"] = amount.toString()
                    job["job_description"] = job_description
                    job["job_specialty"] = job_type_0.toString()
                    job["job_location"] = job_location_0.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return job
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Employer_Homepage::class.java)
        startActivity(intent)
    }
}