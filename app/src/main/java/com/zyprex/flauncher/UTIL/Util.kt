package com.zyprex.flauncher.UTIL

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
    Toast.makeText(context, "copied '$str'", Toast.LENGTH_SHORT).show()
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

fun openAppInMarket(context: Context, pkgName: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${pkgName}"))
    safeStartActiviy(context, intent)
}


/*
*
*  common ui tool
* */
fun charToColor(char: Char): Int {
    val list = listOf<String>(
        "#EF5350",
        "#26C6DA",
        "#FFEE58",
        "#66BB6A",
        "#EC407A",
        "#18897F",
        "#FFA726",
        "#AB47BC",
    )
    val charNum = char.code - 65
    if (charNum >= 0) {
        return Color.parseColor(list[charNum % list.count()])
    }
    return Color.parseColor("#878787")
}

fun emphasisFirstChar(text : String): SpannableString {
    val ss = SpannableString(text).apply {
        //setSpan(RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        setSpan(StyleSpan(Typeface.BOLD), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        setSpan(ForegroundColorSpan(charToColor(text[0])), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return ss
}


fun dp2px(context: Context, dp: Int): Int =
    (dp * context.resources.displayMetrics.density).toInt()

fun dp2px(context: Context, dp: Float): Float =
    (dp * context.resources.displayMetrics.density)

fun shadowTextView(context: Context): TextView =
    TextView(context).apply {
        textSize = 18f
        setPadding(
            dp2px(context, 2),
            dp2px(context, 2),
            dp2px(context, 2),
            dp2px(context, 2),
        )
        setShadowLayer(2f, 2f, 2f, Color.BLACK)
        setTextColor(Color.WHITE)
    }

fun decentTextView(context: Context): TextView =
    //TextView(context).apply {
    StrokeTextView(context).apply {
        setLines(1)
        ellipsize = TextUtils.TruncateAt.END
        textSize = 18f
        gravity = Gravity.CENTER_VERTICAL
        setPadding(
            dp2px(context, 10),
            dp2px(context, 9),
            dp2px(context, 10),
            dp2px(context, 9),
        )
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        setTextColor(Color.WHITE)
    }
