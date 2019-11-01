package  com.et.vtracking.fragments


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.engintech.vtracking.activities.DashBoardActivity
import com.engintech.vtracking.viewmodels.DashboardViewModel
import com.et.vtracking.baseControls.BaseFragment
import com.et.vtracking.R
import com.et.vtracking.viewmodels.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_obpin_code.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class OBPinCodeFragment : BaseFragment() {

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()
    val dashboardViewModel by activityViewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_obpin_code, container, false)

//        setHasOptionsMenu(true)

        view.phone_number_text_view.text = onBoardingViewModel.verifiedPhoneNumber

        view.verify_now_button.setOnClickListener {

            val pinCode = view.pin_code_field.text.toString()

            if(pinCode.length == 6) {
                displayLoadingIndicator("Verifying...")
                onBoardingViewModel.verifyPinCode(pinCode)
            }
        }

        onBoardingViewModel.onBoardingState.observe(
            viewLifecycleOwner,
            Observer { state ->

                if(state == OnBoardingViewModel.OnBoardingState.UPDATE_PROFILE) {

                    hideLoadingIndicator()
                    findNavController().navigate(R.id.action_OBPinCodeFragment_to_onboardingFragment)
                }

                else if(state == OnBoardingViewModel.OnBoardingState.RESEND_PIN_CODE) {

                    hideLoadingIndicator()

                    activity?.let {fragmentActivity ->

                        showPrompt(fragmentActivity, "Alert!", "New code sent")
                    }

                    view.pin_code_field.setText("")
                }

                else if(state == OnBoardingViewModel.OnBoardingState.ONBOARDING_DONE) {

                    hideLoadingIndicator()
                    goToLoggedInScreen()
                    //this is a returning user lets take him to logged in flow

                }

                else if(
                    state == OnBoardingViewModel.OnBoardingState.ERROR_VERIFY_PIN_CODE
                    || state == OnBoardingViewModel.OnBoardingState.ERROR_RESEND_PIN_CODE
                ) {

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

    private fun goToProfileSettingsScreen() {

//        findNavController().navigate(R.id.action_OBPinCodeFragment_to_OBProfileSettingsFragment)
    }

    private fun goToLoggedInScreen() {

        val intent = Intent(context, DashBoardActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        startActivity(intent)

        activity?.finish()
    }


    override fun onStart() {
        super.onStart()

        setActionBarTitle("")
    }

//    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
//        menuInflater.inflate(R.menu.pin_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
//
//        R.id.action_resend -> {
//
//            displayLoadingIndicator("Sending Request")
//
//            onBoardingViewModel.resendPinCode()
//
//            true
//        }
//        else -> super.onOptionsItemSelected(item)
//    }
}
