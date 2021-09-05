package com.lucky.linkpal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.lucky.linkpal.fragments.*
import com.lucky.linkpal.utils.URLs
import kotlinx.android.synthetic.main.activity_worker__homepage.*
import kotlinx.android.synthetic.main.nav_drawer_header_worker.view.*

class Worker_Homepage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__homepage)

        val sh: SharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
//        val user_id = sh.getInt("user_id", 0)
        val firstname = sh.getString("firstname", null)
        val lastname = sh.getString("lastname", null)
        val phone_number = sh.getString("phone_number", null)
        val profile_pic = sh.getString("profile_pic", null)

        toolbar_worker.title = "Available Jobs"
        setSupportActionBar(toolbar_worker)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout_worker,
            toolbar_worker,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout_worker.addDrawerListener(toggle)
        toggle.syncState()

        /*To listen to click events on the navigation view, we first have to reference to it. As below*/
        nav_view_worker.setNavigationItemSelectedListener(this)

        /*setting user information on nav header*/
        val header = nav_view_worker.getHeaderView(0)
        header.nav_username.text = getString(R.string.username, "$firstname", "$lastname")
        header.nav_phone_number.text = phone_number
        Glide.with(this).load(URLs.root_url + profile_pic).into(header.user_profile_pic)

        /*So the activity opens to the home fragment by default. And the home fragment will be restored only in case of a first configuration change*/
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container_worker,
                    WorkerHomeFragment()
                ).commit()
            nav_view_worker.setCheckedItem(R.id.worker_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.worker_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_worker,
                        WorkerHomeFragment()
                    )
                    .commit()
                toolbar_worker.title = "Available Jobs"
            }
            R.id.worker_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_worker,
                        WorkerProfileFragment()
                    ).commit()
                toolbar_worker.title = "Profile"
            }
            R.id.portfolio -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_worker,
                        PortfolioFragment()
                    ).commit()
                toolbar_worker.title = "Portfolio"
            }
            R.id.pending_requests -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_worker,
                        PendingRequestsFragment()
                    ).commit()
                toolbar_worker.title = "Pending Requests"
            }
            R.id.jobs_invited -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_worker,
                        JobsInvitedFragment()
                    ).commit()
                toolbar_worker.title = "Jobs Invited"
            }
            R.id.log_out -> {
                val sh: SharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sh.edit()
                editor.clear()
                editor.apply()

                Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        drawer_layout_worker.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout_worker.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_worker.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}