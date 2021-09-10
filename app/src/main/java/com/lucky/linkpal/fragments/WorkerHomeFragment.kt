package com.lucky.linkpal.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.adapters.Available_Job_Adapter
import com.lucky.linkpal.data_classes.Available_Job
import com.lucky.linkpal.utils.URLs
import kotlinx.android.synthetic.main.fragment_worker_home.*
import org.json.JSONException

class WorkerHomeFragment : Fragment() {
    private var user_id: Int? = null
    private lateinit var jobs: MutableList<Available_Job>
    private lateinit var jsonQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sh: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)

        jsonQueue = Volley.newRequestQueue(context)

        showAvailableJobs()

        return inflater.inflate(R.layout.fragment_worker_home, container, false)
    }

    private fun showAvailableJobs() {

        val availableJobsReq = JsonObjectRequest(
            Request.Method.GET, URLs.available_jobs, null,
            { response ->

                jobs = mutableListOf()

                val msg = response.getString("message")

                if (msg == "true") {
                    try {
                        val jsonArray = response.getJSONArray("available_job")
                        for (i in 0 until jsonArray.length()) {
                            val job = jsonArray.getJSONObject(i)

                            val job_id = job.getString("job_id")
                            val firstname = job.getString("firstname")
                            val lastname = job.getString("lastname")
                            val job_description = job.getString("job_description")
                            val post_date = job.getString("post_date")
                            val job_specialty = job.getString("job_specialty")
                            val employer_phone = job.getString("employer_phone")
                            val longitude = job.getString("longitude")
                            val latitude = job.getString("latitude")
                            jobs.add(
                                Available_Job(
                                    job_id,
                                    firstname,
                                    lastname,
                                    job_description,
                                    post_date,
                                    job_specialty,
                                    employer_phone,
                                    longitude,
                                    latitude
                                )
                            )
                        }
                        val jobs_adapter = Available_Job_Adapter(requireContext(), jobs)
                        list_view_available_jobs.adapter = jobs_adapter

                    } catch (e: JSONException) {
                        Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    txtView_no_jobs.visibility = View.VISIBLE
                }
            }, { error ->
                error.printStackTrace()
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(
                        context,
                        "Error connecting to the internet",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        /*availableJobsReq.retryPolicy = object : RetryPolicy {
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
        jsonQueue.add(availableJobsReq)
    }

}