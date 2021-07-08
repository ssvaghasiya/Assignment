package com.demoapp.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.demoapp.apputils.Constant
import com.demoapp.apputils.Debug


class MyApplication : Application() {

    var context: Context? = null
    var wasInBackground = false

    override fun onCreate() {
        super.onCreate()
        context = applicationContext;

        ProcessLifecycleOwner.get().lifecycle.addObserver(foregroundBackgroundListener)
    }

    internal var foregroundBackgroundListener = ForegroundBackgroundListener()

    internal inner class ForegroundBackgroundListener : LifecycleObserver {

        var handler = Handler()
        var runGoToBackground: Runnable = Runnable {
            LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(Intent(Constant.APP_GOES_BACKGROUND))
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun startSomething() {
            Debug.e("ProcessLog", "APP IS ON FOREGROUND")
            wasInBackground = true
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun stopSomething() {
            handler.removeCallbacks(runGoToBackground)
            handler.postDelayed(runGoToBackground, 100)
            Debug.e("ProcessLog", "APP IS ON BACKGROUND")
            wasInBackground = false
        }
    }

}