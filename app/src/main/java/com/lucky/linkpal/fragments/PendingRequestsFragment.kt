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
import com.lucky.linkpal.adapters.Adapter_Pending_Requests
import com.lucky.linkpal.data_classes.Pending_Request
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.fragment_pending_requests.*
import org.json.JSONException
import org.json.JSONObject

class PendingRequestsFragment : Fragment() {
    private var user_id: Int? = null
    private lateinit var pending_requests: MutableList<Pending_Request>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sh: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)

        showPendingRequests()

        return inflater.inflate(R.layout.fragment_pending_requests, container, false)
    }

    private fun showPendingRequests() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.pending_requests,
            Response.Listener { response ->

                pending_requests = mutableListOf()
                val res = String(response.data)
                val obj = JSONObject(res)

                val msg = obj.getString("message")

                if (msg == "true") {
                    try {
                        val jsonArray = obj.getJSONArray("requests")
                        for (i in 0 until jsonArray.length()) {
                            val job = jsonArray.getJSONObject(i)
                            val job_id = job.getString("job_id")
                            val bidding_amount = job.getString("bidding_amount")
                            val job_title = job.getString("job_title")
                            val longitude = job.getString("longitude")
                            val latitude = job.getString("latitude")
                            val firstname = job.getString("firstname")
                            val lastname = job.getString("lastname")
                            val request_date = job.getString("request_date")
                            pending_requests.add(
                                Pending_Request(
                                    job_id,
                                    bidding_amount,
                                    job_title,
                                    longitude,
                                    latitude,
                                    firstname,
                                    lastname,
                                    request_date
                                )
                            )
                        }
                        val pending_requests_adapter =
                            Adapter_Pending_Requests(
                                requireContext(),
                                pending_requests
                            )
                        list_view_pending_requests.adapter = pending_requests_adapter

                    } catch (e: JSONException) {
                        Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    txtView_no_pending_requests.visibility = View.VISIBLE
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
                    user["user_id"] = user_id.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return user
            }
        }
        /* request.retryPolicy = object : RetryPolicy {
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