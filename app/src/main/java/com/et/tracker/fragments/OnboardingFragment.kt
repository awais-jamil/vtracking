package com.et.tracker.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.et.tracker.activities.DashBoardActivity
import com.et.tracker.viewmodels.DashboardViewModel

import com.et.tracker.R
import com.et.tracker.baseControls.BaseFragment
import com.et.tracker.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_onboarding.view.*

/**
 * A simple [Fragment] subclass.
 */
class OnboardingFragment : BaseFragment() {

    val dashboardViewModel by activityViewModels<DashboardViewModel>()

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        view.done_button.setOnClickListener {

            if(!onBoardingViewModel.userType.equals("Parent")) {
                if (view.username.text.isNotEmpty() && view.vehicle_number.text.isNotEmpty() && view.vehicle_model.text.isNotEmpty() && !onBoardingViewModel.userType.toString().equals("")) {

                    displayLoadingIndicator("Saving...")
                    onBoardingViewModel.saveUserData(
                        view.username.text.toString(),
                        view.vehicle_number.text.toString(),
                        view.vehicle_model.text.toString(),
                        onBoardingViewModel.userType!!
                    )
                } else {
                    showPrompt(context!!, "Error!", "Please provide all data")
                }
            } else {
                if (view.username.text.isNotEmpty()) {

                    displayLoadingIndicator("Saving...")
                    onBoardingViewModel.saveUserData(
                        view.username.text.toString(),
                        "",
                        "",
                        onBoardingViewModel.userType!!
                    )
                } else {
                    showPrompt(context!!, "Error!", "Please provide all data")
                }
            }
        }

        onBoardingViewModel.onBoardingState.observe(
            viewLifecycleOwner,
            Observer { state ->

                if(state == OnBoardingViewModel.OnBoardingState.ONBOARDING_DONE) {

                    hideLoadingIndicator()
                    goToNextScreen()
                }

                else if(state == OnBoardingViewModel.OnBoardingState.ERROR_UPDATE_PROFILE) {

                    hideLoadingIndicator()

                    onBoardingViewModel.lastException?.let {exception ->

                        activity?.let {fragmentActivity ->

                            showPrompt(fragmentActivity, "Error!", exception.localizedMessage)
                        }
                    }
                }
            })

        if(onBoardingViewModel.userType.equals("Parent")){
            view.service_view.visibility = View.GONE
        } else {
            view.service_view.visibility = View.VISIBLE
        }

        return  view
    }
    override fun onStart() {
        super.onStart()

        setActionBarTitle("OnBoarding")

    }

    override fun onResume() {
        super.onResume()

        onBoardingViewModel.onBoardingState.value = OnBoardingViewModel.OnBoardingState.SIGNIN
    }

    fun goToNextScreen(){

        val intent = Intent(context, DashBoardActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        startActivity(intent)
    }

}
