package com.lucky.linkpal.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.adapters.Adapter_Job_Requests
import com.lucky.linkpal.data_classes.Job_Request
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.fragment_job_requests.*
import org.json.JSONException
import org.json.JSONObject

class JobRequestsFragment : Fragment() {
    private var employer_id: Int? = null
    private var longitude: String? = null
    private var latitude: String? = null
    private lateinit var job_requests: MutableList<Job_Request>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sh: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        employer_id = sh.getInt("user_id", 0)
        longitude = sh.getString("longitude", null)
        latitude = sh.getString("latitude", null)

        showJobRequests()

        return inflater.inflate(R.layout.fragment_job_requests, container, false)
    }

    private fun showJobRequests() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.job_requests,
            Response.Listener { response ->

                job_requests = mutableListOf()
                val res = String(response.data)
                val obj = JSONObject(res)

                val msg = obj.getString("message")

                if (msg == "true") {
                    try {
                        val jsonArray = obj.getJSONArray("requests")
                        for (i in 0 until jsonArray.length()) {
                            val job = jsonArray.getJSONObject(i)
                            val job_id = job.getString("job_id")
                            val user_id = job.getString("user_id")
                            val bidding_amount = job.getString("bidding_amount").toInt()
                            val proposal = job.getString("proposal")
                            val firstname = job.getString("firstname")
                            val lastname = job.getString("lastname")
                            val longitude = job.getString("longitude")
                            val latitude = job.getString("latitude")
                            val job_title = job.getString("job_title")
                            val request_date = job.getString("request_date")
                            val phone_number = job.getString("phone_number")
                            val rating = job.getString("rating").toFloat()
                            val distance = job.getString("distance").toFloat()
                            job_requests.add(
                                Job_Request(
                                    job_id,
                                    user_id,
                                    bidding_amount,
                                    proposal,
                                    firstname,
                                    lastname,
                                    longitude,
                                    latitude,
                                    job_title,
                                    request_date,
                                    phone_number,
                                    rating,
                                    distance
                                )
                            )
                        }
                        val job_requests_adapter =
                            Adapter_Job_Requests(
                                requireContext(),
                                job_requests
                            )
                        list_view_job_requests.adapter = job_requests_adapter

                    } catch (e: JSONException) {
                        Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    txtView_no_job_requests.visibility = View.VISIBLE
                }
            },
            Response.ErrorListener { error ->
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(
                        context,
                        "Error connecting to the internet",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val user = HashMap<String, String>()
                try {
                    user["employer_id"] = employer_id.toString()
                    user["longitude"] = longitude.toString()
                    user["latitude"] = latitude.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return user
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