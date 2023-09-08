package com.zyprex.flauncher.DT

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
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
            try {
                return pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA).loadIcon(pm)
            } catch (e: NameNotFoundException) {
                return ShapeDrawable()
            }
        } catch (e: IllegalArgumentException) {
            return ShapeDrawable()
        }
    }


    fun getLabel(pkgName: String): String {
        try {
         return pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
                .loadLabel(pm).toString()
        } catch (e: NameNotFoundException) {
            return ""
        }
    }

}