package com.demoapp.ui.signup.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.demoapp.R
import com.demoapp.base.view.BaseActivity
import com.demoapp.databinding.ActivityLoginBinding
import com.demoapp.databinding.ActivitySignupBinding
import com.demoapp.ui.login.viewmodel.LoginViewModel
import com.demoapp.ui.signup.viewmodel.SignupViewModel

class SignupActivity : BaseActivity() {

    lateinit var binding: ActivitySignupBinding
    lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        viewModel = ViewModelProvider(activity).get(SignupViewModel::class.java)
        viewModel.setBinder(binding)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

}