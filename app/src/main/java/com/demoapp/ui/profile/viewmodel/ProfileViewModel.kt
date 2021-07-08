package com.demoapp.ui.profile.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.demoapp.R
import com.demoapp.apputils.Constant
import com.demoapp.apputils.Debug
import com.demoapp.apputils.Utils
import com.demoapp.base.viewmodel.BaseViewModel
import com.demoapp.databinding.ActivityLoginBinding
import com.demoapp.databinding.ActivityProfileBinding
import com.demoapp.interfaces.TopBarClickListener
import com.demoapp.ui.login.view.LoginActivity
import com.demoapp.ui.profile.view.ProfileActivity
import com.demoapp.ui.signup.database.UserRoomDatabase
import com.demoapp.ui.signup.datamodel.UserData
import com.demoapp.ui.signup.view.SignupActivity
import com.demoapp.validator.EmailValidator
import com.demoapp.validator.PasswordValidator

class ProfileViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityProfileBinding
    private lateinit var mContext: Context
    var application_ = application
    var allUsers: List<UserData> = ArrayList()

    fun setBinder(binder: ActivityProfileBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        userDB = UserRoomDatabase.getDatabase(application_)
        userDao = userDB?.userDao()
        mAllUsers = userDao?.getAllUsers()
        getAllUserData()
            ?.observe(mContext as ProfileActivity,
                Observer<List<UserData>?> { users ->
                    allUsers = users
                    users.forEachIndexed { index, element ->
                        Debug.e(
                            "all users",
                            element.email.toString() + " - " + element.password.toString()
                        )
                    }
                })
        init()
    }

    private fun init() {
        var loginData = Utils.getLoginData(mContext)
        this.binder.tvFname.text = loginData?.fname
        this.binder.tvLname.text = loginData?.lname
        this.binder.tvEmail.text = loginData?.email
        this.binder.tvUsername.text = loginData?.username
        this.binder.tvBdate.text = loginData?.dob
        this.binder.tvAddress.text = loginData?.address
        binder.ivProfilePic.setImageBitmap(getImageFromBLOB(loginData?.profilePic!!))
    }

    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.profile))) {
                try {

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        override fun onBackClicked(view: View?) {

        }
    }

    inner class ViewClickHandler {

        fun onLogout(view: View) {
            try {
                Utils.clearLoginCredentials(mContext)
                LocalBroadcastManager.getInstance(mContext)
                    .sendBroadcast(Intent(Constant.FINISH_ACTIVITY))
                val intent = Intent(mContext, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                mContext.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

