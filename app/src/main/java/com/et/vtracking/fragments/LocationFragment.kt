package com.et.vtracking.fragments

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.et.vtracking.R
import com.et.vtracking.baseControls.BaseFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.engintech.vtracking.models.CurrentUser
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_location.view.*
import com.google.android.gms.location.*
import java.text.DateFormat
import java.util.*
import android.widget.Toast
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import android.os.Handler
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.location.LocationSettingsResponse
import androidx.fragment.app.activityViewModels
import com.engintech.vtracking.activities.DashBoardActivity
import com.engintech.vtracking.viewmodels.DashboardViewModel
import com.google.android.gms.tasks.OnSuccessListener


/**
 * A simple [Fragment] subclass.
 */
class LocationFragment : BaseFragment(), OnMapReadyCallback{

    val dashboardViewModel by activityViewModels<DashboardViewModel>()

    lateinit var map: SupportMapFragment
    lateinit var googleMap: GoogleMap

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mSettingsClient: SettingsClient
    lateinit var mLocationRequest: LocationRequest
    lateinit var mLocationSettingsRequest: LocationSettingsRequest
    lateinit var mLocationCallback: LocationCallback
    lateinit var mCurrentLocation: Location

    // location last updated time
    private var mLastUpdateTime: String? = null

    // location updates interval - 10sec
    private var UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private var FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

    private var REQUEST_CHECK_SETTINGS = 100

    private var mRequestingLocationUpdates: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        map = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        map.getMapAsync(this)//remember getMap() is deprecated!

        dashboardViewModel.userLocationUpdated.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if(it){
                    Handler().postDelayed({

                        view!!.last_seen_number.text = "LAST SEEN: "+dashboardViewModel.trackingUser.lastSeen.toUpperCase()

                        googleMap.clear()
                        googleMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(dashboardViewModel.latitude.value!!, dashboardViewModel.longitude.value!!), 16f
                            )
                        )
                        googleMap!!.addMarker(
                            MarkerOptions()
                                .anchor(0.0f, 1.0f)
                                .position(LatLng(dashboardViewModel.latitude.value!!, dashboardViewModel.longitude.value!!))
                        )

                    }, 3000)
                }
            }
        )

        view.menu_butn.setOnClickListener {
            (activity as DashBoardActivity).openDrawer()
        }

        view.share_id.setOnClickListener {
            showShareDialog()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        setActionBarTitle("")

        initLogic()
        loadUserData()
    }

    fun initLogic(){

        if(CurrentUser.user.userType.equals("Driver")) {
            initLocation()
            view!!.share_id.visibility = View.VISIBLE
        } else {
            startListeningToLocation()
            view!!.share_id.visibility = View.GONE
        }
    }

    fun loadUserData(){

        if(CurrentUser.user.userType.equals("Driver")) {
            view!!.user_info_view.visibility = View.GONE
            view!!.gif_view.visibility = View.VISIBLE
        } else {
            view!!.user_info_view.visibility = View.VISIBLE
            view!!.gif_view.visibility = View.GONE
            view!!.username.text = dashboardViewModel.trackingUser.fullName.toUpperCase()
            view!!.vehicle_number.text = "CAR NUMBER: "+dashboardViewModel.trackingUser.trackingID.toUpperCase()
            view!!.last_seen_number.text = "LAST SEEN: "+CurrentUser.user.lastSeen.toUpperCase()
        }
    }

    fun showShareDialog(){

        val dialogBuilder = AlertDialog.Builder(context)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.share_dialog, null)
        dialogBuilder.setView(dialogView)

        val uidText = dialogView.findViewById(R.id.uid_text) as TextView

        uidText.text = dashboardViewModel.currentUser.trackingID

        val shareButton = dialogView.findViewById(R.id.ok_button) as Button

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        shareButton.setOnClickListener {

            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage ="TrackingId: " + CurrentUser.user.trackingID + "\n Download VTracking app from PlayStore and start Tracking this user."

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))

                alertDialog.dismiss()
            } catch (e: Exception) {
                //e.toString();
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(CurrentUser.user.userType.equals("Driver")) {
            mFusedLocationClient.removeLocationUpdates(
                mLocationCallback
            )

        } else {
            dashboardViewModel.removeLocationListner()
        }
    }

    fun startListeningToLocation(){

        dashboardViewModel.startTrackingLocation()
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map!!
        if(CurrentUser.user.userType.equals("Driver")) {
            googleMap!!.setMyLocationEnabled(true)
        }
    }

    fun initLocation() {
       mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        mSettingsClient = LocationServices.getSettingsClient(context!!)

        mLocationCallback = object : LocationCallback() {
           override fun onLocationResult(locationResult :LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                mCurrentLocation = locationResult.getLastLocation()
                mLastUpdateTime = DateFormat.getTimeInstance().format( Date())

                updateLocationUI()
            }
        }

        mRequestingLocationUpdates = false

        mLocationRequest =  LocationRequest()
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        var builder =  LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()

        startLocationUpdates()
    }

     private fun updateLocationUI() {
        if (mCurrentLocation != null) {
            googleMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude), 16f
                )
            )
            googleMap!!.addMarker(
                MarkerOptions()
                    .anchor(0.0f, 1.0f)
                    .position(LatLng(mCurrentLocation!!.longitude, mCurrentLocation!!.longitude))
            )

            dashboardViewModel.shareLocation(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, callback = { error ->

            })

        }
     }

    private fun startLocationUpdates() {
        mSettingsClient
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(activity!!, OnSuccessListener<LocationSettingsResponse> {

                Toast.makeText(
                   context!!,
                    "Started location updates!",
                    Toast.LENGTH_SHORT
                ).show()


                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )
            })
            .addOnFailureListener(activity!!, OnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                        } catch (sie: IntentSender.SendIntentException) {
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."

                        Toast.makeText(activity!!, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

            })
    }

}
