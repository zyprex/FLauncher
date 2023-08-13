package com.zyprex.flauncher.EXT

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.zyprex.flauncher.R

class ScrLock(val context: Context) {
    fun lock() {
      val component = ComponentName(context, DeviceAdminReceiver::class.java)
      val policyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (!policyManager.isAdminActive(component)) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
          putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
          putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
              context.getResources().getString(R.string.app_name))
        }
        context.startActivity(intent)
      } else policyManager.lockNow()
    }
}
