package com.et.vtracking.support

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.et.vtracking.R
import com.et.vtracking.fragments.*
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    @JvmStatic fun strippedPhoneNumber(phoneNumber:String) : String {

        return (phoneNumber)
            .replace(" ", "")
            .replace("(","")
            .replace(")", "")
            .replace("-","")
    }


    fun getInitialsFromString(string: String): String {

        return string.split(' ')
            .mapNotNull { it.firstOrNull()?.toString() }
            .reduce { acc, s -> acc + s }

    }

}