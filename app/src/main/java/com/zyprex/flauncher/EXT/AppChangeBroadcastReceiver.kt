package com.zyprex.flauncher.EXT

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.AppListConfig.AppListConfigFragment
import com.zyprex.flauncher.UI.MainActivity

class AppChangeBroadcastReceiver: BroadcastReceiver() {

    /*
    *  provide filters
    * */
    fun getFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                if (context != null) {
                    AppIndex(context).update()
                    val mainActivity = context as MainActivity
                    val appListConfigFragment = mainActivity.supportFragmentManager.findFragmentById(MainActivity.FRAG_ID) as AppListConfigFragment
                    appListConfigFragment.adapter.notifyDataSetChanged()
                }
            }
        }
    }
}