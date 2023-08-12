package com.zyprex.flauncher.UI

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import com.zyprex.flauncher.UI.AppList.AppListFragment
import com.zyprex.flauncher.UI.AppListConfig.AppListConfigFragment
import com.zyprex.flauncher.DT.AppIndex
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

    private val filter = IntentFilter()
    private val mReceiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(initView())
        replaceFragment(PanelFragment())
        filter.apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_LOCALE_CHANGED)
        }
        filter.addDataScheme("package")
        registerReceiver(mReceiver, filter)
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
        unregisterReceiver(mReceiver)
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