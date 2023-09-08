package com.zyprex.flauncher.EXT

import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.safeStartActiviy


class ShortcutConfirmActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val pinReq = intent.getParcelableExtra(LauncherApps.EXTRA_PIN_ITEM_REQUEST) as LauncherApps.PinItemRequest?
        if (pinReq == null || !pinReq.accept() || pinReq.shortcutInfo == null) return
        val scInfo = pinReq.shortcutInfo as ShortcutInfo
        val label = scInfo.shortLabel
        val pkgName = scInfo.`package`
        val id = scInfo.id

        /* // get shortcut icon
        val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        if (launcherApps.hasShortcutHostPermission()) {
            launcherApps.getShortcutIconDrawable(scInfo, 0)
        }
         */

        val str = AppIndex.readPanelConfig(this)
        AppIndex.savePanelConfig(this, "$str\n#-*-*- shortcut name: $label -*-*-\nshortcut##$pkgName/$id\n")
        Toast.makeText(this, "Shortcut '$label' added in panel config, please edit it manually!", Toast.LENGTH_SHORT).show()

        val homeIntent = Intent()
        homeIntent.setClass(this, MainActivity::class.java)
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        safeStartActiviy(this, homeIntent)
    }
}