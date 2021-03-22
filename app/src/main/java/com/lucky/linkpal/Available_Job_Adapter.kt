package com.lucky.linkpal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class Available_Job_Adapter(
    private var context: Context,
    private var list: MutableList<Available_Job>
) : BaseAdapter() {
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_available_jobs, parent, false)

        val job_id = mView!!.findViewById<TextView>(R.id.job_id)
        val job_title = mView.findViewById<TextView>(R.id.job_title)
        val job_location = mView.findViewById<TextView>(R.id.job_location)
        val job_summary = mView.findViewById<TextView>(R.id.job_summary)
        val amount = mView.findViewById<TextView>(R.id.amount)
        val posted_by = mView.findViewById<TextView>(R.id.posted_by)
        val post_date = mView.findViewById<TextView>(R.id.post_date)

        job_id.text = list[position].job_id.toString()
        job_title.text = list[position].job_specialty
        job_location.text = list[position].job_location
        job_summary.text = list[position].job_description
        amount.text = list[position].amount + "/="
        posted_by.text = list[position].firstname + " " + list[position].lastname
        post_date.text = list[position].post_date

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
}