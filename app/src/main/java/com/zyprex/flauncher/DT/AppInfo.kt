package com.zyprex.flauncher.DT

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class AppInfo(val context: Context) {
    private val pm = context.packageManager

    fun getIcon(pkgName: String): Drawable =
        pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
            .loadIcon(pm)

    fun getLabel(pkgName: String): String =
        pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
            .loadLabel(pm).toString()

}