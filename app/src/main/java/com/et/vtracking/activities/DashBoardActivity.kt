package com.engintech.vtracking.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.et.vtracking.R

class DashBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment)

        val topLevelScreens = setOf(
            R.id.dashBoardFragment
        )

        val appBarConfiguration = AppBarConfiguration.Builder(topLevelScreens).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
