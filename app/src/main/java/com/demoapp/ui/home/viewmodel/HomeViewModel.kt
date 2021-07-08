package com.demoapp.ui.home.viewmodel


import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.demoapp.R
import com.demoapp.apputils.Utils
import com.demoapp.base.viewmodel.BaseViewModel
import com.demoapp.databinding.ActivityHomeBinding
import com.demoapp.interfaces.TopBarClickListener
import com.demoapp.ui.home.view.HomeActivity
import com.demoapp.ui.profile.view.ProfileActivity
import com.demoapp.ui.signup.view.SignupActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class HomeViewModel(application: Application) : BaseViewModel(application), OnMapReadyCallback {

    private lateinit var binder: ActivityHomeBinding
    private lateinit var mContext: Context
    lateinit var childFragmentManager: FragmentManager
    private var mMap: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    var marker: Marker? = null
    var gCurrent: LatLng? = null

    fun setBinder(binder: ActivityHomeBinding, childFragmentManager: FragmentManager) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        this.binder.topBar.topBarClickListener = SlideMenuClickListener()
        this.binder.topBar.isProfileShow = true
        this.binder.topBar.isTextShow = true
        this.childFragmentManager = childFragmentManager
        init()
    }

    private fun init() {
        val mapFragment = (mContext as HomeActivity).supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap;
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location != null) {
//                    mMap!!.clear()
                    gCurrent =
                        LatLng(location.latitude, location.longitude)
                    if (marker != null) {
                        marker!!.remove();
                    } else {
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(gCurrent, 17f))
                    }
                    marker = mMap!!.addMarker(
                        MarkerOptions().position(gCurrent).title("Current Location")
                    )
                }
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}
        }
        locationManager =
            (mContext as Activity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return
        }
        //Check permission
        getCurrentLocation()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            mContext as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    //Check request code
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {

            }
        }
    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2,
                2f,
                locationListener!!
            )
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                2,
                2f,
                locationListener as LocationListener
            )
            mMap!!.clear()
            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
            mMap!!.uiSettings.isCompassEnabled = false

            val locationButton = (binder.map.findViewById<View>("1".toInt())
                .parent as View).findViewById<View>("2".toInt())
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            rlp.setMargins(0, 20, 250, 0)


            val lastLocation =
                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (lastLocation != null) {
                val lastUserLocation =
                    LatLng(lastLocation.latitude, lastLocation.longitude)
                gCurrent = lastUserLocation
            }
            if (gCurrent != null) {
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(gCurrent, 17f))
                if (marker != null) {
                    marker!!.remove();
                }
                marker = mMap!!.addMarker(
                    MarkerOptions().position(gCurrent).title("Current Location")
                )
            }
        } else {
            requestPermission()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                getCurrentLocation()
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    (mContext as Activity).startActivityForResult(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        ), 2
                    )
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    (mContext as Activity).finish()
                })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    inner class ViewClickHandler {
        fun onCreateTask(view: View) {
            try {
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.profile))) {
                try {
                    val i = Intent(mContext, ProfileActivity::class.java)
                    mContext.startActivity(i)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        override fun onBackClicked(view: View?) {

        }
    }

}

