package com.zyprex.flauncher.UTIL

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

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
        context.startActivity(intent)
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


fun dp2px(context: Context, dp: Int): Int =
    (dp * context.resources.displayMetrics.density).toInt()

fun decentTextView(context: Context): TextView =
    TextView(context).apply {
        setLines(1)
        ellipsize = TextUtils.TruncateAt.END
        textSize = 18f
        gravity = Gravity.CENTER_VERTICAL
        //setPadding(10, 8, 10, 8)
        setPadding(
            dp2px(context, 10),
            dp2px(context, 9),
            dp2px(context, 10),
            dp2px(context, 9),
        )
        setTextColor(Color.WHITE)
        setShadowLayer(2f, 2f, 2f, Color.BLACK)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }
