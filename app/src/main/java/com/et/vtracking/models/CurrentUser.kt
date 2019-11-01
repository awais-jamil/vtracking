package com.engintech.vtracking.models


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.et.vtracking.models.User


object CurrentUser {

    lateinit var firebaseUser: FirebaseUser
    lateinit var user: User

    init {

        FirebaseAuth.getInstance().currentUser?.let {

            firebaseUser = it
        }?: run {

            throw Exception("No Authenticated User Found.")
        }

        user = User()
        reloadUser()
    }

    fun reloadUser() {

        FirebaseAuth.getInstance().currentUser?.reload()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
    }

    fun fetchUserData(callback: (error: Exception?, user:User? ) -> Unit){

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("user").document(firebaseUser!!.uid).get()
            .addOnSuccessListener {

                if(it.data == null){
                    callback(null, null)
                }else{
                    user = it!!.toObject(User::class.java)!!
                    callback(null, user)
                }

            }
            .addOnFailureListener { e ->
                callback(e, null)
            }

    }

    fun updateLocation(lat: Double, long: Double, callback: (error: Exception?) -> Unit){

        var data = hashMapOf<String, Any>(

            "latitude" to lat.toDouble(),
            "longitude" to long.toDouble()
        )
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("user").document(firebaseUser.uid).update(data)
            .addOnSuccessListener {
                callback(null)
            }
            .addOnFailureListener { e ->
                callback(null)
            }

    }

    fun fetchTargetUser(target:String, callback: (error: Exception?, user: User? ) -> Unit){
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("user").get()
            .addOnSuccessListener {

                val list = ArrayList<User>()
                if (!it.isEmpty()) {
                    for (snapshot in it) {
                        list.add(snapshot.toObject(User::class.java!!))
                    }
                }

                if (list.size > 0) {
                    for (user in list) {
                        if (user.trackingID.contains(target)) {
                            callback(null, user)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                callback(e, null)
            }
    }

}
