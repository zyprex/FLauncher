package com.zyprex.flauncher.EXT

import android.accessibilityservice.AccessibilityService
import android.content.pm.ServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService: AccessibilityService() {

    companion object {
        var service: MyAccessibilityService? = null
        fun useAction(action: String) {
            val actionCode = when (action) {
                "notifications" -> GLOBAL_ACTION_NOTIFICATIONS
                "quick_settings" -> GLOBAL_ACTION_QUICK_SETTINGS
                "lock" -> GLOBAL_ACTION_LOCK_SCREEN
                "power" -> GLOBAL_ACTION_POWER_DIALOG
                "recents" -> GLOBAL_ACTION_RECENTS
                else -> -1
            }
            if (actionCode != -1) {
                //Log.d("MAS", service.toString())
                service?.performGlobalAction(actionCode)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        service = this
    }

    override fun onDestroy() {
        super.onDestroy()
        service = null
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

}