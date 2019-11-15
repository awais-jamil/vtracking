package com.et.tracker.fragments


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.et.tracker.models.CurrentUser
import com.et.tracker.viewmodels.DashboardViewModel

import com.et.tracker.R
import com.et.tracker.baseControls.BaseFragment
import kotlinx.android.synthetic.main.fragment_dash_board.view.*
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.et.tracker.activities.DashBoardActivity


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

        trackView = view.findViewById(R.id.track_view)
        shareView = view.findViewById(R.id.share_view)
        trackView.visibility = View.INVISIBLE

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

        view.menu_butn.setOnClickListener {
            (activity as DashBoardActivity).openDrawer()
        }

        dashboardViewModel.userDataLoaded.observe(
            viewLifecycleOwner,
            Observer { loaded ->

                if(loaded){
                    hideLoadingIndicator()
                    loadUI()
                }
            })

        return view
    }

    override fun onStart() {
        super.onStart()

        setActionBarTitle("")

        if(CurrentUser.user.fullName.equals("")){
            displayLoadingIndicator("Fetching Data...")
        }
    }

    fun loadUI(){

        if(dashboardViewModel.currentUser.userType.equals("Tracker")){
            trackView.visibility = View.VISIBLE
            shareView.visibility = View.INVISIBLE
        } else {
            shareView.visibility = View.VISIBLE
            trackView.visibility = View.INVISIBLE
        }
    }
}
