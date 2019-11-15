package com.et.tracker.fragments


import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.et.tracker.baseControls.BaseFragment
import com.et.tracker.R
import com.et.tracker.support.TextDrawable
import com.et.tracker.support.Utils
import com.et.tracker.viewmodels.OnBoardingViewModel
import com.rilixtech.CountryCodePicker
import kotlinx.android.synthetic.main.fragment_obnumber_varification.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class OBNumberVerificationFragment : BaseFragment() {

    lateinit var countryCodePicker: CountryCodePicker
    lateinit var editText:EditText

    lateinit var phoneNumberUtil: PhoneNumberUtil

    val onBoardingViewModel by activityViewModels<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_obnumber_varification, container, false)

        phoneNumberUtil = PhoneNumberUtil.getInstance()

        countryCodePicker = view.findViewById(R.id.country_code_picker)

        countryCodePicker.setOnCountryChangeListener {

            editText.setCompoundDrawables(TextDrawable(editText, countryCodePicker.selectedCountryCodeWithPlus+" "), null, null, null)
        }

        editText = view.findViewById(R.id.phone_number_edittext)
        editText.addTextChangedListener(PhoneNumberFormattingTextWatcher(countryCodePicker.defaultCountryNameCode))
        editText.setCompoundDrawables(TextDrawable(editText, countryCodePicker.defaultCountryCodeWithPlus+" "), null, null, null)

        view.send_code_button.setOnClickListener {

            if(editText.text.isNotEmpty()) {

                val numberWithCountryCode: String = countryCodePicker.selectedCountryCodeWithPlus + editText.text.toString()

                val strippedPhoneNumber: String = Utils.strippedPhoneNumber(numberWithCountryCode)

                val phoneNumber: Phonenumber.PhoneNumber = phoneNumberUtil.parse(strippedPhoneNumber, null)
                if (phoneNumberUtil.isValidNumber(phoneNumber)) {

                    displayLoadingIndicator("Verifying Phone Number")
                    onBoardingViewModel.verifyPhoneNumber(strippedPhoneNumber)

                } else {

                    Toast.makeText(activity, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                }
            }
        }

        onBoardingViewModel.onBoardingState.observe(
            viewLifecycleOwner,
            Observer { state ->

                if(state == OnBoardingViewModel.OnBoardingState.VERIFY_PIN_CODE) {

                    hideLoadingIndicator()

                    goToPinCodeScreen()
                }

                else if(state == OnBoardingViewModel.OnBoardingState.ERROR_VERIFY_PHONE_NUMBER) {

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

        setActionBarTitle("")
    }

    private fun goToPinCodeScreen() {

        findNavController().navigate(R.id.action_OBNumberVerificationFragment_to_OBPinCodeFragment)
    }
}
