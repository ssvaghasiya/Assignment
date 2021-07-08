package com.demoapp.base.viewmodel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import com.demoapp.R
import com.demoapp.apputils.Debug
import com.demoapp.apputils.Utils
import com.demoapp.base.utils.SideMenuAdapter
import com.demoapp.base.view.BaseActivity
import com.demoapp.databinding.DialogPicChooserBinding
import com.demoapp.interfaces.BottomBarClickListener
import com.demoapp.ui.signup.dao.UserDao
import com.demoapp.ui.signup.database.UserRoomDatabase
import com.demoapp.ui.signup.datamodel.UserData
import com.demoapp.ui.view.SplashActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mikepenz.materialdrawer.BuildConfig
import com.mikepenz.materialdrawer.Drawer
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


open class BaseViewModel(application: Application) : AppViewModel(application) {
    lateinit var result: Drawer
    private lateinit var activity: Activity


    fun finishActivity(mContext: Context) {

//        if (mContext is HomeActivity) {
//
//        } else
        (mContext as Activity).finish()

    }

    fun initDrawer(mContext: Context) {
//        initDrawer((mContext as Activity), mContext)
    }

    private lateinit var mAdapter: SideMenuAdapter


    private fun onMenuItemClicked(menuId: String, view: View) {
        when (menuId) {
            "1" -> {
                if (activity is SplashActivity) {
                    hideMenu(true)
                } else {
                    val intent = Intent(activity, SplashActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    activity.startActivity(intent)
                    hideMenu(false)
                }
            }

            "2" -> {

            }
            "3" -> {

            }

            "4" -> {

            }

            "5" -> {


            }
            "6" -> {

            }
        }

    }


    fun hideMenu(b: Boolean) {
        try {
            result.closeDrawer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onTopMenuClick() {
        toggleDrawer()
    }

    private fun toggleDrawer() {
        if (result.isDrawerOpen) {
            result.closeDrawer()
        } else {
            result.openDrawer()
        }
    }

    private fun setupFullHeight(
        v: View,
        dialogInterface: DialogInterface,
        linearLayout: LinearLayout,
        mContext: Context
    ) {

        val bottomSheetBehavior = BottomSheetBehavior.from((v.getParent()) as View)
        val layoutParams = linearLayout.layoutParams
        val windowHeight = getWindowHeight(mContext)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        linearLayout.layoutParams = layoutParams
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            R.id.design_bottom_sheet
        )
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @Suppress("DEPRECATION")
    private fun getWindowHeight(mContext: Context): Int { // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    interface PermissionListener {
        fun onGranted()

        fun onDenied()
    }


    fun parseDate(time: String?, output: String?): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = output
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {


            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }

    fun scrollingWhileKeyboard(
        mContext: Context,
        layout: ViewGroup,
        btnBottom: Button?,
        llBottom: LinearLayout? = null,
        llParent: LinearLayout? = null
    ) {
        layout.viewTreeObserver
            .addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
                val r = Rect()
                try {
                    layout.getWindowVisibleDisplayFrame(r)
                    val screenHeight: Int = layout.rootView.height
                    val keypadHeight = screenHeight - r.bottom
                    if (keypadHeight > screenHeight * 0.15) {
                        btnBottom?.visibility = View.INVISIBLE
                        if (llParent != null) {
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            val bottom: Int =
                                (mContext as Activity).resources.getDimensionPixelSize(R.dimen._20sdp)
                            layoutParams.setMargins(0, 0, 0, bottom)
                            llParent.layoutParams = layoutParams
                        }
                        if (llBottom != null) {
                            llBottom.visibility = View.INVISIBLE
                        }
                    } else {
                        btnBottom?.visibility = View.VISIBLE

                        if (llParent != null) {
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            val bottom: Int =
                                (mContext as Activity).resources.getDimensionPixelSize(R.dimen._88sdp);
                            layoutParams.setMargins(0, 0, 0, bottom)
                            llParent.layoutParams = layoutParams
                        }
                        if (llBottom != null) {
                            llBottom.visibility = View.VISIBLE
                        }
                    }

                } catch (e: NullPointerException) {
                }
            })
    }

    fun customToast(mContext: Context, msg: String) {
        val toast = Toast(mContext)
        val inflater: LayoutInflater = (mContext as Activity).layoutInflater
        val layout: View = inflater.inflate(
            R.layout.custom_toast,
            (mContext as Activity).findViewById(R.id.toast_layout_root) as ViewGroup?
        )

        val text = layout.findViewById<View>(R.id.tvCustomToastMessage) as TextView
        text.text = msg

        layout.findViewById<ImageView>(R.id.ivCloseToast).setOnClickListener {
            toast.cancel();
        }
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 16)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()

    }

    class BottomBarListener(mContext: Context) : BottomBarClickListener {
        var mContext = mContext
        override fun onBottomBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(mContext, view!!)

        }
    }

    fun hideKeyboard(mContext: Context, v: View) {
        val inputMethodManager: InputMethodManager? =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    fun openKeyboard(mContext: Context, v: View) {
        val inputMethodManager: InputMethodManager? =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.showSoftInput(
            v, 0
        )
    }

    fun checkPermissionStorageAndCamera(
        activity: Activity,
        permissionsListener: PermissionListener
    ) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    Debug.e("onPermissionsChecked", "" + report.areAllPermissionsGranted())
                    Debug.e("onPermissionsChecked", "" + report.isAnyPermissionPermanentlyDenied)

                    if (report.areAllPermissionsGranted()) {
                        permissionsListener.onGranted()
                    } else {
                        permissionsListener.onDenied()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Debug.e("onPermissionRationale", "" + permissions.size)
                    token.continuePermissionRequest()
                }


            }).check()
    }

    fun showPermissionAlert(mContext: Context) {
        var dialog: androidx.appcompat.app.AlertDialog? = null
        val builder = androidx.appcompat.app.AlertDialog.Builder(
            mContext as BaseActivity,
            R.style.MyAlertDialogStyle
        )
        builder.setTitle(mContext.getString(R.string.need_permission_title))
        builder.setCancelable(false)
        builder.setMessage(mContext.getString(R.string.err_need_permission_msg))
        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            dialog.dismiss()
            mContext.startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
        }
        builder.setNeutralButton(R.string.btn_cancel) { dialog, which ->
            dialog.dismiss()
        }
        dialog = builder.create()
        dialog!!.show()

    }


    var fileUri: File? = null
    val REQ_CAPTURE_IMAGE = 4470
    val REQ_PICK_IMAGE = 4569
    var type = ""


    fun showPictureChooser(mContext: Context) {
        val pd: AlertDialog.Builder =
            AlertDialog.Builder((mContext as Activity), R.style.MyAlertDialogStyle)
        val binding = DataBindingUtil.inflate<DialogPicChooserBinding>(
            LayoutInflater.from(mContext),
            R.layout.dialog_pic_chooser, null, false
        )

        pd.setView(binding.root)
        val dialog = pd.create()
        binding.tvChooseGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                (mContext as Activity).startActivityForResult(
                    Intent.createChooser(
                        intent,
                        mContext.getString(R.string.err_select_image)
                    ), REQ_PICK_IMAGE
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            dialog.dismiss()
        }

        binding.tvChooseCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true)
            fileUri = File(Utils.getOutputMedia(mContext)!!.absolutePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getUriForShare(mContext, fileUri!!))
            try {
                (mContext as Activity).startActivityForResult(
                    Intent.createChooser(
                        intent,
                        mContext!!.getString(R.string.err_select_image)
                    ), REQ_CAPTURE_IMAGE
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            dialog.dismiss()
        }

        dialog.show()

    }

    var userDao: UserDao? = null
    var userDB: UserRoomDatabase? = null
    var mAllUsers: LiveData<List<UserData>>? = null

    fun insert(UserData: UserData?) {
        InsertAsyncTask(userDao)
            .execute(UserData)
    }

    fun getAllUserData(): LiveData<List<UserData>>? {
        return mAllUsers
    }

    fun update(UserData: UserData?) {
        UpdateAsyncTask(userDao)
            .execute(UserData)
    }

    fun delete(UserData: UserData?) {
        DeleteAsyncTask(userDao)
            .execute(UserData)
    }

    open inner class OperationsAsyncTask :
        AsyncTask<UserData?, Void?, Void?> {
        var mAsyncTaskDao: UserDao

        constructor(dao: UserDao?) {
            mAsyncTaskDao = dao!!
        }

        override fun doInBackground(vararg params: UserData?): Void? {
            return null
        }
    }

    inner class InsertAsyncTask :
        OperationsAsyncTask {

        constructor(userDao: UserDao?) : super(userDao) {

        }

        override fun doInBackground(vararg userDatas: UserData?): Void? {
            mAsyncTaskDao.insert(userDatas[0])
            return null
        }
    }

    inner class UpdateAsyncTask :
        OperationsAsyncTask {

        constructor(userDao: UserDao?) : super(userDao) {

        }

        override fun doInBackground(vararg userDatas: UserData?): Void? {
            mAsyncTaskDao.update(userDatas[0])
            return null
        }
    }


    inner class DeleteAsyncTask : OperationsAsyncTask {

        constructor(userDao: UserDao?) : super(userDao) {

        }

        override fun doInBackground(vararg userDatas: UserData?): Void? {
            mAsyncTaskDao.delete(userDatas[0])
            return null
        }
    }

    fun getBlob(photo: Uri?, mContext: Context): ByteArray? {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), photo)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            var blob = stream.toByteArray()
            return blob
        } catch (e: java.lang.Exception) {
            // TODO: handle exception
            Debug.e("error", e.toString())
        }
        return null
    }
//    https://stackoverflow.com/questions/28448923/how-to-convert-camera-uri-to-blob-object-in-phonegap

    fun getImageFromBLOB(mBlob: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(mBlob, 0, mBlob.size)
    }
}
