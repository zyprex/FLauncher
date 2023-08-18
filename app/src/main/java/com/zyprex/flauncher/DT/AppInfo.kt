package com.zyprex.flauncher.DT

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class AppInfo(val context: Context) {
    private val pm = context.packageManager

    fun getIcon(pkgName: String): Drawable {
        try {
            context.openFileInput("$pkgName.png").use {fis ->
                val bitmap = BitmapFactory.decodeStream(fis)
                return BitmapDrawable(context.resources, bitmap)
            }
        } catch (e: FileNotFoundException) {
            return pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
                .loadIcon(pm)
        }
    }


    fun getLabel(pkgName: String): String =
        pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
            .loadLabel(pm).toString()

}