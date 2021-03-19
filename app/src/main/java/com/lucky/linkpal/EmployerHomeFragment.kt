package com.lucky.linkpal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lucky.linkpal.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_employer_home.view.*

class EmployerHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_employer_home, container, false)
        fragment.btn_post_a_job.setSafeOnClickListener {
            val intent = Intent(context, Post_A_Job::class.java)
            startActivity(intent)
        }
        return fragment
    }

}

