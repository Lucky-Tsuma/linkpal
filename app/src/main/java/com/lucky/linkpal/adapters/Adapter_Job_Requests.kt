package com.lucky.linkpal.adapters

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.data_classes.Job_Request
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Adapter_Job_Requests(
    private var context: Context,
    private var list: MutableList<Job_Request>
) : BaseAdapter() {
    var sh: SharedPreferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    var firstname = sh.getString("firstname", null)
    var lastname = sh.getString("lastname", null)
    var employer_id = sh.getInt("user_id", 0)
    private lateinit var Addresses: List<Address>
    private lateinit var worker_id: String

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_job_requests, parent, false)

        val job_id = mView?.findViewById<TextView>(R.id.job_id)
        val applicant_id = mView?.findViewById<TextView>(R.id.applicant_id)
        val bidding_amount = mView?.findViewById<TextView>(R.id.price)
        val proposal = mView?.findViewById<TextView>(R.id.proposal)
        val username = mView?.findViewById<TextView>(R.id.username)
        val worker_location = mView?.findViewById<TextView>(R.id.worker_location)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val request_date = mView?.findViewById<TextView>(R.id.request_date)
        val phone_number = mView?.findViewById<TextView>(R.id.phone_number)
        val rating = mView?.findViewById<RatingBar>(R.id.user_rating)
        val recruit = mView?.findViewById<Button>(R.id.button_recruit)
        val decline = mView?.findViewById<Button>(R.id.button_decline)

        val longitude = list[position].longitude
        val latitude = list[position].latitude

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            Addresses =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (Addresses.isNotEmpty()) {
                if (worker_location != null) {
                    worker_location.text = Addresses[0].adminArea
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (job_id != null) {
            job_id.text = list[position].job_id
        }
        if (applicant_id != null) {
            applicant_id.text = list[position].user_id
        }
        if (job_title != null) {
            job_title.text = list[position].job_title
        }
        if (username != null) {
            username.text = list[position].firstname + " " + list[position].lastname
        }
        if (request_date != null) {
            request_date.text = "Request date: " + list[position].request_date
        }
        if (phone_number != null) {
            phone_number.text = list[position].phone_number
        }
        if (proposal != null) {
            proposal.text = list[position].proposal
        }
        if (bidding_amount != null) {
            bidding_amount.text = "My price: " + list[position].bidding_amount + "/="
        }
        if (rating != null) {
            rating.rating = list[position].rating
        }

        decline!!.setSafeOnClickListener {
            declineRequest(list[position].job_id, list[position].user_id)
        }
        recruit!!.setSafeOnClickListener {
            recruitWorker(list[position].job_id, list[position].user_id)
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

    private fun declineRequest(job_id: String, applicant_id: String) {
        val request = object : VolleyFileUploadRequest(
            Method.POST, URLs.delete_job_request,
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
                val info = HashMap<String, String>()
                try {
                    info["user_id"] = applicant_id
                    info["job_id"] = job_id
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return info
            }
        }
        /*request.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }*/
        Volley.newRequestQueue(context).add(request)
    }

    private fun recruitWorker(jobId: String, worker_id: String) {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.recruit,
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
                val info = HashMap<String, String>()
                try {
                    info["job_id"] = jobId
                    info["worker_id"] = worker_id
                    info["employer_id"] = employer_id.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return info
            }
        }
        /*request.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }*/
        Volley.newRequestQueue(context).add(request)
    }
}