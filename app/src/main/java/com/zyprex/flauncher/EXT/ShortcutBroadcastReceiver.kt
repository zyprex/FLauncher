package com.zyprex.flauncher.EXT

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import com.zyprex.flauncher.DT.AppIndex

class ShortcutBroadcastReceiver: BroadcastReceiver() {

    /*
    *  provide filters
    * */
    fun getFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction("com.android.launcher.action.INSTALL_SHORTCUT")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent== null) return
        val label = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)
        // val icon = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON) as Bitmap
        val shortcutIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT) as Intent?
        if (label == null || shortcutIntent == null) return
        val scIntent = shortcutIntent.toUri(0) // serialize the intent
        if (scIntent.contains("android.intent.action.MAIN") &&
            scIntent.contains("android.intent.category.LAUNCHER")) return

        val str = AppIndex.readPanelConfig(context)
        AppIndex.savePanelConfig(context, "$str\n#-*-*- shortcut name: $label -*-*-\nshortcut##${scIntent}\n")
        Toast.makeText(context, "Shortcut '$label' added in panel config, please edit it manually!", Toast.LENGTH_LONG).show()
    }
}