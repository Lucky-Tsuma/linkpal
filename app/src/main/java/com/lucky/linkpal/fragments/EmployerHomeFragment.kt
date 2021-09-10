package com.lucky.linkpal.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.Post_A_Job
import com.lucky.linkpal.R
import com.lucky.linkpal.adapters.Posted_Job_Adapter
import com.lucky.linkpal.data_classes.Posted_Job
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.fragment_employer_home.*
import kotlinx.android.synthetic.main.fragment_employer_home.view.*
import org.json.JSONException
import org.json.JSONObject

class EmployerHomeFragment : Fragment() {
    private var user_id: Int? = null
    private lateinit var jobs: MutableList<Posted_Job>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sh: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)

        showPostedJobs()

        val fragment = inflater.inflate(R.layout.fragment_employer_home, container, false)
        fragment.btn_post_a_job.setSafeOnClickListener {
            val intent = Intent(context, Post_A_Job::class.java)
            startActivity(intent)
        }
        return fragment
    }

    private fun showPostedJobs() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.employer_job_posts,
            Response.Listener { response ->

                jobs = mutableListOf()
                val res = String(response.data)
                val obj = JSONObject(res)

                val msg = obj.getString("message")

                if (msg == "true") {
                    try {
                        val jsonArray = obj.getJSONArray("posted_job")
                        for (i in 0 until jsonArray.length()) {
                            val job = jsonArray.getJSONObject(i)
                            val job_id = job.getString("job_id").toInt()
                            val job_description = job.getString("job_description")
                            val post_date = job.getString("post_date")
                            val job_specialty = job.getString("job_specialty")
                            jobs.add(
                                Posted_Job(
                                    job_id,
                                    job_description,
                                    post_date,
                                    job_specialty
                                )
                            )
                        }
                        val jobs_adapter =
                            Posted_Job_Adapter(
                                requireContext(),
                                jobs
                            )
                        list_view_job_posts.adapter = jobs_adapter

                    } catch (e: JSONException) {
                        Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    txtView_havent_posted.visibility = View.VISIBLE
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

