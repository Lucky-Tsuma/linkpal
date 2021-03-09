package com.lucky.linkpal

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_employer__homepage.*
import kotlinx.android.synthetic.main.nav_drawer_header_employer.view.*

class Employer_Homepage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer__homepage)

        val intent = intent
        email = intent.getStringExtra("email").toString()
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()

        setSupportActionBar(toolbar_employer)/*We got rid of the default action bar, noew we setting a toolbar instead*/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_employer, toolbar_employer,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_employer.addDrawerListener(toggle)
        toggle.syncState()

        nav_view_employer.setNavigationItemSelectedListener(this)

        /*setting user information on nav header*/
        val header = nav_view_employer.getHeaderView(0)
        header.nav_username.text = "${firstname} ${lastname}"
        header.useremail.text = email

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_employer, EmployerHomeFragment()).commit()
            nav_view_employer.setCheckedItem(R.id.employer_home)
            toolbar_employer.title = "Your Posts"
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.employer_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_employer, EmployerHomeFragment())
                    .commit()
                toolbar_employer.title = "Your Posts"
            }

            R.id.employer_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_employer, EmployerProfileFragment())
                    .commit()
                toolbar_employer.title = "Profile"
            }

            R.id.upgrade_account -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_employer, UpgradeAccountFragment())
                    .commit()
                toolbar_employer.title = "Upgrade Account"
            }

            R.id.job_requests -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_employer, JobRequestsFragment())
                    .commit()
                toolbar_employer.title = "Job Requests"
            }

            R.id.job_invites -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_employer, JobInvitesFragment())
                    .commit()
                toolbar_employer.title = "Job Invites"
            }
        }

        drawer_layout_employer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout_employer.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_employer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}