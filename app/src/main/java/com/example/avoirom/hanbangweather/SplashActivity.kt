package com.example.avoirom.hanbangweather

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE =1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check permission

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else {
            buildLocationRequest()
            buildLocationCallBack()

            // Create FusedProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            // Set event
            btn_start_updates.setOnClickListener(View.OnClickListener {
                if (ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                    return@OnClickListener
                }

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                // Change state of button
                btn_start_updates.isEnabled = !btn_start_updates.isEnabled
                btn_stop_updates.isEnabled = !btn_stop_updates.isEnabled
            })

            btn_stop_updates.setOnClickListener(View.OnClickListener {
                if (ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                    return@OnClickListener
                }

                fusedLocationProviderClient.removeLocationUpdates(locationCallback)

                // Change state of button
                btn_start_updates.isEnabled = !btn_start_updates.isEnabled
                btn_stop_updates.isEnabled = !btn_stop_updates.isEnabled
            })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this@SplashActivity, "Permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SplashActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallBack() {
        locationCallback = object: LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                val location = p0!!.locations.get(p0!!.locations.size-1) // Get last location
                txt_location.text = location.latitude.toString() + " / " + location.longitude.toString()

                val intent = Intent(baseContext, MainActivity::class.java)
                intent.putExtra("latitude", location.latitude.toInt().toString())
                intent.putExtra("longitude", location.longitude.toInt().toString())
                startActivity(intent)
                finish()
            }
        }
    }
}