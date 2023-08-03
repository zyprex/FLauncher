package com.zyprex.flauncher

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.BitmapCompat
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URLEncoder



/*
*
*  persistence data store
* */
fun writeFile(context: Context, filename: String, str: String) {
    try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            BufferedWriter(OutputStreamWriter(fos)).use {
                it.write(str)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun appendLineToFile(context: Context, filename: String, str: String) {
    val content = readFile(context, filename)
    try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            BufferedWriter(OutputStreamWriter(fos)).use {
                if (content.length == 0) {
                    it.write("$str\n")
                } else if (content[content.length - 1] != '\n') {
                    it.write("$content\n$str\n")
                } else {
                    it.write("$content$str\n")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun readFile(context: Context, filename: String): String {
    var str = ""
    try {
        context.openFileInput(filename).use { fis ->
            val br = BufferedReader(InputStreamReader(fis))
            str = br.readText()
            br.close()
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return str
}


/*
*
* actions and help functions
* */

fun copyToClipboard(context: Context, str: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("text", str)
    clipboard.setPrimaryClip(clip)
}


fun safeStartActiviy(context: Context, intent: Intent) {
    if (intent.resolveActivity(context.packageManager) != null) {
        try {
            context.startActivity(intent)
        } catch (e: NullPointerException) {
        }
    }
}

fun launchApp(context: Context, pkgName: String) {
    //Toast.makeText(context, pkgName, Toast.LENGTH_SHORT).show()
    val launchIntent = context.packageManager.getLaunchIntentForPackage(pkgName)
    if (launchIntent != null) {
        safeStartActiviy(context, launchIntent)
    }
}

fun launchAppDetail(context: Context, pkgName: String) {
    val intent = Intent().apply {
        setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        setData(Uri.fromParts("package", pkgName, null))
    }
    safeStartActiviy(context, intent)
}

fun dialPhoneNum(context: Context, phoneNum: String) {
    val uri = Uri.parse("tel:${phoneNum}")
    val intent = Intent(Intent.ACTION_DIAL, uri)
    safeStartActiviy(context, intent)
}

fun callPhoneNum(context: Context, phoneNum: String) {
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



fun sendSMS(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${number}"))
    safeStartActiviy(context, intent)
}

fun sendMail(context: Context, address: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${address}"))
    safeStartActiviy(context, intent)
}

fun showMap(context: Context, geoInfo: String) {
    // geoInfo : latitude,longitude
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${geoInfo}"))
    safeStartActiviy(context, intent)
}

fun openWebPage(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    safeStartActiviy(context, intent)
}

fun searchWeb(context: Context, query: String) {
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
                openWebPage(context,
                    query.replace("%s", URLEncoder.encode(result, "UTF-8")))
            }
        }.show()
    }
}

fun openSystemSettings(context: Context, which: String) {
    val setting = when(which) {
        "locale" -> Settings.ACTION_APP_LOCALE_SETTINGS
        "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
        "deviceinfo" -> Settings.ACTION_DEVICE_INFO_SETTINGS
        "display" -> Settings.ACTION_DISPLAY_SETTINGS
        "security" -> Settings.ACTION_SECURITY_SETTINGS
        "wifi" -> Settings.ACTION_WIFI_SETTINGS
        "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
        "date" -> Settings.ACTION_DATE_SETTINGS
        "voiceinput" -> Settings.ACTION_VOICE_INPUT_SETTINGS
        "ime" -> Settings.ACTION_INPUT_METHOD_SETTINGS
        "usrdict" -> Settings.ACTION_USER_DICTIONARY_SETTINGS
        "dev" -> Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
        "storage" -> Settings.ACTION_INTERNAL_STORAGE_SETTINGS
        "home" -> Settings.ACTION_HOME_SETTINGS
        "appmgr" -> Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
        "nfc" -> Settings.ACTION_NFC_SETTINGS
        "quicklaunch" -> Settings.ACTION_QUICK_LAUNCH_SETTINGS
        else -> ""
    }
    if (setting != "") {
        safeStartActiviy(context, Intent(setting))
    }
}

var torchState = false
var camera : Camera? = null

fun toggleTorch(context: Context) {
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

/*
*
*  common ui tool
* */
fun charToColor(char: Char): Int {
    val list = listOf<String>(
        "#C62828", "#43A047",
        "#FB8C00", "#80DEEA",
        "#FFEB3B", "#1E88E5",
        "#F48FB1", "#AB47BC",
    )
    val charNum = char.code - 65
    if (charNum >= 0) {
        return Color.parseColor(list[charNum % list.count()])
    }
    return Color.parseColor("#878787")
}

fun getAppIcon(context: Context, pkgName: String): Drawable {
    try {
        val pm = context.packageManager
        val info = pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
        return info.loadIcon(pm)
    } catch (e: Exception) {
        return ShapeDrawable()
    }
}


fun decentTextView(context: Context): TextView =
    TextView(context).apply {
        setLines(1)
        height = 100
        ellipsize = TextUtils.TruncateAt.END
        textSize = 18f
        gravity = Gravity.CENTER_VERTICAL
        setPadding(10, 8, 10, 8)
        setTextColor(Color.WHITE)
        setShadowLayer(2f, 2f, 2f, Color.BLACK)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }
