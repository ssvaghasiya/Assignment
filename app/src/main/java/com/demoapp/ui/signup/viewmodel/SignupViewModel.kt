package com.demoapp.ui.signup.viewmodel

import android.R
import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.demoapp.apputils.Debug
import com.demoapp.apputils.UriHelper
import com.demoapp.base.viewmodel.BaseViewModel
import com.demoapp.databinding.ActivitySignupBinding
import com.demoapp.ui.login.view.LoginActivity
import com.demoapp.ui.signup.database.UserRoomDatabase
import com.demoapp.ui.signup.datamodel.UserData
import com.demoapp.ui.signup.view.SignupActivity
import com.demoapp.validator.EmailValidator
import com.demoapp.validator.PasswordValidator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SignupViewModel(application: Application) : BaseViewModel(application),
    AdapterView.OnItemSelectedListener {

    private lateinit var binder: ActivitySignupBinding
    private lateinit var mContext: Context
    var cal = Calendar.getInstance()
    var application_ = application
    var securityQuestions = arrayOf(
        "Select Security Question",
        "What is your mother's maiden name?",
        "What is the name of your first pet?",
        "What was your first car?"
    )
    var selectedQuestion: String? = null
    var blob: ByteArray? = null
    var dob: String? = null
    var allUsers: List<UserData> = ArrayList()

    fun setBinder(binder: ActivitySignupBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        userDB = UserRoomDatabase.getDatabase(application_)
        userDao = userDB?.userDao()
        mAllUsers = userDao?.getAllUsers()
        getAllUserData()
            ?.observe(mContext as SignupActivity,
                Observer<List<UserData>?> { users ->
                    allUsers = users
                })
        init()
    }

    private fun init() {
        binder.spinner.onItemSelectedListener = this;
        val aa: ArrayAdapter<*> =
            ArrayAdapter<String?>(mContext, R.layout.simple_spinner_item, securityQuestions)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binder.spinner.adapter = aa

    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
        if (position != 0) {
            selectedQuestion = securityQuestions[position]
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_PICK_IMAGE) {
            val tmpFileUri = data!!.data
            Debug.e("", "tmp_fileUri : " + tmpFileUri!!.path!!)
            val selectedImagePath = UriHelper.getPath(
                mContext as Activity,
                tmpFileUri
            )

            fileUri = File(selectedImagePath!!)
            if (fileUri != null && fileUri!!.exists()) {
                afterImageSelected(fileUri)

                Debug.e("", "fileUri : " + fileUri!!.absolutePath)
            }
        } else if (requestCode == REQ_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                if (fileUri == null || !fileUri!!.exists()) {
                    val tmpFileUri = data!!.data
                    Debug.e("", "tmp_fileUri : " + tmpFileUri!!.path!!)
                    val selectedImagePath = UriHelper.getPath(
                        mContext as Activity, tmpFileUri
                    )
                    fileUri = File(selectedImagePath!!)

                }
                if (fileUri != null && fileUri!!.exists()) {
                    afterImageSelected(fileUri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun afterImageSelected(fileUri: File?) {
        try {
            var imageUri = Uri.fromFile(File(fileUri!!.path))
            binder.ivProfilePic.setImageBitmap(BitmapFactory.decodeFile(fileUri.path))
            blob = getBlob(imageUri, mContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewClickHandler {

        fun onProfilePic(view: View) {
            try {
                checkPermissionStorageAndCamera(
                    mContext as Activity,
                    object : PermissionListener {
                        override fun onGranted() {
                            showPictureChooser(mContext)
                        }

                        override fun onDenied() {
                            showPermissionAlert(mContext)
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }


        fun onDatePicker(view: View) {
            try {
                var mDatePicker = DatePickerDialog(
                    mContext,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                mDatePicker.datePicker.maxDate = System.currentTimeMillis()
                mDatePicker.show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onSignup(view: View) {
            try {
                if (isValidate()) {
                    val userAvailable = allUsers.find {
                        it.email == binder.edtEmail.text.toString().trim()
                    }
                    if (userAvailable == null) {
                        val user_id = UUID.randomUUID().toString()
                        val user = UserData(
                            user_id,
                            binder.edtFname.text.toString().trim(),
                            binder.edtLname.text.toString().trim(),
                            binder.edtEmail.text.toString().trim(),
                            dob,
                            binder.edtUsername.text.toString().trim(),
                            binder.edtPassword.text.toString().trim(),
                            selectedQuestion,
                            binder.edtSecAns.text.toString().trim(),
                            binder.edtAddress.text.toString().trim(),
                            blob
                        )
                        insert(user)
                        val i = Intent(mContext, LoginActivity::class.java)
                        i.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        mContext.startActivity(i)
                    } else {
                        showToast("This email is already available")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onLogin(view: View) {
            try {
                val i = Intent(mContext, LoginActivity::class.java)
                i.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                mContext.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateDateInView() {
        dob = getRequiredFormat("YYYY-MM-dd")
        binder.tvBdate.text = dob
    }

    fun getRequiredFormat(format: String): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(cal.time).toString()
    }

    fun isValidate(): Boolean {
        val emailValidator = EmailValidator(binder.edtEmail.text.toString().trim())
        val passwordValidator = PasswordValidator(mContext, binder.edtPassword.text.toString())
        if (binder.edtFname.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.first_name_error))
            return false
        } else if (binder.edtLname.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.last_name_error))
            return false
        } else if (!emailValidator.isValid()) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.email_error))
            return false
        } else if (binder.edtUsername.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.username_error))
            return false
        } else if (binder.edtPassword.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.password_error))
            return false
        } else if (binder.edtConfirmPswd.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.confirmpswd_error))
            return false
        } else if (dob
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.dob_error))
            return false
        } else if (selectedQuestion.isNullOrEmpty()) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.security_que_error))
            return false
        } else if (binder.edtSecAns.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.security_answer_error))
            return false
        } else if (binder.edtAddress.text.toString().trim()
                .isNullOrEmpty()
        ) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.address_error))
            return false
        } else if (blob == null) {
            showToast((mContext as Activity).getString(com.demoapp.R.string.profile_pic_error))
            return false
        }

        if ((binder.edtPassword.text.toString().trim()
                .isNullOrEmpty().not() && binder.edtConfirmPswd.text.toString().trim()
                .isNullOrEmpty().not()) && (binder.edtPassword.text.toString().trim()
                    != binder.edtConfirmPswd.text.toString().trim())
        ) {
            showToast("Password and Confirm Password Not matched")
            return false
        } else if ((binder.edtPassword.text.toString().trim()
                .isNullOrEmpty().not() && binder.edtConfirmPswd.text.toString().trim()
                .isNullOrEmpty().not()) && (binder.edtPassword.text.toString().trim()
                    == binder.edtConfirmPswd.text.toString().trim())
        ) {
            if (!checkPassword(binder.edtPassword.text.toString().trim())) {
                showToast("Password field must be 8 characters long and contain one capital letter")
                return false
            }
        }
        return true
    }


    private fun checkPassword(str: String): Boolean {
        var ch: Char
        var capitalFlag = false
        var lowerCaseFlag = false
        var numberFlag = false
        for (element in str) {
            ch = element
            if (Character.isDigit(ch)) {
                numberFlag = true
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true
            }
            return capitalFlag && str.length >= 8
        }
        return false
    }
}
