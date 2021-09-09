package com.lucky.linkpal.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

class Adapter_Job_Requests(
    private var context: Context,
    private var list: MutableList<Job_Request>
) : BaseAdapter() {
    var sh: SharedPreferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    var firstname = sh.getString("firstname", null)
    var lastname = sh.getString("lastname", null)
    var phone_number = sh.getString("phone_number", null)
    var employer_id = sh.getInt("user_id", 0)
    private lateinit var worker_id: String
    private lateinit var message: String

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_job_requests, parent, false)

        val job_id = mView?.findViewById<TextView>(R.id.job_id)
        val applicant_id = mView?.findViewById<TextView>(R.id.applicant_id)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val job_location = mView?.findViewById<TextView>(R.id.job_location)
        val username = mView?.findViewById<TextView>(R.id.username)
        val request_date = mView?.findViewById<TextView>(R.id.request_date)
        val email = mView?.findViewById<TextView>(R.id.email)
        val recruit = mView?.findViewById<Button>(R.id.button_recruit)
        val decline = mView?.findViewById<Button>(R.id.button_decline)

        val date = list[position].request_date
        val jobTitle = list[position].job_title
        val email_address = list[position].email
        worker_id = list[position].user_id

        if (job_id != null) {
            job_id.text = list[position].job_id
        }
        if (applicant_id != null) {
            applicant_id.text = list[position].user_id
        }
        if (job_title != null) {
            job_title.text = list[position].job_title
        }
        if (job_location != null) {
            job_location.text = list[position].job_location
        }
        if (username != null) {
            username.text = list[position].firstname + " " + list[position].lastname
        }
        if (request_date != null) {
            request_date.text = "Request date: " + list[position].request_date
        }
        if (email != null) {
            email.text = list[position].email
        }

        message =
            "Your job request made on $date to work on $firstname $lastname's $jobTitle job has been accepted. Contact them through " +
                    "$phone_number to catch up on further details."

        decline!!.setSafeOnClickListener {
            declineRequest(list[position].job_id, list[position].user_id)
        }
        recruit!!.setSafeOnClickListener {
            recruitWorker(list[position].job_id, list[position].user_id, email_address)
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
        request.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        Volley.newRequestQueue(context).add(request)
    }

    private fun recruitWorker(jobId: String, worker_id: String, email_address: String) {
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
                    info["message"] = message
                    info["email_address"] = email_address
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return info
            }
        }
        request.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        Volley.newRequestQueue(context).add(request)
    }
}