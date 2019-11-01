package com.et.vtracking.baseControls

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.et.vtracking.R

open class BaseActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        val childFragment = navHostFragment?.getChildFragmentManager()?.getFragments()?.get(0)

        if((childFragment as BaseFragment).interruptTouchEvents) {
            if (currentFocus != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
