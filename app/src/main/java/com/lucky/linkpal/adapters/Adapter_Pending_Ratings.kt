package com.lucky.linkpal.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.lucky.linkpal.R
import com.lucky.linkpal.data_classes.Pending_Ratings
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import org.json.JSONException
import org.json.JSONObject

class Adapter_Pending_Ratings(
    private var context: Context,
    private var list: MutableList<Pending_Ratings>
) : BaseAdapter() {
    val sh: SharedPreferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    val user_id = sh.getInt("user_id", 0)

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_rate_jobs, parent, false)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val job_summary = mView?.findViewById<TextView>(R.id.job_summary)
        val username = mView?.findViewById<TextView>(R.id.done_by)

        val job_id = list[position].job_id
        val rate_me = mView?.findViewById<Button>(R.id.rate_me)

        if (job_title != null) {
            job_title.text = list[position].job_title
        }
        if (job_summary != null) {
            job_summary.text = list[position].job_description
        }
        if (username != null) {
            username.text = "Done by: " + list[position].firstname + " " + list[position].lastname
        }

        rate_me!!.setSafeOnClickListener {
            showRatingBar(job_id)
        }
        return mView
    }

    private fun showRatingBar(job_id: String) {
        val popDialog = AlertDialog.Builder(context)
        val rating = RatingBar(context)
        rating.max = 5

        val linearlayout = LinearLayout(context)

        val lp = linearlayout.layoutParams
        linearlayout.addView(rating)

        popDialog.setIcon(android.R.drawable.btn_star_big_on)
        popDialog.setTitle(R.string.rate_job)
        popDialog.setView(linearlayout)

        popDialog.setPositiveButton(android.R.string.ok) { dialog, _ ->
            if (rating.progress != 0) {
                rateJob(rating.progress, job_id)
            }
            dialog.dismiss()
        }

            .setNegativeButton("Ignore") { dialog, _ ->
                rateJob(0, job_id)
                dialog.dismiss()
            }
        popDialog.create()
        popDialog.show()
    }

    private fun rateJob(progress: Int, jobId: String) {
        val request = object : VolleyFileUploadRequest(
            Method.POST, URLs.rate_job,
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
                    info["rating"] = progress.toString()
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