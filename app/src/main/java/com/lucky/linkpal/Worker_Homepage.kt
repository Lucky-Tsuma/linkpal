package com.lucky.linkpal

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.lucky.linkpal.fragments.*
import com.lucky.linkpal.utils.URLs
import com.lucky.linkpal.utils.VolleyFileUploadRequest
import kotlinx.android.synthetic.main.activity_worker__homepage.*
import kotlinx.android.synthetic.main.nav_drawer_header_worker.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class Worker_Homepage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var header: View
    private var user_id: Int? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private lateinit var geocoder: Geocoder
    private lateinit var Adresses: List<Address>
    private var currentLocation: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    /*Updating location in the case that the device cannot access it. For whatever reason*/
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                longitude = location.longitude.toString()
                latitude = location.latitude.toString()
            }
        }
    }

    /*After the user has been prompted to switch location on, pick the response and work with whichever they chose*/
    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                pickLocation()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Your location will be set to Unknown.",
                    Toast.LENGTH_SHORT
                ).show()
                currentLocation = "Unknown Location"
                header.nav_location.text = currentLocation
            }
        }

    private val resolutionForResult2 =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                noPickLocation()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Your location will be set to Unknown.",
                    Toast.LENGTH_SHORT
                ).show()
                currentLocation = "Unknown Location"
                header.nav_location.text = currentLocation
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker__homepage)

        val sh: SharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        user_id = sh.getInt("user_id", 0)
        val firstname = sh.getString("firstname", null)
        val lastname = sh.getString("lastname", null)
        val phone_number = sh.getString("phone_number", null)
        val profile_pic = sh.getString("profile_pic", null)
        val rating = sh.getFloat("rating", 0F)
        latitude = sh.getString("latitude", null)
        longitude = sh.getString("longitude", null)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
        header = nav_view_worker.getHeaderView(0)
        header.nav_username.text = getString(R.string.username, "$firstname", "$lastname")
        header.nav_phone_number.text = phone_number
        header.user_rating.rating = rating.toFloat()
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

        /*Creating an alert dialog so a user can choose to update location*/
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message)

        builder.setPositiveButton("Yes") { _, _ ->
            pickLocation()
        }

        builder.setNegativeButton("No") { _, _ ->
            noPickLocation()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
//        alertDialog.show()

        /*Am picking the location automatically, no user dialog*/
        pickLocation()
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

    private fun showLocation() {
        geocoder = Geocoder(this, Locale.getDefault())
        if (latitude != null && longitude != null) {
            try {
                Adresses =
                    geocoder.getFromLocation(latitude!!.toDouble(), longitude!!.toDouble(), 1)
                if (Adresses.isNotEmpty()) {
                    header.nav_location.text = Adresses[0].adminArea
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            header.nav_location.text = "Unknown Location"
        }
    }

    /*launches dialog to request app location permission*/
    private var requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickLocation()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Location will be set to the last known location.",
                    Toast.LENGTH_SHORT
                ).show()
                showLocation()
            }
        }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                val snackBar = Snackbar.make(
                    drawer_layout_worker,
                    "You need to enable location to get suggestions on nearby users",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction("Ok") { snackBar.dismiss() }
                snackBar.show()
                requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun pickLocation() {
        /*GETTING CURRENT LOCATION SETTINGS*/
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        //All location settings are satisfied. The client can make location requests
        task.addOnSuccessListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission()
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            longitude = location.longitude.toString()
                            latitude = location.latitude.toString()
                        } else {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                            )
                        }
                    }
                updateLocation()
                showLocation()
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun noPickLocation() {
        /*GETTING CURRENT LOCATION SETTINGS*/
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        //All location settings are satisfied. The client can make location requests
        task.addOnSuccessListener {
            showLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult2.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun updateLocation() {
        val request = object : VolleyFileUploadRequest(Method.POST, URLs.update_location,
            Response.Listener { response ->
                val res = String(response.data)

                try {
                    val obj = JSONObject(res)
                    val msg: String = obj.getString("message")
                    val error = obj.getBoolean("error")
                    if (!error) {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Oops! An error occurred", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                if (error.toString().matches(Regex("(.*)NoConnectionError(.*)"))) {
                    Toast.makeText(
                        applicationContext,
                        "Check your internet connection. Or try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val data = HashMap<String, String>()
                try {
                    data["user_id"] = user_id.toString()
                    data["longitude"] = longitude.toString()
                    data["latitude"] = latitude.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return data
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

}