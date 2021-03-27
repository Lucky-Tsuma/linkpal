package com.lucky.linkpal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lucky.linkpal.R
import com.lucky.linkpal.data_classes.Pending_Request

class Adapter_Pending_Requests(
    private var context: Context,
    private var list: MutableList<Pending_Request>
) : BaseAdapter() {
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val mView: View? =
            LayoutInflater.from(context).inflate(R.layout.list_pending_requests, parent, false)

        val job_id = mView?.findViewById<TextView>(R.id.job_id)
        val job_title = mView?.findViewById<TextView>(R.id.job_title)
        val job_location = mView?.findViewById<TextView>(R.id.job_location)
        val amount = mView?.findViewById<TextView>(R.id.amount)
        val request_date = mView?.findViewById<TextView>(R.id.request_date)

        if (job_id != null) {
            job_id.text = list[position].job_id
        }
        if (amount != null) {
            amount.text = list[position].amount + "/="
        }
        if (job_title != null) {
            job_title.text = list[position].job_title
        }
        if (job_location != null) {
            job_location.text = list[position].job_location
        }
        if (request_date != null) {
            request_date.text = list[position].request_date
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
}