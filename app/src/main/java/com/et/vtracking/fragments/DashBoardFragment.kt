package com.engintech.vtracking.fragments


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.engintech.vtracking.models.CurrentUser
import com.engintech.vtracking.viewmodels.DashboardViewModel

import com.et.vtracking.R
import com.et.vtracking.activities.MainActivity
import com.et.vtracking.baseControls.BaseFragment
import com.et.vtracking.networkLayer.AuthenticationService
import kotlinx.android.synthetic.main.fragment_dash_board.view.*
import android.widget.EditText
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.content.ContextCompat


/**
 * A simple [Fragment] subclass.
 */
class DashBoardFragment : BaseFragment() {

    val dashboardViewModel by activityViewModels<DashboardViewModel>()


    lateinit var trackView: LinearLayout
    lateinit var shareView: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dash_board, container, false)
        setHasOptionsMenu(true)

        trackView = view.findViewById(R.id.track_view)
        shareView = view.findViewById(R.id.share_view)
        shareView.visibility = View.GONE
        trackView.visibility = View.GONE

        dashboardViewModel.userDataLoaded.observe(
            viewLifecycleOwner,
            Observer { loaded ->

                if(loaded){
                    view.uid_text.setText(CurrentUser.user.trackingID)

                    if(CurrentUser.user.userType.equals("Tracker")){
                        trackView.visibility = View.VISIBLE
                        shareView.visibility = View.GONE
                    } else {
                        shareView.visibility = View.VISIBLE
                        trackView.visibility = View.GONE
                    }
                }
            })

        view.share_id.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage =  CurrentUser.user.trackingID

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }

        }

        view.track_button.setOnClickListener {
            if(!view.uid_number.text.toString().equals("")) {
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        123
                    )
                } else {
                    dashboardViewModel.trackingID = view.uid_number.text.toString()
                    displayLoadingIndicator("Fetching user...")
                    dashboardViewModel.getTargetUserToTrack {error ->
                        if(error == null){
                            hideLoadingIndicator()
                            findNavController().navigate(R.id.action_dashBoardFragment_to_locationFragment)
                        }
                    }
                }
            } else {
                showPrompt(context!!, "Error", "Please Enter UniqueID")
            }
        }

        view.start_button.setOnClickListener {

            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                findNavController().navigate(R.id.action_dashBoardFragment_to_locationFragment)
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        setActionBarTitle("")
    }

    fun showShareDialog(){

        val dialogBuilder = AlertDialog.Builder(context)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.share_dialog, null)
        dialogBuilder.setView(dialogView)

        val editText = dialogView.findViewById(R.id.phone_edit_view) as EditText

        val okButton = dialogView.findViewById(R.id.ok_button) as Button

        val cancelButton = dialogView.findViewById(R.id.cancel_button) as Button

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        okButton.setOnClickListener {

            if(editText.text.isNotEmpty()){
                displayLoadingIndicator("loading...")

//                dashboardViewModel.shareLocationWith(editText.text.toString(), callback = { error ->
//                    if (error == null) {
//                        hideLoadingIndicator()
//                        alertDialog.dismiss()
//                        showPrompt(context!!, "Success", "Now you can start sharing Location")
//                    }
//                })
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {

        R.id.action_logout -> {

            AuthenticationService.logoutUser()

            val intent = Intent(context, MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    findNavController().navigate(R.id.action_dashBoardFragment_to_locationFragment)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

}
