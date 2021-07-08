package com.demoapp.ui.login.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.demoapp.R
import com.demoapp.apputils.Constant
import com.demoapp.apputils.Debug
import com.demoapp.apputils.Utils
import com.demoapp.base.viewmodel.BaseViewModel
import com.demoapp.databinding.ActivityLoginBinding
import com.demoapp.ui.home.view.HomeActivity
import com.demoapp.ui.login.view.LoginActivity
import com.demoapp.ui.signup.database.UserRoomDatabase
import com.demoapp.ui.signup.datamodel.UserData
import com.demoapp.ui.signup.view.SignupActivity
import com.demoapp.validator.EmailValidator
import com.demoapp.validator.PasswordValidator
import com.google.gson.Gson


class LoginViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityLoginBinding
    private lateinit var mContext: Context
    var application_ = application
    var allUsers: List<UserData> = ArrayList()

    fun setBinder(binder: ActivityLoginBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        userDB = UserRoomDatabase.getDatabase(application_)
        userDao = userDB?.userDao()
        mAllUsers = userDao?.getAllUsers()
        getAllUserData()
            ?.observe(mContext as LoginActivity,
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

    }

    inner class ViewClickHandler {
        fun onSignin(view: View) {
            try {
                val userAvailable = allUsers.find {
                    it.email == binder.edtEmail.text.toString().trim()
                }
                if (userAvailable != null && (userAvailable.email == binder.edtEmail.text.toString()
                        .trim() && userAvailable.password == binder.edtPassword.text.toString()
                        .trim())
                ) {
                    Utils.setPref(
                        mContext,
                        Constant.LOGIN_INFO,
                        Gson().toJson(userAvailable)
                    )
                    val i = Intent(mContext, HomeActivity::class.java)
                    i.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    mContext.startActivity(i)
                } else {
                    showToast("invalid user_id or password.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onSignup(view: View) {
            try {
                val i = Intent(mContext, SignupActivity::class.java)
                mContext.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isValidate(): Boolean {
        val emailValidator = EmailValidator(binder.edtEmail.text.toString().trim())
        val passwordValidator = PasswordValidator(mContext, binder.edtPassword.text.toString())
        if (!emailValidator.isValid()) {
            showToast((mContext as Activity).getString(R.string.empty_field))
            return false
        } else if (binder.edtPassword.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(R.string.empty_field))
            return false
        }
        return true
    }


}

