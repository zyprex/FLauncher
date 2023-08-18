package com.zyprex.flauncher.UI.AppListConfig

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.GestureDetector
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.DT.AppArchive
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.DT.AppInfo
import com.zyprex.flauncher.R
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.charToColor
import com.zyprex.flauncher.UTIL.copyToClipboard
import com.zyprex.flauncher.UTIL.decentTextView
import com.zyprex.flauncher.UTIL.launchApp
import com.zyprex.flauncher.UTIL.launchAppDetail
import kotlin.math.abs

class AppListConfigAdapter(val apps: MutableList<AppArchive>):
    RecyclerView.Adapter<AppListConfigAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = decentTextView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = 0
                bottomMargin = MainActivity.ITEM_MARGIN
                leftMargin = 0
                rightMargin = MainActivity.ITEM_MARGIN
            }
            setBackgroundColor(Color.parseColor("#40000000"))
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val tv = holder.itemView as TextView
        val appName = apps[position].label
        if (position == 0 ||
             position > 0 && apps[position-1].label[0] != appName[0]) {
            tv.text = emphasisFirstChar(appName)
        } else {
            tv.text = appName
        }
        tv.setOnClickListener {
            launchApp(context, apps[position].pkgName)
        }
        tv.setOnLongClickListener {
            showAppListConfigMenu(context, tv, apps[position])
            true
        }
    }

    override fun getItemCount(): Int = apps.size

    private fun emphasisFirstChar(text : String): SpannableString {
        val ss = SpannableString(text).apply {
            //setSpan(RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(charToColor(text[0])), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        return ss
    }

    private fun showAppListConfigMenu(context: Context, view: View, app: AppArchive) {
        PopupMenu(context, view).apply {
            menu.apply {
                add(0, 0, 0, "Copy Package Name")
                add(0, 1, 1, "Add to AppList")
                add(0, 2, 2, "Remove from AppList")
                add(0, 3, 3, "App Info")
            }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    0 -> {
                        copyToClipboard(context, app.pkgName)
                        true
                    }
                    1 -> {
                        AppIndex.addFav(context, app)
                        true
                    }
                    2 -> {
                        AppIndex.removeFav(context, app)
                        true
                    }
                    3 -> {
                        launchAppDetail(context, app.pkgName)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}