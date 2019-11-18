package com.et.tracker.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.et.tracker.R
import com.et.tracker.baseControls.BaseFragment
import com.et.tracker.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_onboarding_user.view.*

/**
 * A simple [Fragment] subclass.
 */
class OnboardingUserFragment : BaseFragment() {

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding_user, container, false)

        val adapter = ArrayAdapter.createFromResource(context,
            R.array.user_type, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinner.adapter = adapter

        view.done_button.setOnClickListener {

            if(!view.spinner.selectedItem.toString().equals("")) {
                onBoardingViewModel.userType =  view.spinner.selectedItem.toString()

                findNavController().navigate(R.id.action_OBUserFragment_to_onboardingFragment)
            } else{
                showPrompt(context!!, "Error!", "Please select user type")
            }
        }

        return view
    }


}
