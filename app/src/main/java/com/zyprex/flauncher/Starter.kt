package com.zyprex.flauncher

import android.Manifest
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.reflect.Method
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date

class Starter(val context: Context) {
    fun dialPhoneNum(phoneNum: String) {
        val uri = Uri.parse("tel:${phoneNum}")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        safeStartActiviy(context, intent)
    }

    fun callPhoneNum(phoneNum: String) {
        when {
            (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) -> {
                val uri = Uri.parse("tel:${phoneNum}")
                val intent = Intent(Intent.ACTION_CALL, uri)
                safeStartActiviy(context, intent)
            }
            else -> {
                val mainActivity = context as MainActivity
                mainActivity.reqPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }



    fun sendSMS(number: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${number}"))
        safeStartActiviy(context, intent)
    }

    fun sendMail(address: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${address}"))
        safeStartActiviy(context, intent)
    }

    fun showMap(geoInfo: String) {
        // geoInfo : latitude,longitude
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${geoInfo}"))
        safeStartActiviy(context, intent)
    }

    fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        safeStartActiviy(context, intent)
    }

    fun searchWeb(query: String) {
//    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
//        putExtra(SearchManager.QUERY, query)
//    }
//    safeStartActiviy(context, intent)
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val input = EditText(context)
            AlertDialog.Builder(context).apply {
                setTitle("search web")
                setMessage(query)
                setView(input)
                setNegativeButton("CANCEL"){ dialog,_ ->
                    dialog.dismiss()
                }
                setPositiveButton("OK"){ _,_ ->
                    val result = input.text.toString()
                    openWebPage(query.replace("%s", URLEncoder.encode(result, "UTF-8")))
                }
            }.show()
        }
    }

    fun openSystemSettings(which: String) {
        val setting = when(which) {
            "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            "apn" -> Settings.ACTION_APN_SETTINGS
            "appmgr" -> Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "data" -> Settings.ACTION_DATA_ROAMING_SETTINGS
            "data_usage" -> Settings.ACTION_DATA_USAGE_SETTINGS
            "date" -> Settings.ACTION_DATE_SETTINGS
            "dev" -> Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
            "device_info" -> Settings.ACTION_DEVICE_INFO_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "home" -> Settings.ACTION_HOME_SETTINGS
            "ime" -> Settings.ACTION_INPUT_METHOD_SETTINGS
            "locale" -> Settings.ACTION_APP_LOCALE_SETTINGS
            "nfc" -> Settings.ACTION_NFC_SETTINGS
            "quick_launch" -> Settings.ACTION_QUICK_LAUNCH_SETTINGS
            "security" -> Settings.ACTION_SECURITY_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "storage" -> Settings.ACTION_INTERNAL_STORAGE_SETTINGS
            "user_dict" -> Settings.ACTION_USER_DICTIONARY_SETTINGS
            "voice_input" -> Settings.ACTION_VOICE_INPUT_SETTINGS
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            else -> ""
        }
        if (setting != "") {
            safeStartActiviy(context, Intent(setting))
        }
    }

    var torchState = false
    var camera : Camera? = null
    @Suppress("deprecation")
    fun toggleTorch() {
        if (Build.VERSION.SDK_INT < 23) {
            val pm = context.packageManager
            val featInfo = pm.systemAvailableFeatures
            for (info in featInfo) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(info.name)) {
                    if (camera == null) camera = Camera.open()
                    camera?.apply {
                        val parameters = this.parameters
                        torchState = if (torchState) false else true
                        if (torchState) {
                            parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                            this.parameters = parameters
                            this.startPreview()
                        } else {
                            parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                            this.parameters = parameters
                            this.stopPreview()
                            this.release()
                            camera = null
                        }
                    }
                }
            }
        } else {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val isFlashAvailable =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
            if (!isFlashAvailable) return
            try {
                val cmid = cameraManager.cameraIdList[0]
                torchState = if (torchState) false else true
                cameraManager.setTorchMode(cmid, torchState)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    fun expandStatusBar() {
        val statusBarService = context.getSystemService("statusbar")
        if (statusBarService == null) return
        try {
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            var expand: Method?
            if (Build.VERSION.SDK_INT <= 16) {
                expand = statusBarManager.getMethod("expand")
            }  else {
                expand = statusBarManager.getMethod("expandNotificationsPanel")
//            expand = statusBarManager.getMethod("expandSettingsPanel")
            }
            expand.isAccessible = true
            expand.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setWallpaper() {
        val intentChooser = Intent(Intent.ACTION_SET_WALLPAPER)
        val intent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, intentChooser)
            putExtra(Intent.EXTRA_TITLE, "set wallpaper")
        }
        safeStartActiviy(context, intent)
    }

    fun recentActivities() {
        val manager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = Date().time
        val halfday = 6*60*60*1000
        val usageStats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - halfday, now)
        if (usageStats.isNullOrEmpty()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        }
        val usages = usageStats
            .filter { info ->
                if (info.packageName.startsWith("com.android.systemui") or
                        info.packageName.startsWith("com.zyprex.flauncher"))
                    false else true }
            .sortedByDescending { info -> info.lastTimeStamp }
        for (app in usages) {
            Log.d("Starter", "${app.packageName} ${timeFmt(app.lastTimeStamp)}")
        }
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.apply {
                val wrapper = HorizontalScrollView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
                val layout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    //orientation = LinearLayout.HORIZONTAL
                }
                for (app in usages) {
                    layout.addView(ImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            dp2px(context, MainActivity.ICON_SIZE),
                            dp2px(context, MainActivity.ICON_SIZE),
                        ).apply {
                            leftMargin = dp2px(context, 5)
                            rightMargin = dp2px(context, 5)
                            topMargin = dp2px(context, 10)
                            bottomMargin = dp2px(context, 15)
                        }
                        setOnClickListener {
                            bottomSheetDialog.dismiss()
                            launchApp(context, app.packageName)
                        }
                        setImageDrawable(getAppIcon(context, app.packageName))
                        //text = " ${app.packageName} ${timeFmt(app.lastTimeStamp)}"
                    })
                }
                wrapper.addView(layout)
                setContentView(wrapper)
                setCancelable(true)
                setCanceledOnTouchOutside(true)
            }.show()
        }
    }

    private fun timeFmt(timeStamp: Long): String {
        //"yyyy-MM-dd HH:mm:ss"
        return SimpleDateFormat(
            "HH:mm:ss"
        ).format(Date(timeStamp))
    }
}
