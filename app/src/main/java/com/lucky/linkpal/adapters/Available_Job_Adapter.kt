package com.lucky.linkpal.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.data_classes.Available_Job
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.nav_drawer_header_employer.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Available_Job_Adapter(
    private var context: Context,
    private var list: MutableList<Available_Job>
) : BaseAdapter() {
    private val sh: SharedPreferences =
        context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    val user = sh.getInt("user_id", 0)
    val firstname = sh.getString("firstname", null)
    val lastname = sh.getString("lastname", null)
    private lateinit var Addresses: List<Address>
    private lateinit var proposal: String
    private lateinit var price: String

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_available_jobs, parent, false)

        val job_id = mView?.findViewById<TextView>(R.id.job_id)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val employer_phone = mView?.findViewById<TextView>(R.id.employer_phone)
        val job_summary = mView?.findViewById<TextView>(R.id.job_summary)
        val posted_by = mView?.findViewById<TextView>(R.id.posted_by)
        val post_date = mView?.findViewById<TextView>(R.id.post_date)
        val apply_job = mView?.findViewById<Button>(R.id.button_apply_job)
        val location = mView?.findViewById<TextView>(R.id.job_location)

        val longitude = list[position].longitude
        val latitude = list[position].latitude

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            Addresses =
                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (Addresses.isNotEmpty()) {
                if (location != null) {
                    location.text = Addresses[0].adminArea
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (job_id != null) {
            job_id.text = list[position].job_id
        }
        if (job_title != null) {
            job_title.text = list[position].job_specialty
        }
        if (job_summary != null) {
            job_summary.text = list[position].job_description
        }
        if (posted_by != null) {
            posted_by.text = list[position].firstname + " " + list[position].lastname
        }
        if (post_date != null) {
            post_date.text = list[position].post_date
        }
        if (employer_phone != null) {
            employer_phone.text = list[position].employer_phone
        }

        fun quotePrice(){
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.job_dialog_title)

            val inputPrice = EditText(context)
            inputPrice.setHint(R.string.job_price)
            inputPrice.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(inputPrice)

            builder.setPositiveButton("OK") { _, _ ->
                price = inputPrice.text.toString()

                if (price.isNotEmpty()) {
                    applyJob(list[position].job_id)
                } else {
                    Toast.makeText(context, "Input cannot be less than 20 characters", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

        fun makeProposal(){
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.job_dialog_title)

            val inputProposal = EditText(context)
            inputProposal.setHint(R.string.job_proposal)
            inputProposal.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(inputProposal)

            builder.setPositiveButton("OK") { _, _ ->
                proposal = inputProposal.text.toString()

                if (proposal.isNotEmpty() && proposal.length > 20) {
                    quotePrice()
                } else {
                    Toast.makeText(context, "Input cannot be less than 20 characters", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

        apply_job!!.setSafeOnClickListener {
            makeProposal()
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

    private fun applyJob(jobId: String) {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.apply_job,
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
                    Log.d("LTM_DEBUGR", res)
                    Log.d("LTM_DEBUG", e.toString())
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
                val application = HashMap<String, String>()
                try {
                    application["user_id"] = user.toString()
                    application["job_id"] = jobId
                    application["price"] = price
                    application["proposal"] = proposal
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return application
            }
        }
        Volley.newRequestQueue(context).add(request)
    }
}