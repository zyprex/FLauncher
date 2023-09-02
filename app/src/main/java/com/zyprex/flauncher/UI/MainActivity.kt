package com.zyprex.flauncher.UI

import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.zyprex.flauncher.UI.AppList.AppListFragment
import com.zyprex.flauncher.UI.AppListConfig.AppListConfigFragment
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.EXT.AppChangeBroadcastReceiver
import com.zyprex.flauncher.EXT.NetworkListener
import com.zyprex.flauncher.EXT.ShortcutBroadcastReceiver
import com.zyprex.flauncher.EXT.SysBroadcastReceiver
import com.zyprex.flauncher.UI.Panel.PanelFragment
import com.zyprex.flauncher.UI.Panel.PanelVerdict
import com.zyprex.flauncher.UI.Panel.PanelView
import com.zyprex.flauncher.UI.PanelConfig.PanelConfigFragment
import com.zyprex.flauncher.UTIL.decentTextView
import java.util.Date

class MainActivity : AppCompatActivity() {

    companion object {
        const val FRAG_ID = 200
        const val MAIN_LAYOUT_ID = 100
        const val PROMPT_ID = 300
        const val PANEL_ID = 1
        const val APPLIST_ID = 2
        const val PANEL_CONF_ID = 3
        const val APPLIST_CONF_ID = 4
        const val CPOS_ID = 5

        const val ITEM_MARGIN = 5
        const val ICON_SIZE = 50

        var appReadyTime = 0L
        /* app start >3s */
        fun appReady(): Boolean = (Date().time - MainActivity.appReadyTime > 3000)

    }

    var changedImageName: String = ""
    var changedImagePos: Int = -1
    val getImgFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        // save png
        contentResolver.openInputStream(uri).use {input ->
            openFileOutput(changedImageName, Context.MODE_PRIVATE).use { fos ->
                val bitmap = BitmapFactory.decodeStream(input)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        }
        val appListFragment = supportFragmentManager.findFragmentById(FRAG_ID) as AppListFragment
        appListFragment.adapter.notifyItemChanged(changedImagePos)
    }

    private val appChgReceiver = AppChangeBroadcastReceiver()
    private val sysReceiver = SysBroadcastReceiver()

    private val shortcutBroadcastReceiver = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        ShortcutBroadcastReceiver()
    } else {
        null
    }

    private lateinit var networkCallback : ConnectivityManager.NetworkCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(initView())
        replaceFragment(PanelFragment())

        /* register receivers */
        registerReceiver(appChgReceiver, appChgReceiver.getFilter())
        registerReceiver(sysReceiver, sysReceiver.getFilter())
        if (shortcutBroadcastReceiver != null) {
            registerReceiver(shortcutBroadcastReceiver, shortcutBroadcastReceiver.getFilter())
        }

        appReadyTime = Date().time

        registerNetworkChangeListener()

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val trans = fragmentManager.beginTransaction()
        trans.replace(FRAG_ID, fragment)
        trans.commit()
    }

    private fun initView(): View {
        val linearLayout = LinearLayout(this)
        linearLayout.id = MAIN_LAYOUT_ID
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(initViewFrag())
        linearLayout.addView(initViewPrompt())
        return linearLayout
    }

    private fun initViewPrompt(): TextView {
        val prompt = decentTextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            gravity = Gravity.CENTER
            textSize = 20f
        }
        prompt.setOnClickListener {
            when (prompt.text){
                "Panel" -> replaceFragment(AppListFragment())
                "AppList" -> replaceFragment(PanelFragment())
                "PanelConfig" -> replaceFragment(PanelFragment())
                "AppListConfig" -> replaceFragment(AppListFragment())
                else -> {
                    findViewById<PanelView>(PANEL_ID).instantRun()
                }
            }
        }
        prompt.setOnLongClickListener {
            when (prompt.text) {
                "Panel" -> replaceFragment(PanelConfigFragment())
                "AppList" -> replaceFragment(AppListConfigFragment())
                "PanelConfig" -> replaceFragment(AppListConfigFragment())
                "AppListConfig" -> replaceFragment(PanelConfigFragment())
            }
            true
        }
        prompt.id = PROMPT_ID
        return prompt
    }

    private fun initViewFrag(): View {
        val frag = LinearLayout(this)
        frag.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        )
        frag.id = FRAG_ID
        return frag
    }

    fun displayPrompt(text: String) {
        val prompt = findViewById<TextView>(PROMPT_ID)
        prompt.text = text
    }

    override fun onDestroy() {
        unregisterReceiver(appChgReceiver)
        unregisterReceiver(sysReceiver)
        if (shortcutBroadcastReceiver != null) {
            unregisterReceiver(shortcutBroadcastReceiver)
        }
        unregiserNetworkChangeListener()
        super.onDestroy()
    }

    override fun onBackPressed() {
        replaceFragment(PanelFragment())
        //super.onBackPressed()
    }

    val reqPermissionLauncher =
        registerForActivityResult(RequestPermission()) {
            isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "You granted permission, Try again!",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "You denied permission.",
                    Toast.LENGTH_SHORT).show()
            }
        }

    private fun registerNetworkChangeListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        networkCallback = NetworkListener(this)
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unregiserNetworkChangeListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}