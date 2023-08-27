package com.zyprex.flauncher.EXT

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.widget.Toast
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UI.Panel.PanelVerdict
import java.util.Date
import kotlin.coroutines.coroutineContext

class NetworkListener(context: Context): ConnectivityManager.NetworkCallback() {
    val verdict = PanelVerdict(context)
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        if (!MainActivity.appReady()) return
        verdict.actionStart("NETWORK_ON")
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        if (!MainActivity.appReady()) return
        verdict.actionStart("NETWORK_OFF")
    }
}