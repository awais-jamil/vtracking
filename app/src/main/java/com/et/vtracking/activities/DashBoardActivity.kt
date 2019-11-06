package com.engintech.vtracking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.et.vtracking.R
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.et.vtracking.activities.MainActivity
import com.et.vtracking.networkLayer.AuthenticationService
import com.et.vtracking.support.Application


class DashBoardActivity : AppCompatActivity() {

    var mDrawerLayout: DrawerLayout? = null
    var nv: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        val navController = findNavController(R.id.nav_host_fragment)
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)


        mDrawerLayout = findViewById(R.id.layout) as (DrawerLayout)

        nv = findViewById(R.id.nav_view) as (NavigationView)
        nv!!.setNavigationItemSelectedListener(navSelectListener)

    }

    public fun openDrawer(){
        mDrawerLayout!!.openDrawer(nv!!)
    }

    private val navSelectListener = object : NavigationView.OnNavigationItemSelectedListener {

       override fun onNavigationItemSelected(item: MenuItem): Boolean {

            item.setChecked(true)
            mDrawerLayout!!.closeDrawer(nv!!)

           when(item.itemId) {
               R.id.home -> {

                   findNavController(R.id.nav_host_fragment).navigate(R.id.action_locationFragment_to_dashBoardFragment)

                   true
               }

               R.id.logout -> {

                    AuthenticationService.logoutUser()

                    val intent = Intent(Application.applicationContext(), MainActivity::class.java)

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    startActivity(intent)

                   true
               }

           }
                return true
        }
    }

}
