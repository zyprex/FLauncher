package com.zyprex.flauncher.UI

import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
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
import androidx.fragment.app.Fragment
import com.zyprex.flauncher.UI.AppList.AppListFragment
import com.zyprex.flauncher.UI.AppListConfig.AppListConfigFragment
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.EXT.AppChangeBroadcastReceiver
import com.zyprex.flauncher.EXT.SysBroadcastReceiver
import com.zyprex.flauncher.UI.Panel.PanelFragment
import com.zyprex.flauncher.UI.Panel.PanelView
import com.zyprex.flauncher.UI.PanelConfig.PanelConfigFragment
import com.zyprex.flauncher.UTIL.decentTextView

class MainActivity : AppCompatActivity() {

    companion object {
        const val FRAG_ID = 200
        const val MAIN_LAYOUT_ID = 100
        const val PROMPT_ID = 300
        const val PANEL_ID = 1
        const val APPLIST_ID = 2
        const val PANEL_CONF_ID = 3
        const val APPLIST_CONF_ID = 4

        const val ITEM_MARGIN = 5
        const val ICON_SIZE = 50


    }

    var choosedImage: Uri? = null
    val getImgFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        choosedImage = uri
    }

    private val appChgfilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addAction(Intent.ACTION_LOCALE_CHANGED)
        addDataScheme("package")
    }
    private val appChgReceiver = AppChangeBroadcastReceiver()

    private val sysFilter = IntentFilter().apply {
        addAction(Intent.ACTION_POWER_CONNECTED)
        addAction(Intent.ACTION_POWER_DISCONNECTED)
        addAction(Intent.ACTION_BATTERY_LOW)
        addAction(Intent.ACTION_BATTERY_OKAY)
        addAction(Intent.ACTION_HEADSET_PLUG)
        addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        addAction(Intent.ACTION_DOCK_EVENT)
    }
    private val sysReceiver = SysBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(initView())
        replaceFragment(PanelFragment())

        registerReceiver(appChgReceiver, appChgfilter)
        registerReceiver(sysReceiver, sysFilter)
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
            setTextColor(Color.WHITE)
            setShadowLayer(8f, 0f, 0f, Color.BLACK)
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
        super.onDestroy()
    }

    override fun onBackPressed() {
        replaceFragment(PanelFragment())
        //super.onBackPressed()
    }

    class MyReceiver:BroadcastReceiver() {
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
}