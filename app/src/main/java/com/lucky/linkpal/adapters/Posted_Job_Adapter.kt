package com.lucky.linkpal.adapters

import android.content.Context
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
import com.lucky.linkpal.data_classes.Posted_Job
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import org.json.JSONException
import org.json.JSONObject

class Posted_Job_Adapter(private var context: Context, private var list: MutableList<Posted_Job>) :
    BaseAdapter() {

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_job_posts, parent, false)
        val job_id = mView!!.findViewById<TextView>(R.id.job_id)
        val job_title = mView.findViewById<TextView>(R.id.job_title)
        val job_summary = mView.findViewById<TextView>(R.id.job_summary)
        val post_date = mView.findViewById<TextView>(R.id.post_date)
        val delete_post = mView.findViewById<Button>(R.id.button_delete_post)

        job_id.text = list[position].job_id.toString()
        job_title.text = list[position].job_specialty
        job_summary.text = list[position].job_description
        post_date.text = list[position].post_date


        delete_post!!.setSafeOnClickListener {
            deleteJob(list[position].job_id.toString())
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

    private fun deleteJob(job_id: String) {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.delete_job,
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
                val job = HashMap<String, String>()
                try {
                    job["job_id"] = job_id
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return job
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