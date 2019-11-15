package com.et.tracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.et.tracker.models.CurrentUser
import com.et.tracker.networkLayer.AuthenticationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class OnBoardingViewModel: ViewModel() {

    enum class OnBoardingState {
        SIGNUP_DONE,
        ERROR_SIGNUP,

        SIGNIN,
        SIGNIN_DONE,
        ERROR_SIGNIN,

        VERIFY_PHONE_NUMBER,
        ERROR_VERIFY_PHONE_NUMBER,

        VERIFY_PIN_CODE,
        ERROR_VERIFY_PIN_CODE,

        RESEND_PIN_CODE,
        ERROR_RESEND_PIN_CODE,

        UPDATE_PROFILE,
        ERROR_UPDATE_PROFILE,

        ONBOARDING_DONE

    }

    val onBoardingState = MutableLiveData<OnBoardingState>(OnBoardingState.VERIFY_PHONE_NUMBER)

    var currentUser: FirebaseUser? = null
        get() = FirebaseAuth.getInstance().currentUser
        private set

    var lastException:Exception? = null

    var verifiedPhoneNumber: String? = null


    fun verifyPhoneNumber(phoneNumber: String) {

        AuthenticationService.verify(phoneNumber)
        { error ->

            if(error == null) {

                verifiedPhoneNumber = phoneNumber

                onBoardingState.value = OnBoardingState.VERIFY_PIN_CODE
            }
            else {

                lastException = error

                onBoardingState.value = OnBoardingState.ERROR_VERIFY_PHONE_NUMBER
            }
        }
    }

    fun verifyPinCode(pinCode: String) {

        AuthenticationService.verify(pinCode) { user, error ->

            if(error == null) {

                user?.let {
                    currentUser = it

                    CurrentUser.reloadUser()

                    CurrentUser.fetchUserData(callback = { error, response ->

                        if(error==null){

                            if(response == null){
                                onBoardingState.value = OnBoardingState.UPDATE_PROFILE
                            } else {
                                onBoardingState.value = OnBoardingState.ONBOARDING_DONE
                            }

                        } else {
                            onBoardingState.value = OnBoardingState.ONBOARDING_DONE
                        }
                    })
                }
            }

            else {

                lastException = error

                onBoardingState.value = OnBoardingState.ERROR_VERIFY_PIN_CODE
            }
        }
    }

    fun resendPinCode() {

        //Lets verify phone number again to resend the code

        verifiedPhoneNumber?.let {

            val phoneNumber = it

            AuthenticationService.verify(phoneNumber)
            { error ->

                if(error == null) {

                    verifiedPhoneNumber = phoneNumber

                    onBoardingState.value = OnBoardingState.RESEND_PIN_CODE
                }
                else {

                    lastException = error

                    onBoardingState.value = OnBoardingState.ERROR_RESEND_PIN_CODE
                }
            }
        }
    }


    fun signUp(username: String, email: String, phone: String, password: String, isObserver: Boolean){

        AuthenticationService.createUser(email, password){ user, error ->

            if(error == null) {

                user?.let {
                    currentUser = it

                }
            }

            else {

                lastException = error

                onBoardingState.value = OnBoardingState.ERROR_SIGNUP
            }

        }

    }

    fun saveUserData(username: String, vehicleNum: String, vehicleModel: String, userType: String) {

        var trackId = ""
        var vehicleList = vehicleNum.split(' ')
        var vehicleModelList = vehicleModel.split(' ')

        for(str in vehicleModelList){
            trackId = trackId+str
        }

        for(str in vehicleList){
            trackId = trackId+str
        }

        var  user = hashMapOf(
            "uid" to currentUser!!.uid,
            "fullName" to username,
            "vehicleNum" to vehicleNum,
            "vehicleModel" to vehicleModel,
            "userType" to userType,
            "trackingID" to trackId.toLowerCase()
        )

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("user").document(currentUser!!.uid).set(user)
            .addOnSuccessListener {
                onBoardingState.value = OnBoardingState.ONBOARDING_DONE
            }
            .addOnFailureListener { e ->
                lastException = e

                onBoardingState.value = OnBoardingState.ERROR_UPDATE_PROFILE
            }

    }


    fun login(email: String, password: String){

        AuthenticationService.signinUser(email, password){ user, error ->

            if(error == null) {

                user?.let {
                    currentUser = it

                    onBoardingState.value = OnBoardingState.SIGNIN_DONE
                }
            }

            else {

                lastException = error

                onBoardingState.value = OnBoardingState.ERROR_SIGNIN
            }

        }

    }

    fun <T : Any?> MutableLiveData(defaultValue: T) = MutableLiveData<T>().apply {
        setValue(defaultValue)
    }

}