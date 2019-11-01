package com.engintech.vtracking.viewmodels

import android.content.ContentValues.TAG
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.engintech.vtracking.models.CurrentUser
import com.et.vtracking.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.google.firebase.firestore.ListenerRegistration


class DashboardViewModel: ViewModel() {

    var firebaseUser: FirebaseUser? = null
        get() = FirebaseAuth.getInstance().currentUser
        private set

    var lastException:Exception? = null

    val userDataLoaded = MutableLiveData<Boolean> ()
    val userLocationUpdated = MutableLiveData<Boolean> ()
    val latitude = MutableLiveData<Double> ()
    val longitude = MutableLiveData<Double> ()

    lateinit var currentUser: User
    lateinit var trackingUser: User

    var trackingID: String? = ""

    private var listener: ListenerRegistration? = null

    init {
        currentUser = User()
        fetchCurrentUserData()
    }

    fun fetchCurrentUserData() {

        CurrentUser.fetchUserData(callback = { error, response ->

            if(error==null){
                currentUser = response!!
                userDataLoaded.value = true
            } else {
                userDataLoaded.value = false
            }
        })
    }

    fun shareLocation(lat: Double, long: Double, callback: (error: Exception?) -> Unit){

        CurrentUser.updateLocation(lat, long, callback = { error ->

            if(error==null){
                callback(null)
            } else {
                callback(error)
            }
        })
    }

    fun getTargetUserToTrack(callback: (error: Exception?) -> Unit){
        CurrentUser.fetchTargetUser(trackingID!!, callback = { error, user ->

            if (error == null) {
                trackingUser = user!!

                callback(null)
            }
        })
    }

    fun startTrackingLocation(){

//        lateinit var targetUserId : String
//        CurrentUser.fetchAllUsers(trackingID!!, callback = { error, uid ->
//
//            if (error == null) {
//
//                targetUserId = uid!!

                val firestore = FirebaseFirestore.getInstance()
                val query = firestore.collection("user").document(trackingUser.uid)

                listener = query.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: ${snapshot.data}")
                            var user = snapshot.toObject(User::class.java!!)

                            trackingUser = user!!

                            userLocationUpdated.value = true
                            latitude.value = (user!!.latitude)
                            longitude.value = (user!!.longitude)

                        } else {
                            Log.d(TAG, "Current data: null")
                        }
                    }
//            }
//
//        })

    }

    fun removeLocationListner(){

        listener!!.remove()
    }

}