package com.et.tracker.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.et.tracker.activities.DashBoardActivity
import com.et.tracker.models.CurrentUser

import com.et.tracker.R
import com.et.tracker.baseControls.BaseFragment
import com.et.tracker.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_email_sign_up.view.*

/**
 * A simple [Fragment] subclass.
 */
class EmailSignUpFragment : BaseFragment() {

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email_sign_up, container, false)

        view.signup_button.setOnClickListener {

            if(view.email.text.isNotEmpty() && view.password.text.isNotEmpty() &&  view.confirm_password.text.isNotEmpty()  && view.username.text.isNotEmpty() && view.phone.text.isNotEmpty()) {

                if(view.password.text.toString().equals(view.confirm_password.text.toString())) {

                    val isObserver = view.switch1.isChecked

                    displayLoadingIndicator("SigningUp")
                    onBoardingViewModel.signUp(
                        view.username.text.toString(),
                        view.email.text.toString(),
                        view.phone.text.toString(),
                        view.password.text.toString(),
                        isObserver
                    )
                } else{
                    showPrompt(context!!, "Error!", "Password does not match")
                }
            }
        }

        onBoardingViewModel.onBoardingState.observe(
            viewLifecycleOwner,
            Observer { state ->

                if(state == OnBoardingViewModel.OnBoardingState.ONBOARDING_DONE) {

                    hideLoadingIndicator()
//                    showPrompt(context!!, "SignUp!", "Successful")
                    CurrentUser.reloadUser()
                    goToNextScreen()
                }

                else if(state == OnBoardingViewModel.OnBoardingState.ERROR_SIGNUP) {

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

        setActionBarTitle("SignUp")
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
