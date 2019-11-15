package com.et.tracker.support

import android.app.Application

class Application: Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        SharedPreferences.init(this)
    }

    companion object {
        private var instance: Application? = null

        @JvmStatic
        fun applicationContext() : Application {
            return instance as Application
        }
    }
}