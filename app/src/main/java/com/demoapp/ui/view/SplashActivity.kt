package com.demoapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.demoapp.R
import com.demoapp.apputils.Debug
import com.demoapp.apputils.ErrorAlertDialog
import com.demoapp.apputils.Utils
import com.demoapp.base.view.BaseActivity
import com.demoapp.databinding.ActivitySplashBinding
import com.demoapp.exceptions.networks.NoInternetException
import com.demoapp.ui.MyApplication
import com.demoapp.ui.home.view.HomeActivity
import com.demoapp.ui.login.view.LoginActivity
import java.io.PrintWriter
import java.io.StringWriter


class SplashActivity : BaseActivity() {
    internal var TAG = "SplashActivity"
    internal var handler = Handler()
    internal lateinit var binding: ActivitySplashBinding
    var mApplication: MyApplication? = null

    internal var startApp: Runnable = object : Runnable {
        override fun run() {
            handler.removeCallbacks(this)
            mApplication?.wasInBackground = false
            @Suppress("DEPRECATION")
            if (Utils.isUserLoggedIn(activity)!!) {
                val i = Intent(activity, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            } else {
                val i = Intent(activity, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.common_bg)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        mApplication = application as MyApplication

        if (Debug.DEBUG_EXCEPTION) {
            Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->

                try {
                    val sw = StringWriter()
                    paramThrowable.printStackTrace(PrintWriter(sw))
                    Utils.sendCrashReport(sw.toString(), activity)
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }
            }
        }
        Utils.getHashKey()


        try {
            if (Utils.isInternetConnected(activity)) {


                checkPermission(this, permissionsListener)

            } else {
                handler.postDelayed(dialogRunnable, 100)
            }


        } catch (e: NoInternetException) {
            e.printStackTrace()
        }
        //  getFirebaseToken()
    }

    private val permissionsListener = object : PermissionListener {
        override fun onGranted() {
            startApplication(1000)

        }

        override fun onDenied() {
            showPermissionAlert()
        }
    }


    public fun startApplication(sleepTime: Long) {
        handler.postDelayed(startApp, sleepTime)
    }

    public override fun onDestroy() {
        try {
            handler.removeCallbacks(startApp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun showInternetDialog() {
        ErrorAlertDialog(activity).setTitle(getString(R.string.connection_title))
            .setMessage(getString(R.string.connection_not_available))
            .setNegativeButton(activity.getString(R.string.cancel))
            .setPositiveButton(activity.getString(R.string.settings))
            .setOnButtonClickListener(object : ErrorAlertDialog.DialogButtonClick {
                override fun onPositiveClick() {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                }

                override fun onNegativeClick() {
                    finish()
                }
            }).show()


    }

    internal var dialogRunnable: Runnable = object : Runnable {
        override fun run() {
            handler.removeCallbacks(this)
            showInternetDialog()
        }
    }
}
