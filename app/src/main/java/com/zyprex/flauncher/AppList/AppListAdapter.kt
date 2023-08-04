package com.zyprex.flauncher.AppList

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Process
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.AppArchive
import com.zyprex.flauncher.decentTextView
import com.zyprex.flauncher.getAppIcon
import com.zyprex.flauncher.launchApp
import com.zyprex.flauncher.launchAppDetail

class AppListAdapter(val apps: MutableList<AppArchive>):
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    companion object {
        const val ITEM_IMG_ID = 0
        const val ITEM_TXT_ID = 1
    }

    lateinit var launcherApps: LauncherApps

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        launcherApps = parent.context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        val layout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#40000000"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                bottomMargin = 5
            }
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(10)
        }
        val iconSize = (50 * parent.context.resources.displayMetrics.density).toInt()
        val iv = ImageView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                iconSize,
                0.2f,
            )
        }
        iv.id = ITEM_IMG_ID
        layout.addView(iv)
        val tv = decentTextView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.8f,
            )
        }
        tv.id = ITEM_TXT_ID
        layout.addView(tv)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val iv = holder.itemView.findViewById(ITEM_IMG_ID) as ImageView
        val tv = holder.itemView.findViewById(ITEM_TXT_ID) as TextView
        val drawable = getAppIcon(context, apps[position].pkgName)

        iv.setImageDrawable(drawable)

        iv.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                showAppListMenu(context, it, apps[position])
            } else {
                launchAppDetail(context, apps[position].pkgName)
            }
        }
        tv.text = apps[position].label
        holder.itemView.setOnClickListener {
            launchApp(context, apps[position].pkgName)
        }
    }
    override fun getItemCount(): Int = apps.size

    private fun showAppListMenu(context: Context, view: View, app: AppArchive) {
        val shortcutQuery = LauncherApps.ShortcutQuery().apply {
            setQueryFlags(ShortcutQuery.FLAG_MATCH_MANIFEST or
                    ShortcutQuery.FLAG_MATCH_PINNED or
                    ShortcutQuery.FLAG_MATCH_DYNAMIC)
            setPackage(app.pkgName)
        }
        val shortcuts = launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle())
        if (shortcuts == null || shortcuts.isEmpty()) {
            launchAppDetail(context, app.pkgName)
            return
        }
        var idx = 1
        PopupMenu(context, view).apply {
            menu.add(1, 0, 0, "App Info")
            for (shortcut in shortcuts) {
                menu.add(1, idx, idx, shortcut.shortLabel)
                idx++
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> {
                        launchAppDetail(context, app.pkgName)
                    }
                    else -> {
                        val shortcut = shortcuts[it.itemId - 1]
                        launcherApps.startShortcut(
                            app.pkgName,
                            shortcut.id,
                            null,
                            null,
                            Process.myUserHandle()
                        )
                    }
                }
                true
            }
        }.show()
    }
}