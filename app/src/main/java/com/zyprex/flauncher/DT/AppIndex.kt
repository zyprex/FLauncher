package com.zyprex.flauncher.DT

import android.content.Context
import android.content.Intent
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

        fun addFav(context: Context, app: AppArchive) {
            val str = readFile(context, appFavorFileName)
            if (str.contains(app.toString())) {
                Toast.makeText(context, "already added '${app.label}'", Toast.LENGTH_SHORT).show()
            } else {
                writeFile(context, appFavorFileName, "${app}${str.replace(app.toString(), "")}")
                Toast.makeText(context, "added '${app.label}'", Toast.LENGTH_SHORT).show()
            }
        }
        fun removeFav(context: Context, app: AppArchive) {
            var str = readFile(context, appFavorFileName)
            writeFile(context, appFavorFileName, "${str.replace(app.toString(), "")}")
            Toast.makeText(context, "removed '${app.label}'", Toast.LENGTH_SHORT).show()
        }
        fun renameFav(context: Context, app: AppArchive, newName: String) {
            var str = readFile(context, appFavorFileName)
            var newFav = str.replace(app.toString(), "${app.pkgName}#$newName\n")
            writeFile(context, appFavorFileName, newFav)
            Toast.makeText(context, "rename '${app.label}' to '$newName'", Toast.LENGTH_SHORT).show()
        }
        fun getPanelConfig(context: Context): String {
            return readFile(context, panelFileName)
        }
        fun setPanelConfig(context: Context, str: String) {
            writeFile(context, panelFileName, str)
        }
    }

    private val pm = context.packageManager

    private fun sync() {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = pm.queryIntentActivities(intent, 0)
        archives.clear()
        for (info in activities) {
            archives.add(AppArchive(info.loadLabel(pm).toString(), info.activityInfo.packageName))
        }
        archives.sortBy {
            it.label
        }
    }

    private fun syncFav() {
        val str = readFile(context, appFavorFileName)
        archivesFav.clear()
        for (line in str.split("\n")) {
            if (line.contains("#")) {
                archivesFav.add(parseStringToAppArchive(line))
            }
        }
    }

    private fun parseStringToAppArchive(str: String): AppArchive {
        val list = str.split("#")
        return AppArchive(list[1], list[0])
    }

    fun update() {
        val sb = StringBuilder()
        sync()
        for (info in archives) {
            sb.append("${info.pkgName}#${info.label}\n")
        }
        writeFile(context, appListFileName, sb.toString())
    }

    fun data(): MutableList<AppArchive> {
        if (archives.count() == 0) {
            sync()
        }
        return archives
    }

    fun updateFav() {
        val sb = StringBuilder()
        for (info in archivesFav) {
            sb.append("${info}")
        }
        writeFile(context, appFavorFileName, sb.toString())
    }

    fun dataFav(): MutableList<AppArchive> {
        if (archivesFav.count() == 0) {
            syncFav()
        }
        return archivesFav
    }

    fun dataFavRemove(pos: Int) {
        archivesFav.removeAt(pos)
    }

    fun dataFavSwap(from: Int, to: Int) {
        Collections.swap(archivesFav, from, to)
    }

    fun dataFavRename(app: AppArchive, newName: String) {
        val pos = archivesFav.indexOfFirst { i -> i == app }
        if (pos != -1) {
            archivesFav[pos].label = newName
        }
    }

    fun getCtx() : Context = context
}