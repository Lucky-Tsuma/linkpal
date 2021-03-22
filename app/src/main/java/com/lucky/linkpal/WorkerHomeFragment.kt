package com.lucky.linkpal

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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_employer_home.*
import kotlinx.android.synthetic.main.fragment_worker_home.*
import org.json.JSONException
import org.json.JSONObject

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
            activity!!.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)

        jsonQueue = Volley.newRequestQueue(context)

        showAvailableJobs()

        return inflater.inflate(R.layout.fragment_worker_home, container, false)
    }

    private fun showAvailableJobs() {

        val availableJobsReq = JsonObjectRequest(
            Request.Method.GET, URLs.available_jobs, null,
            Response.Listener { response ->

                jobs = mutableListOf()

                val msg = response.getString("message")

                if (msg == "true") {
                    try {
                        val jsonArray = response.getJSONArray("available_job")
                        for (i in 0 until jsonArray.length()) {
                            val job = jsonArray.getJSONObject(i)
                            val job_id = job.getString("job_id").toInt()
                            val firstname = job.getString("firstname")
                            val lastname = job.getString("lastname")
                            val job_description = job.getString("job_description")
                            val amount = job.getString("amount")
                            val post_date = job.getString("post_date")
                            val job_specialty = job.getString("job_specialty")
                            val job_location = job.getString("job_location")
                            jobs.add(
                                Available_Job(
                                    job_id,
                                    firstname,
                                    lastname,
                                    job_description,
                                    amount,
                                    post_date,
                                    job_specialty,
                                    job_location
                                )
                            )
                        }
                        val jobs_adapter = Available_Job_Adapter(context!!, jobs)
                        list_view_available_jobs.adapter = jobs_adapter

                    } catch (e: JSONException) {
                        Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    txtView_no_jobs.visibility = View.VISIBLE
                }
            }, Response.ErrorListener { error ->
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
        jsonQueue.add(availableJobsReq)
    }
}