package com.et.tracker.activities

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.et.tracker.R
import com.et.tracker.baseControls.BaseActivity

class OnBoardingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

//        setSupportActionBar(findViewById(R.id.toolbar))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val navController = findNavController(R.id.nav_host_fragment)

        val topLevelScreens = setOf(
            R.id.OBNumberVerificationFragment
        )

        val appBarConfiguration = AppBarConfiguration.Builder(topLevelScreens).build()

//        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
