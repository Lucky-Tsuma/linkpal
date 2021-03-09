package com.lucky.linkpal

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_worker__homepage.*
import kotlinx.android.synthetic.main.nav_drawer_header_worker.view.*

class Worker_Homepage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String
    private lateinit var profile_pic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__homepage)

        val intent = intent
        email = intent.getStringExtra("email").toString()
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()

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
        header.nav_username.text = "${firstname} ${lastname}"
        header.useremail.text = email

        /*So the activity opens to the home fragment by default. And the home fragment will be restored only in case of a first configuration change*/
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_worker, WorkerHomeFragment()).commit()
            nav_view_worker.setCheckedItem(R.id.worker_home)
            toolbar_worker.title = "Available Jobs"
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.worker_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_worker, WorkerHomeFragment())
                    .commit()
                toolbar_worker.title = "Available Jobs"
            }
            R.id.worker_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_worker, WorkerProfileFragment()).commit()
                toolbar_worker.title = "Profile"
            }
            R.id.portfolio -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_worker, PortfolioFragment()).commit()
                toolbar_worker.title = "Portfolio"
            }
            R.id.pending_requests -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_worker, PendingRequestsFragment()).commit()
                toolbar_worker.title = "Pending Requests"
            }
            R.id.jobs_invited -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_worker, JobsInvitedFragment()).commit()
                toolbar_worker.title = "Jobs Invited"
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