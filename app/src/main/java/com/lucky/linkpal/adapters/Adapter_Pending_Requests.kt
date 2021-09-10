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
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.data_classes.Pending_Request
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Adapter_Pending_Requests(
    private var context: Context,
    private var list: MutableList<Pending_Request>
) : BaseAdapter() {
    val sh: SharedPreferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    val user_id = sh.getInt("user_id", 0)
    private lateinit var Addresses: List<Address>

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_pending_requests, parent, false)

        val job_id = mView?.findViewById<TextView>(R.id.job_id)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val job_location = mView?.findViewById<TextView>(R.id.job_location)
        val employer = mView?.findViewById<TextView>(R.id.employer)
        val amount = mView?.findViewById<TextView>(R.id.amount)
        val request_date = mView?.findViewById<TextView>(R.id.request_date)
        val delete_request = mView?.findViewById<Button>(R.id.button_delete_request)

        val longitude = list[position].longitude
        val latitude = list[position].latitude

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            Addresses =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (Addresses.isNotEmpty()) {
                if (job_location != null) {
                    job_location.text = Addresses[0].adminArea
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (job_id != null) {
            job_id.text = list[position].job_id
        }
        if (amount != null) {
            amount.text = "My price: "+list[position].bidding_amount + "/="
        }
        if (job_title != null) {
            job_title.text = list[position].job_title
        }
        if (request_date != null) {
            request_date.text = list[position].request_date
        }
        if(employer != null) {
            employer.text = "Posted by: " + list[position].firstname + " " + list[position].lastname
        }

        delete_request!!.setSafeOnClickListener {
            deleteRequest(list[position].job_id)
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

    private fun deleteRequest(job_id: String) {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.delete_job_request,
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
                    info["user_id"] = user_id.toString()
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
}