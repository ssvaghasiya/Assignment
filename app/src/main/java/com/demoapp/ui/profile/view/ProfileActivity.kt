package com.demoapp.ui.profile.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.demoapp.R
import com.demoapp.base.view.BaseActivity
import com.demoapp.databinding.ActivityProfileBinding
import com.demoapp.databinding.ActivitySignupBinding
import com.demoapp.ui.profile.viewmodel.ProfileViewModel
import com.demoapp.ui.signup.viewmodel.SignupViewModel

class ProfileActivity : BaseActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewModel = ViewModelProvider(activity).get(ProfileViewModel::class.java)
        viewModel.setBinder(binding)
    }
}