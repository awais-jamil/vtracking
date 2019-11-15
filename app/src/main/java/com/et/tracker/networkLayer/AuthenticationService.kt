package com.et.tracker.networkLayer

import com.et.tracker.support.SharedPreferences
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object AuthenticationService {

    val auth: FirebaseAuth? = FirebaseAuth.getInstance()

    var currentUser: FirebaseUser? = null
        get() = FirebaseAuth.getInstance().currentUser
        private set

    fun needsAuthentication(): Boolean {
        return currentUser == null
    }


    fun currentPhoneNumber(): String? {

        var phoneNumber:String? = null

        currentUser?.let {

            for (providerData in it.providerData) {
                if (providerData.providerId.equals("phone"))
                    phoneNumber = providerData.phoneNumber
            }
        }

        return phoneNumber
    }


    fun verify(phoneNumber: String,
               callback:(error: Exception?)-> Unit) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    //signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {

                    callback(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {

                    SharedPreferences.setAuthVerificationId(verificationId)

                    SharedPreferences.setAuthResendToken(token.toString())

                    callback(null)
                }

            })
    }

    fun verify(
        verificationCode: String,
        callback: (user: FirebaseUser?, error: java.lang.Exception?) -> Unit
    ) {

        SharedPreferences.getAuthVerificationId()?.let {

            val credential = PhoneAuthProvider.getCredential(
                it,
                verificationCode
            )

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = task.result?.user

                        callback(user, null)

                    } else {
                        // Sign in failed, display a message and update the UI
                        callback(null, task.exception)
                    }
                }
        }
    }


    fun createUser(email: String, password: String,
               callback:(user: FirebaseUser?, error: Exception?)-> Unit) {

        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    callback(user, null)
                } else {
                    // If sign in fails, display a message to the user.
                    callback(null, task.exception)
                }
            }
    }

    fun signinUser(email: String, password: String,
                   callback:(user: FirebaseUser?, error: Exception?)-> Unit) {

        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    callback(user, null)
                } else {
                    // If sign in fails, display a message to the user.
                    callback(null, task.exception)
                }
            }
    }

    fun logoutUser(){

        auth!!.signOut()
    }

}