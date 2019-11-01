package com.et.vtracking.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.engintech.vtracking.activities.DashBoardActivity
import com.engintech.vtracking.models.CurrentUser

import com.et.vtracking.R
import com.et.vtracking.baseControls.BaseFragment
import com.et.vtracking.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_email_login.view.*

/**
 * A simple [Fragment] subclass.
 */
class EmailLoginFragment : BaseFragment() {

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email_login, container, false)

        view.signup_button.setOnClickListener {
//            findNavController().navigate(R.id.action_emailLoginFragment_to_emailSignUpFragment)
        }


        view.login_button.setOnClickListener {

            if(view.email.text.isNotEmpty() && view.password.text.isNotEmpty() ) {

                displayLoadingIndicator("Loading")
                onBoardingViewModel.login(view.email.text.toString(), view.password.text.toString())
            }
        }

        onBoardingViewModel.onBoardingState.observe(
            viewLifecycleOwner,
            Observer { state ->

                if(state == OnBoardingViewModel.OnBoardingState.SIGNIN_DONE) {

                    hideLoadingIndicator()
                    CurrentUser.reloadUser()
                    goToNextScreen()
                }

                else if(state == OnBoardingViewModel.OnBoardingState.ERROR_SIGNIN) {

                    hideLoadingIndicator()

                    onBoardingViewModel.lastException?.let {exception ->

                        activity?.let {fragmentActivity ->

                            showPrompt(fragmentActivity, "Error!", exception.localizedMessage)
                        }
                    }
                }
            })

        return view
    }

    override fun onStart() {
        super.onStart()

        setActionBarTitle("Login")
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
