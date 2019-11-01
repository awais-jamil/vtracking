package com.et.vtracking.models

import com.et.vtracking.networkLayer.AuthenticationService
import java.io.Serializable

data class User(
    var uid: String = "",
    var fullName: String = "",
    var vehicleNum: String = "",
    var vehicleModel: String = "",
    var userType: String = "",
    var trackingID:  String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
):Serializable {

    fun User.isCurrent(): Boolean {

        AuthenticationService.currentUser?.let {

            return it.uid.equals(uid)
        }

        return false
    }

    override fun equals(other: Any?): Boolean {

        other?.let {

            return uid.equals((it as User).uid)
        }

        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}