package com.lucky.linkpal.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.adapters.Adapter_Pending_Ratings
import com.lucky.linkpal.data_classes.Pending_Ratings
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.fragment_pending_ratings.*
import org.json.JSONException
import org.json.JSONObject

class RateJobsFragment : Fragment() {
    private var user_id: Int? = null
    private lateinit var pending_ratings: MutableList<Pending_Ratings>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sh: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)

        showPendingRatings()

        return inflater.inflate(R.layout.fragment_pending_ratings, container, false)
    }

    private fun showPendingRatings() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.pending_ratings,
            Response.Listener { response ->

                pending_ratings = mutableListOf()
                val res = String(response.data)
                val obj = JSONObject(res)

                val msg = obj.getString("message")

                if (msg == "true") {
                    try {
                        val jsonArray = obj.getJSONArray("ratings")
                        for (i in 0 until jsonArray.length()) {
                            val rating = jsonArray.getJSONObject(i)
                            val job_id = rating.getString("job_id")
                            val job_title = rating.getString("job_title")
                            val job_description = rating.getString("job_description")
                            val firstname = rating.getString("firstname")
                            val lastname = rating.getString("lastname")
                            pending_ratings.add(
                                Pending_Ratings(
                                    job_id,
                                    job_title,
                                    job_description,
                                    firstname,
                                    lastname
                                )
                            )
                        }
                        val pending_ratings_adapter =
                            Adapter_Pending_Ratings(
                                requireContext(),
                                pending_ratings
                            )
                        list_view_pending_ratings.adapter = pending_ratings_adapter

                    } catch (e: JSONException) {
                        Toast.makeText(context, "Oops! An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    txtView_no_pending_ratings.visibility = View.VISIBLE
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
                    user["employer_id"] = user_id.toString()
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