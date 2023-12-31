package com.zyprex.flauncher.DT

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import android.widget.Toast
import com.zyprex.flauncher.UTIL.readFile
import com.zyprex.flauncher.UTIL.writeFile
import java.lang.StringBuilder
import java.util.Collections

class AppIndex(val context: Context) {
    companion object {
        const val appListFileName = "apps.txt"
        const val appFavorFileName = "apps_fav.txt"
        const val panelFileName = "panel.txt"

        val archives = mutableListOf<AppArchive>()
        val archivesFav = mutableListOf<AppArchive>()

        fun readAppFav(context: Context): String {
            return readFile(context, appFavorFileName)
        }
        fun saveAppFav(context: Context, str: String) {
            writeFile(context, appFavorFileName, str)
        }

        fun readPanelConfig(context: Context): String {
            return readFile(context, panelFileName)
        }
        fun savePanelConfig(context: Context, str: String) {
            writeFile(context, panelFileName, str)
        }
    }

    private val pm = context.packageManager

    init {
        archives.clear()
        archives.addAll(initArchives())
        archivesFav.clear()
        archivesFav.addAll(initArchivesFav())
    }

    private fun initArchives(): MutableList<AppArchive> {
        val archives = mutableListOf<AppArchive>()
        val str = readFile(context, appListFileName)
        if (str == "") {
            return queryAppArchives()
        }
        for (line in str.split("\n")) {
            if (line.contains("#")) {
                archives.add(parseStringToAppArchive(line))
            }
        }
        return archives
    }

    private fun queryAppArchives(): MutableList<AppArchive> {
        val archives = mutableListOf<AppArchive>()

        /* // Used for Android Version Under LOLLIPOP
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = pm.queryIntentActivities(intent, 0)
        for (info in activities) {
            archives.add(AppArchive(info.loadLabel(pm).toString(), info.activityInfo.packageName))
        }
         */

        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val appInfoList = launcherApps.getActivityList(null, Process.myUserHandle())
        for (appInfo in appInfoList) {
            val pkgName = appInfo.applicationInfo.packageName
            val label = appInfo.label.toString()
            archives.add(AppArchive(label, pkgName))
        }

        archives.sortBy(AppArchive::label)

        // save to file
        val sb = StringBuilder()
        for (info in archives) {
            sb.append("${info.pkgName}#${info.label}\n")
        }
        writeFile(context, appListFileName, sb.toString())
        return archives
    }

    private fun initArchivesFav(): MutableList<AppArchive> {
        val archivesFav = mutableListOf<AppArchive>()
        val str = readFile(context, appFavorFileName)
        if (str != "") {
            for (line in str.split("\n")) {
                if (line.contains("#")) {
                    archivesFav.add(parseStringToAppArchive(line))
                }
            }
            return archivesFav
        }
        return archivesFav
    }

    private fun parseStringToAppArchive(str: String): AppArchive {
        val list = str.split("#")
        return AppArchive(list[1], list[0])
    }

    // app index methods
    fun data(): MutableList<AppArchive> = archives

    fun dataUpdate()  {
        archives.clear()
        archives.addAll(queryAppArchives())
    }

    // fav app index methods
    fun dataFav(): MutableList<AppArchive> = archivesFav

    fun dataFavUpdate() {
        val sb = StringBuilder()
        for (info in archivesFav) {
            sb.append("${info}")
        }
        writeFile(context, appFavorFileName, sb.toString())
    }

    fun dataFavAdd(app: AppArchive) {
        if (archivesFav.contains(app)) {
            Toast.makeText(context, "already added '${app.label}'", Toast.LENGTH_SHORT).show()
        } else {
            archivesFav.add(0, app)
            Toast.makeText(context, "added '${app.label}'", Toast.LENGTH_SHORT).show()
            dataFavUpdate()
        }
    }

    fun dataFavRemove(pos: Int) {
        Toast.makeText(context, "removed '${archivesFav[pos].label}'", Toast.LENGTH_SHORT).show()
        archivesFav.removeAt(pos)
        dataFavUpdate()
    }

    fun dataFavRemoveItem(app: AppArchive) {
        if (archivesFav.remove(app)) {
            Toast.makeText(context, "removed '${app.label}'", Toast.LENGTH_SHORT).show()
            dataFavUpdate()
        } else {
            Toast.makeText(context, "no need remove!", Toast.LENGTH_SHORT).show()
        }
    }

    fun dataFavSwap(from: Int, to: Int) {
        Collections.swap(archivesFav, from, to)
        dataFavUpdate()
    }

    fun dataFavRename(app: AppArchive, newName: String) {
        val pos = archivesFav.indexOfFirst { i -> i == app }
        val oldName = archivesFav[pos].label
        if (pos != -1 && oldName != newName) {
            archivesFav[pos].label = newName
            dataFavUpdate()
            Toast.makeText(context, "rename '$oldName' to '$newName'", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCtx() : Context = context
}