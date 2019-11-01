package com.et.vtracking.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.engintech.vtracking.activities.DashBoardActivity
import com.engintech.vtracking.models.CurrentUser

import com.et.vtracking.R
import com.et.vtracking.baseControls.BaseFragment
import com.et.vtracking.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_onboarding.view.*

/**
 * A simple [Fragment] subclass.
 */
class OnboardingFragment : BaseFragment() {

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        val adapter = ArrayAdapter.createFromResource(context,
            R.array.user_type, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinner.adapter = adapter


        view.done_button.setOnClickListener {

            if(view.username.text.isNotEmpty() && view.vehicle_number.text.isNotEmpty() &&  view.vehicle_model.text.isNotEmpty()  &&  !view.spinner.selectedItem.toString().equals("")) {

                    displayLoadingIndicator("Saving...")
                    onBoardingViewModel.saveUserData(
                        view.username.text.toString(),
                        view.vehicle_number.text.toString(),
                        view.vehicle_model.text.toString(),
                        view.spinner.selectedItem.toString().toString()
                    )
                } else{
                    showPrompt(context!!, "Error!", "Please provide all data")
                }
        }

        onBoardingViewModel.onBoardingState.observe(
            viewLifecycleOwner,
            Observer { state ->

                if(state == OnBoardingViewModel.OnBoardingState.ONBOARDING_DONE) {

                    hideLoadingIndicator()
                    CurrentUser.reloadUser()
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
