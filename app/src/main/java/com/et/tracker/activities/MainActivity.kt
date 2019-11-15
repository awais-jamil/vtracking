package com.et.tracker.activities

import android.content.Intent
import android.os.Bundle
import com.et.tracker.activities.DashBoardActivity
import com.et.tracker.R
import com.et.tracker.baseControls.BaseActivity
import com.et.tracker.networkLayer.AuthenticationService

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if(AuthenticationService.needsAuthentication()) {

            //Go to On Boarding user flow
            goToOnBoarding()
        }

        else {

            proceedToLoggedFlow()
        }
    }

    private fun goToOnBoarding() {

        val intent = Intent(applicationContext, OnBoardingActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        startActivity(intent)

        finish()
    }

    private fun proceedToLoggedFlow() {
//
        val intent = Intent(applicationContext, DashBoardActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        startActivity(intent)

        finish()
    }

}
