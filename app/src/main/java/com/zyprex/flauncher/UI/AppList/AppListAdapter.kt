package com.zyprex.flauncher.UI.AppList

import android.app.AlertDialog
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery
import android.content.pm.ShortcutInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.DT.AppArchive
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.DT.AppInfo
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UI.Panel.PanelVerdict
import com.zyprex.flauncher.UTIL.decentTextView
import com.zyprex.flauncher.UTIL.dp2px
import com.zyprex.flauncher.UTIL.launchApp
import com.zyprex.flauncher.UTIL.launchAppDetail
import java.io.File

class AppListAdapter(val apps: MutableList<AppArchive>):
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    companion object {
        const val ITEM_IMG_ID = 0
        const val ITEM_TXT_ID = 1
    }

    lateinit var launcherApps: LauncherApps

    lateinit var panelVerdict: PanelVerdict

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context

        launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        panelVerdict = PanelVerdict(context)

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            //setBackgroundColor(Color.parseColor("#40000000"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                bottomMargin = MainActivity.ITEM_MARGIN
            }
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp2px(context, 10))
        }
        val iv = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                dp2px(parent.context, MainActivity.ICON_SIZE),
                0.2f,
            )
        }
        iv.id = ITEM_IMG_ID
        layout.addView(iv)
        val tv = decentTextView(context).apply {
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
        val drawable = AppInfo(context).getIcon(apps[position].pkgName)

        iv.setImageDrawable(drawable)

        iv.setOnClickListener {
            showAppListMenu(context, it, apps[position], position)
        }
        tv.text = apps[position].label
        holder.itemView.setOnClickListener {
            if (panelVerdict.runNumStrOpCode(apps[position].pkgName) == false) {
                launchApp(context, apps[position].pkgName)
            }
        }
    }
    override fun getItemCount(): Int = apps.size

    private fun showAppListMenu(context: Context, view: View, app: AppArchive, position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            showAppListMenuWithShortcuts(context, view, app, position)
            return
        }
        PopupMenu(context, view).apply {
            menu.add(0, 0, 0, "App Info")
            menu.addSubMenu(1, 1, 0, "Item Display").apply {
                add(1, 101, 0, "Set Image Icon")
                add(1, 102, 0, "Reset Icon")
                add(1, 103, 0, "Rename App")
                add(1, 104, 0, "Edit App List")
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> launchAppDetail(context, app.pkgName)
                    101 -> setIconFromImg(context, app.pkgName, position)
                    102 -> deleteImageIcon(context, app.pkgName, position)
                    103 -> renameApp(context, app, position)
                    104 -> editAppList(context)
                }
                true
            }
        }.show()
    }

    private fun showAppListMenuWithShortcuts(context: Context, view: View, app: AppArchive, position: Int) {
        val staticShortcutQuery = LauncherApps.ShortcutQuery().apply {
            setQueryFlags(ShortcutQuery.FLAG_MATCH_MANIFEST)
            setPackage(app.pkgName)
        }
        val dynamicShortcutQuery = LauncherApps.ShortcutQuery().apply {
            setQueryFlags(ShortcutQuery.FLAG_MATCH_DYNAMIC)
            setPackage(app.pkgName)
        }
        val pinShortcutQuery = LauncherApps.ShortcutQuery().apply {
            setQueryFlags(ShortcutQuery.FLAG_MATCH_PINNED)
            setPackage(app.pkgName)
        }
        var staticShortcuts = mutableListOf<ShortcutInfo>()
        var dynamicShortcuts = mutableListOf<ShortcutInfo>()
        var pinShortcuts = mutableListOf<ShortcutInfo>()
        var allShortcuts = mutableListOf<ShortcutInfo>()
        try {
            staticShortcuts = launcherApps.getShortcuts(staticShortcutQuery, Process.myUserHandle()) as MutableList<ShortcutInfo>
            dynamicShortcuts = launcherApps.getShortcuts(dynamicShortcutQuery, Process.myUserHandle()) as MutableList<ShortcutInfo>
            pinShortcuts = launcherApps.getShortcuts(pinShortcutQuery, Process.myUserHandle()) as MutableList<ShortcutInfo>
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        var idx = 900
        PopupMenu(context, view).apply {
            menu.add(0, 0, 0, "App Info")
            menu.addSubMenu(1, 1, 0, "Item Display").apply {
                add(1, 101, 0, "Set Image Icon")
                add(1, 102, 0, "Reset Icon")
                add(1, 103, 0, "Rename App")
                add(1, 104, 0, "Edit App List")
            }
            if (staticShortcuts.isNotEmpty()) {
                for (shortcut in staticShortcuts) {
                    menu.add(2, idx, 0, shortcut.shortLabel)
                    allShortcuts.add(shortcut)
                    idx++
                }
            }
            if (dynamicShortcuts.isNotEmpty()) {
                for (shortcut in dynamicShortcuts) {
                    menu.add(2, idx, 0, shortcut.shortLabel)
                    allShortcuts.add(shortcut)
                    idx++
                }
            }
            if (pinShortcuts.isNotEmpty()) {
                for (shortcut in pinShortcuts) {
                    menu.add(2, idx, 0, shortcut.shortLabel)
                    allShortcuts.add(shortcut)
                    idx++
                }
                menu.add(2, 2, 0, "[UNPIN ALL SHORTCUTS]")
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> launchAppDetail(context, app.pkgName)
                    101 -> setIconFromImg(context, app.pkgName, position)
                    102 -> deleteImageIcon(context, app.pkgName, position)
                    103 -> renameApp(context, app, position)
                    104 -> editAppList(context)
                    2 -> unpinAllShortcuts(app)
                    else -> {
                        if (allShortcuts.isNotEmpty()
                            && it.itemId >= 900) {
                            val shortcut = allShortcuts[it.itemId - 900]
                            launcherApps.startShortcut(
                                app.pkgName,
                                shortcut.id,
                                null,
                                null,
                                Process.myUserHandle()
                            )
                        }
                    }
                }
                true
            }
        }.show()
    }

    private fun setIconFromImg(context: Context, pkgName: String, position: Int) {
        val mainActivity = context as MainActivity
        if ("$pkgName".contains("/")) {
            return
        }
        mainActivity.changedImageName = "$pkgName.png"
        mainActivity.changedImagePos = position
        mainActivity.getImgFile.launch("image/png")
        //notifyItemChanged(position)
    }

    private fun deleteImageIcon(context: Context, pkgName: String, position: Int) {
        File(context.filesDir, "$pkgName.png").delete()
        notifyItemChanged(position)
    }

    private fun renameApp(context: Context, app: AppArchive, position: Int) {
        val appIndex = AppIndex(context)
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val input = EditText(context).apply {
                setText(app.label)
            }
            AlertDialog.Builder(context).apply {
                setTitle("Rename App")
                setView(input)
                setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton("OK") { _, _ ->
                    val newName = input.text.toString()
                    appIndex.dataFavRename(app, newName)
                    notifyItemChanged(position)
                }
            }.show()
        }
    }

    private fun unpinAllShortcuts(app: AppArchive) {
        launcherApps.pinShortcuts(app.pkgName, listOf(), Process.myUserHandle())
    }

    private fun editAppList(context: Context) {
        val input = EditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setText(AppIndex.readAppFav(context))
            maxLines = 10
        }
        val hScrolllView = HorizontalScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }
        hScrolllView.addView(input)
        AlertDialog.Builder(context).apply {
            setTitle("Edit App List")
            setMessage("Tip: format `{num_str}#{label}` run as opcode!")
            setView(hScrolllView)
            setNegativeButton("CANCEL"){ dialog,_ ->
                dialog.dismiss()
            }
            setPositiveButton("OK"){ _,_ ->
                val result = input.text.toString()
                AppIndex.saveAppFav(context, result)
            }
        }.show()
    }
}