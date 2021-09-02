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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.utils.FileDataPart
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import com.lucky.linkpal.utils.WorkerSignup2ViewModel
import kotlinx.android.synthetic.main.activity_worker__signup2.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*


class Worker_Signup2 : AppCompatActivity() {

    private var status: Boolean = true
    private var longitude: Double? = null
    private var latitude: Double? = null
    private lateinit var userJobField: String
    private lateinit var jsonQueue: RequestQueue
    private lateinit var profileDescription: String
    private lateinit var workerViewModel: WorkerSignup2ViewModel

    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var gender: String
    private var userJobField0: Int? = null

    private var imageData: ByteArray? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__signup2)

        /*Getting data from previous activity*/
        val intent = intent
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()
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
            resultLauncher.launch(picIntent)
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

        /*ON SIGN UP BUTTON*/
        button_sign_up_worker.setSafeOnClickListener {
            checkUserInput()
            if (status) {
                checkForImage()
            }
        }
    }/*onCreate method ends here*/

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
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

    }

    /*ON JOB FIELD*/
    private fun populateJobFieldMenu() {

        val specialtyReq = JsonObjectRequest(Request.Method.GET, URLs.get_specialty, null,
            /*Lambda returning a type Response.Listener*/{ response ->
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
                    listview_job_field.adapter = adapterSpecialty

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, /*Lambda returning type Response.ErrorListener*/{ error ->
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

    /*ON SIGN UP BUTTON*/
    private fun checkUserInput() {
        /*reset in case there problems with previous user input*/
        status = true
        job_field.setHintTextColor(Color.parseColor("#737373"))
        profile_description.error = null

        /*get user input Strings*/
        userJobField = job_field.text.toString()
        profileDescription = profile_description.editText.toString().trim()

        /*check for an empty field*/
        if (status) {
            if (userJobField.isEmpty() || profileDescription.isEmpty()) {
                status = false
                if (userJobField.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Please fill the highlighted field",
                        Toast.LENGTH_SHORT
                    ).show()
                    job_field.setHintTextColor(Color.RED)
                }
                if (profileDescription.isEmpty()) {
                    profile_description.error = "Cannot be empty"
                }
            }
        }

        /*check for length of profile description*/
        if (status) {
            if (profileDescription.length < 10) {
                profile_description.error = "Description too short (10 characters minimum)."
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

        val request = object : VolleyFileUploadRequest(Method.POST, URLs.register,
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
                    worker["phone"] = phone
                    worker["password"] = password
                    worker["gender"] = gender
                    worker["jobField"] = userJobField0.toString()
                    worker["longitude"] = longitude.toString()
                    worker["latitude"] = latitude.toString()
                    worker["profileSummary"] = profileDescription
                    worker["imageName"] = imageName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return worker
            }

            override fun getByteData(): MutableMap<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                params["imageFile"] =
                    FileDataPart(phone, imageData!!, "")
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

