package com.zyprex.flauncher.AppListConfig

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.AppArchive
import com.zyprex.flauncher.AppIndex
import com.zyprex.flauncher.charToColor
import com.zyprex.flauncher.copyToClipboard
import com.zyprex.flauncher.decentTextView
import com.zyprex.flauncher.launchApp
import com.zyprex.flauncher.launchAppDetail

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
                bottomMargin = 1
                leftMargin = 0
                rightMargin = 1
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
            tv.text = enlargeFirstChar(appName)
        } else {
            tv.text = appName
        }
        tv.setOnClickListener {
            launchApp(context, apps[position].pkgName)
        }
        tv.setOnLongClickListener {
            showMenu(context, tv, apps[position])
            true
        }
        if (position % 2 != 0) {
            tv.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = 0
                bottomMargin = 1
                leftMargin = 0
                rightMargin = 0
            }
        }
    }
    override fun getItemCount(): Int = apps.size

    private fun enlargeFirstChar(text : String): SpannableString {
        val ss = SpannableString(text).apply {
            //setSpan(RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(charToColor(text[0])), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        return ss
    }

    private fun showMenu(context: Context, view: View, app: AppArchive) {
        PopupMenu(context, view).apply {
            menu.apply {
                add(0, 0, 0, "copy package name")
                add(0, 1, 1, "add  to favor app")
                add(0, 2, 2, "remove from favor app")
                add(0, 3, 3, "app info")
            }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    0 -> {
                        copyToClipboard(context, app.pkgName)
                        true
                    }
                    1 -> {
                        AppIndex.addFav(context, app)
                        Toast.makeText(context, "added ${app.label}", Toast.LENGTH_SHORT).show()
                        true
                    }
                    2 -> {
                        AppIndex.removeFav(context, app)
                        Toast.makeText(context, "removed ${app.label}", Toast.LENGTH_SHORT).show()
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