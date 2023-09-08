package com.zyprex.flauncher.UI.AppListConfig

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.DT.AppArchive
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.copyToClipboard
import com.zyprex.flauncher.UTIL.dp2px
import com.zyprex.flauncher.UTIL.emphasisFirstChar
import com.zyprex.flauncher.UTIL.launchApp
import com.zyprex.flauncher.UTIL.launchAppDetail
import com.zyprex.flauncher.UTIL.openAppInMarket

class AppListConfigAdapter(val apps: MutableList<AppArchive>):
    RecyclerView.Adapter<AppListConfigAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TextView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = 0
                bottomMargin = MainActivity.ITEM_MARGIN
                leftMargin = 0
                rightMargin = MainActivity.ITEM_MARGIN
            }
            textSize = 18f
            setPadding(
                dp2px(context, 10),
                dp2px(context, 10),
                dp2px(context, 10),
                dp2px(context, 10),
            )
            setLines(1)
            ellipsize = TextUtils.TruncateAt.END
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#90000000"))
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
            showAppListConfigMenu(context, tv, apps[position], position)
            true
        }
    }

    override fun getItemCount(): Int = apps.size

    private fun showAppListConfigMenu(context: Context, view: View, app: AppArchive, pos: Int) {
        val appIndex = AppIndex(context)
        PopupMenu(context, view).apply {
            menu.apply {
                add(0, 0, 0, "Copy Package Name")
                add(0, 1, 0, "Add to AppList")
                add(0, 2, 0, "Remove from AppList")
                add(0, 3, 0, "App Info")
                add(0, 4, 0, "Update Apps Data")
                add(0, 5, 0, "Open App Market")
            }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    0 -> {
                        copyToClipboard(context, app.pkgName)
                        true
                    }
                    1 -> {
                        appIndex.dataFavAdd(app)
                        true
                    }
                    2 -> {
                        appIndex.dataFavRemoveItem(app)
                        true
                    }
                    3 -> {
                        launchAppDetail(context, app.pkgName)
                        true
                    }
                    4 -> {
                        appIndex.dataUpdate()
                        notifyDataSetChanged()
                        true
                    }
                    5 -> {
                        openAppInMarket(context, app.pkgName)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}