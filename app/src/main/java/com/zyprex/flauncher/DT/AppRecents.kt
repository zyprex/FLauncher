package com.zyprex.flauncher.DT

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import java.util.Date
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zyprex.flauncher.DT.AppInfo
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.dp2px
import com.zyprex.flauncher.UTIL.launchApp

class AppRecents(val context: Context) {

    val list = recentApp()

    private fun recentApp(): MutableList<AppArchive> {
        val list = mutableListOf<AppArchive>()
        val manager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = Date().time
        val halfday = 6*60*60*1000
        val usageStats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - halfday, now)
        if (usageStats.isNullOrEmpty()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return list
        }
        val legalApps = AppIndex(context).data()
        val usages = usageStats
            .filter { info ->
                val pkgName = info.packageName
                ((legalApps.find { app -> app.pkgName == pkgName } != null)
                        and (! pkgName.startsWith("com.zyprex.flauncher")))
            }
            .sortedByDescending { info -> info.lastTimeStamp }
        for (app in usages) {
            val pkgName = app.packageName
            list.add(AppArchive(AppInfo(context).getLabel(pkgName), pkgName))
        }
        return list
    }

    //        private fun timeFmt(timeStamp: Long, fmt: String): String {
//    /*"yyyy-MM-dd HH:mm:ss"*/
//        return SimpleDateFormat(
//            fmt
//        ).format(Date(timeStamp))
//    }

    fun launcher() {
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
                    orientation = LinearLayout.HORIZONTAL
                }
                val appInfo = AppInfo(context)
                for (app in list) {
                    val itemLayout = LinearLayout(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ).apply {
                            leftMargin = dp2px(context, 10)
                            rightMargin = dp2px(context, 10)
                            topMargin = dp2px(context, 10)
                            bottomMargin = dp2px(context, 15)
                        }
                        orientation = LinearLayout.VERTICAL
                    }
                    itemLayout.addView(ImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            dp2px(context, MainActivity.ICON_SIZE),
                            dp2px(context, MainActivity.ICON_SIZE),
                        )
                        setOnClickListener {
                            bottomSheetDialog.dismiss()
                            launchApp(context, app.pkgName)
                        }
                        setImageDrawable(appInfo.getIcon(app.pkgName))
                    })
                    itemLayout.addView(TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            dp2px(context, MainActivity.ICON_SIZE),
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        setPadding(
                            dp2px(context, 0),
                            dp2px(context, 5),
                            dp2px(context, 0),
                            dp2px(context, 10)
                        )
                        setLines(1)
                        ellipsize = TextUtils.TruncateAt.END
                        text = app.label
                    })
                    layout.addView(itemLayout)
                }
                wrapper.addView(layout)
                setContentView(wrapper)
                setCancelable(true)
                setCanceledOnTouchOutside(true)
            }.show()
        }
    }
}