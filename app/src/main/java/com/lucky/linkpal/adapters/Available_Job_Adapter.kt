package com.lucky.linkpal.adapters

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.data_classes.Available_Job
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.nav_drawer_header_employer.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Available_Job_Adapter(
    private var context: Context,
    private var list: MutableList<Available_Job>
) : BaseAdapter() {
    private val sh: SharedPreferences =
        context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    val user = sh.getInt("user_id", 0)
    val firstname = sh.getString("firstname", null)
    val lastname = sh.getString("lastname", null)
    private lateinit var Addresses: List<Address>
    lateinit var message: String

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_available_jobs, parent, false)

        val job_id = mView?.findViewById<TextView>(R.id.job_id)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val employer_phone = mView?.findViewById<TextView>(R.id.employer_phone)
        val job_summary = mView?.findViewById<TextView>(R.id.job_summary)
        val posted_by = mView?.findViewById<TextView>(R.id.posted_by)
        val post_date = mView?.findViewById<TextView>(R.id.post_date)
        val apply_job = mView?.findViewById<Button>(R.id.button_apply_job)
        val location = mView?.findViewById<TextView>(R.id.job_location)

        val longitude = list[position].longitude
        val latitude = list[position].latitude

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            Addresses =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (Addresses.isNotEmpty()) {
                if (location != null) {
                    location.text = Addresses[0].adminArea
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (job_id != null) {
            job_id.text = list[position].job_id
        }
        if (job_title != null) {
            job_title.text = list[position].job_specialty
        }
        if (job_summary != null) {
            job_summary.text = list[position].job_description
        }
        if (posted_by != null) {
            posted_by.text = list[position].firstname + " " + list[position].lastname
        }
        if (post_date != null) {
            post_date.text = list[position].post_date
        }
        if (employer_phone != null) {
            employer_phone.text = list[position].employer_phone
        }

        apply_job!!.setSafeOnClickListener {
            /*applyJob(list[position].job_id)*/
        }

        return mView
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    private fun applyJob(jobId: String) {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.apply_job,
            Response.Listener { response ->

                val res = String(response.data)

                try {
                    val obj = JSONObject(res)
                    val msg: String = obj.getString("message")
                    val error: Boolean = obj.getBoolean("error")

                    if (!error) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(
                        context,
                        "Error connecting to the internet.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val application = HashMap<String, String>()
                try {
                    application["user_id"] = user.toString()
                    application["job_id"] = jobId
                    application["message"] = message
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return application
            }
        }
        Volley.newRequestQueue(context).add(request)
    }
}