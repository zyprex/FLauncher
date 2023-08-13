package com.zyprex.flauncher.EXT

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zyprex.flauncher.DT.AppIndex

class AppChangeBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED,
            Intent.ACTION_LOCALE_CHANGED -> {
                if (context != null) {
                    AppIndex(context).update()
                }
            }
        }
    }
}