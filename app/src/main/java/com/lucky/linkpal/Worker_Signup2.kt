package com.lucky.linkpal

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_worker__signup2.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*


class Worker_Signup2 : AppCompatActivity() {

    private var status: Boolean = true
    private lateinit var userLocation: String
    private lateinit var userJobField: String
    private lateinit var jsonQueue: RequestQueue
    private val requestCode = 100
    private lateinit var profileDescription: String
    private lateinit var workerViewModel: WorkerSignup2ViewModel

    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var gender: String
    private var userJobField0: Int? = null
    private var userLocation0: Int? = null

    private var imageData: ByteArray? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__signup2)

        /*Getting data from previous activity*/
        val intent = intent
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()
        email = intent.getStringExtra("email").toString()
        phone = intent.getStringExtra("phone").toString()
        password = intent.getStringExtra("password").toString()
        gender = intent.getStringExtra("gender").toString()

        jsonQueue = Volley.newRequestQueue(this)

        /*Creating a ViewModel instance to help retain data in case the system destroys this activity*/
        workerViewModel = ViewModelProviders.of(this).get(WorkerSignup2ViewModel::class.java)

        /*ON PROFILE PICTURE*/
        profile_pic.setSafeOnClickListener {
            val picIntent = Intent(Intent.ACTION_PICK)
            picIntent.type = "image/*"
            startActivityForResult(picIntent, requestCode)
        }

        if (workerViewModel.getImage() != null) {
            profile_pic.setImageDrawable(workerViewModel.getImage())
        }
        /*ON JOB FIELD*/
        listview_job_field.visibility = View.GONE

        job_field.setSafeOnClickListener {
            populateJobFieldMenu()
            listview_job_field.visibility = View.VISIBLE
        }

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

        if (workerViewModel.getJobField() != null) {
            job_field.text = workerViewModel.getJobField()
            userJobField = workerViewModel.getJobField()!!
        }
        if (workerViewModel.getJobField0() != null) {
            userJobField0 = workerViewModel.getJobField0()
        }

        /*ON LOCATION*/
        listview_location.visibility = View.GONE

        location.setSafeOnClickListener {
            populateLocationMenu()
            listview_location.visibility = View.VISIBLE
        }

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

        if (workerViewModel.getLocation() != null) {
            location.text = workerViewModel.getLocation()
            userLocation = workerViewModel.getLocation()!!
        }
        if (workerViewModel.getLocation0() != null) {
            userLocation0 = workerViewModel.getLocation0()
        }

        /*ON SIGN UP BUTTON*/
        button_sign_up_worker.setSafeOnClickListener {
            checkUserInput()
            if (status) {
                checkForImage()
            }
        }
    }/*onCreate method ends here*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == requestCode) {
            uri = data?.data!!
            profile_pic.setImageURI(uri)
            createImageData(uri!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*In case the system destroys this Activity, selected image on profile_pic will be retained using profilePicViewModel and
        * reassigned during onCreate()*/
        if (profile_pic.drawable != null) {
            workerViewModel.setImage(profile_pic.drawable)
        }

        if (job_field.text != null) {
            workerViewModel.setJobField(job_field.text.toString())
            if (workerViewModel.getJobField0() != null) {
                workerViewModel.setJobField0(userJobField0!!)
            }
        }

        if (location.text != null) {
            workerViewModel.setLocation(location.text.toString())
            if (workerViewModel.getLocation0() != null) {
                workerViewModel.setLocation0(userLocation0!!)
            }
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
                        val adapterSpecialty = SimpleAdapter(
                                this, specialtyList, R.layout.activity_listview_jobfield,
                                arrayOf("specialty_id", "name"), intArrayOf(R.id.specialty_id, R.id.name)
                        )
                        listview_job_field.adapter = adapterSpecialty

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

                        val adapterLocation = SimpleAdapter(
                                this, locationList, R.layout.activity_listview_location,
                                arrayOf("location_id", "name"), intArrayOf(R.id.location_id, R.id.name)
                        )
                        listview_location.adapter = adapterLocation
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

    /*ON SIGN UP BUTTON*/
    private fun checkUserInput() {
        /*reset in case there problems with previous user input*/
        status = true
        location.setHintTextColor(Color.parseColor("#737373"))
        job_field.setHintTextColor(Color.parseColor("#737373"))
        profile_description.setHintTextColor(Color.parseColor("#737373"))
        profile_description.setTextColor(Color.BLACK)

        /*get user input Strings*/
        userLocation = location.text.toString()
        userJobField = job_field.text.toString()
        profileDescription = profile_description.text.toString().trim()

        /*check for an empty field*/
        if (status) {
            if (userLocation.isEmpty() || userJobField.isEmpty() || profileDescription.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "Please fill the highlighted fields",
                        Toast.LENGTH_SHORT
                ).show()
                status = false
                if (userLocation.isEmpty()) {
                    location.setHintTextColor(Color.RED)
                }
                if (userJobField.isEmpty()) {
                    job_field.setHintTextColor(Color.RED)
                }
                if (profileDescription.isEmpty()) {
                    profile_description.setHintTextColor(Color.RED)
                }
            }
        }

        /*check for length of profile description*/
        if (status) {
            if (profileDescription.length < 10) {
                Toast.makeText(
                        applicationContext,
                        "Profile description should be about 10 characters",
                        Toast.LENGTH_SHORT
                ).show()
                profile_description.setTextColor(Color.RED)
                status = false
            }
        }
    }

    /*SEND WORKER DATA TO SERVER*/
    private fun checkForImage() {
        if (uri == null) {
            Toast.makeText(
                    this,
                    "Please pick a profile picture and retry",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            val uriString: String = uri.toString()
            /*Constructor below creates a new File instance by converting the given file: URI into an abstract pathname.*/
            val myFile = File(uriString)
            val displayName: String?
            /*If the uriString refers to a path in the phone's internal storage i.e not a memory card or other sec storage*/
            if (uriString.startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver.query(uri!!, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        uploadMultipart(displayName)
                    }
                } finally {
                    cursor?.close()
                }
                /*uriString in this case refers to a path in the sec storage eg. Memory card*/
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.name
                uploadMultipart(displayName)
            }
        }
    }

    private fun uploadMultipart(imageName: String) {
        imageData ?: return

        val dialog: Dialog = AlertDialog.Builder(this).setView(R.layout.loading).create()
        dialog.show()

        val request = object : VolleyFileUploadRequest(Method.POST, URLs.user_register,
                Response.Listener { response ->
                    dialog.dismiss()
                    val res = String(response.data)
                    try {
                        val obj = JSONObject(res)
                        val msg: String = obj.getString("message")
                        if (obj.getBoolean("error")) {
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        } else {
                            val adb: AlertDialog.Builder = AlertDialog.Builder(this)
                            adb.setTitle("Notification").setMessage(msg).setCancelable(false)
                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        }
                    } catch (e: JSONException) {
                        dialog.dismiss()
                        e.printStackTrace()
                        Toast.makeText(this, "Oops! An error occurred", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    dialog.dismiss()
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val worker = HashMap<String, String>()
                try {
                    worker["firstName"] = firstname
                    worker["lastName"] = lastname
                    worker["email"] = email
                    worker["phone"] = phone
                    worker["password"] = password
                    worker["gender"] = gender
                    worker["jobField"] = userJobField0.toString()
                    worker["location"] = userLocation0.toString()
                    worker["profileSummary"] = profileDescription
                    worker["imageName"] = imageName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return worker
            }

            override fun getByteData(): MutableMap<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                params["imageFile"] = FileDataPart(email, imageData!!, "")
                return params
            }
        }/*VolleyFileUploadRequest(...) ends here*/
        Volley.newRequestQueue(this).add(request)
    }/*uploadImage() ends here*/

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }
}

