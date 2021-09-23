package com.lucky.linkpal.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.lucky.linkpal.R
import com.lucky.linkpal.adapters.Adapter_Job_Requests
import com.lucky.linkpal.data_classes.Job_Request
import com.lucky.linkpal.utils.GLOBALS
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.fragment_job_requests.*
import org.json.JSONException
import org.json.JSONObject

class JobRequestsFragment : Fragment() {
    private var employer_id: Int? = null
    private var longitude: String? = null
    private var latitude: String? = null
    private lateinit var employer_phone: String
    private lateinit var job_requests: MutableList<Job_Request>
    private lateinit var job_requests_adapter: Adapter_Job_Requests
    private var sort_criteria: Int = 1

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
        employer_phone = sh.getString("phone_number", null).toString()

        showJobRequests()

        setHasOptionsMenu(true)

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
                                    rating
                                )
                            )
                        }
                        job_requests_adapter =
                            Adapter_Job_Requests(
                                requireContext(),
                                job_requests
                            )
                        list_view_job_requests.adapter = job_requests_adapter

                        job_requests_adapter.setOnSmsListener(object :
                            Adapter_Job_Requests.OnSmsSendListener {
                            override fun OnSms(
                                firstname: String,
                                lastname: String,
                                job_type: String,
                                worker_phone: String
                            ) {
                                onSmsFunc(firstname, lastname, job_type, worker_phone)
                            }
                        })

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
                    user["sort_method"] = sort_criteria.toString()
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

    private fun onSmsFunc(
        firstname: String,
        lastname: String,
        jobType: String,
        workerPhone: String
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestSmsPermission()
        } else {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                workerPhone,
                null,
                "$firstname $lastname recruited you to work on the $jobType job for which you applied. Kindly reach out through $employer_phone",
                null,
                null
            )
            GLOBALS.recruitSmsChecker = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.sort_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        sort_criteria = when (item.itemId) {
            R.id.option_rating -> {
                1
            }
            R.id.option_distance -> {
                2
            }
            R.id.option_price -> {
                3
            }
            else -> {
                1
            }
        }
        showJobRequests()
        return super.onOptionsItemSelected(item)
    }

    private fun requestSmsPermission() {
        when {
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                android.Manifest.permission.SEND_SMS
            ) -> {
                var sb = Snackbar.make(
                    layout_job_requests,
                    "You need to give sms permission for good performance",
                    Snackbar.LENGTH_INDEFINITE
                )
                sb.setAction("Ok") { }
                sb.show()
                requestSmsPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
            }
            else -> {
                requestSmsPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
            }
        }
    }

    private var requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    context,
                    "Permission granted. You can now resend the message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requestSmsPermission()
            }
        }

}