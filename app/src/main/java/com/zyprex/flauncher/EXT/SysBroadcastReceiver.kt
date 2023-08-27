package com.zyprex.flauncher.EXT

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.widget.Toast
import androidx.core.content.getSystemService
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UI.Panel.PanelVerdict
import java.util.Date

class SysBroadcastReceiver: BroadcastReceiver() {

    /*
    *  provide filters
    * */
    fun getFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            addAction(Intent.ACTION_DOCK_EVENT)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val verdict = PanelVerdict(context)

        when (intent?.action) {
            // plug usb in or out
            Intent.ACTION_POWER_CONNECTED -> {
                verdict.actionStart("USB_IN")
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                verdict.actionStart("USB_OUT")
            }

            // power low or full
            Intent.ACTION_BATTERY_LOW -> {
                verdict.actionStart("BAT_LOW")
            }

            Intent.ACTION_BATTERY_OKAY -> {
                verdict.actionStart("BAT_OK")
            }

            // headset in or out
            Intent.ACTION_HEADSET_PLUG -> {
                when (intent.getIntExtra("state", -1)) {
                    1 -> {
                        verdict.actionStart("HEADSET_IN")
                    }
                    0 -> {
                        verdict.actionStart("HEADSET_OUT")
                    }
                }
            }
            BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                when (adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        verdict.actionStart("BL_HEADSET_IN")
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        verdict.actionStart("BL_HEADSET_OUT")
                    }
                }
            }

            // dock event, car, desk, low-end(analog) desk, high-end(digital) desk
            Intent.ACTION_DOCK_EVENT -> {
                when(intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1)) {
                    Intent.EXTRA_DOCK_STATE_CAR -> verdict.actionStart("DOCK_CAR")
                    Intent.EXTRA_DOCK_STATE_DESK -> verdict.actionStart("DOCK_DESK")
                    Intent.EXTRA_DOCK_STATE_LE_DESK -> verdict.actionStart("DOCK_LE_DESK")
                    Intent.EXTRA_DOCK_STATE_HE_DESK -> verdict.actionStart("DOCK_HE_DESK")
                }
            }

            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                if (intent.getBooleanExtra("state", false)) {
                    verdict.actionStart("AIRPLANE_MODE_ON")
                } else {
                    verdict.actionStart("AIRPLANE_MODE_OFF")
                }
            }
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                if (!MainActivity.appReady()) return

                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connectivityManager.activeNetworkInfo?.isConnected == true) {
                    //Toast.makeText(context, "network on", Toast.LENGTH_SHORT).show()
                    verdict.actionStart("NETWORK_ON")
                } else if (connectivityManager.activeNetworkInfo?.isConnected == false) {
                    //Toast.makeText(context, "network off", Toast.LENGTH_SHORT).show()
                    verdict.actionStart("NETWORK_OFF")
                }
            }

        }

    }
}