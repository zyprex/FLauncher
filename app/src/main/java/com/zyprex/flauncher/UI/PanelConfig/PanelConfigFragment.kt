package com.zyprex.flauncher.UI.PanelConfig

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UTIL.DocStr
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.decentTextView
import com.zyprex.flauncher.UTIL.shadowTextView

class PanelConfigFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        displayPrompt("PanelConfig")
        if (activity == null) return null
        val context = activity as Context
        val layout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            orientation = LinearLayout.VERTICAL
        }
        val hScrolllView = HorizontalScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.parseColor("#90000000"))
        }
        val vScrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        val editor = EditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            minimumHeight = 50
            minimumWidth = context.resources.displayMetrics.widthPixels
            inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            isSingleLine = false
        }
        editor.setText(AppIndex.getPanelConfig(activity as MainActivity))
        editor.id = MainActivity.PANEL_CONF_ID
        vScrollView.addView(editor)
        hScrolllView.addView(vScrollView)
        layout.addView(hScrolllView)
        val tv = shadowTextView(context).apply {
            typeface = Typeface.MONOSPACE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0,
                1f
            )
            setTextIsSelectable(true)
            val helpstr = DocStr.get()
            text = helpstr
        }
        layout.addView(tv)
        return layout
    }

    override fun onDestroyView() {
        view?.findViewById<EditText>(MainActivity.PANEL_CONF_ID)?.apply {
            val text = this.text.toString()
            AppIndex.setPanelConfig(this.context, text)
        }
        super.onDestroyView()
    }

    private fun displayPrompt(str: String) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.displayPrompt(str)
        }
    }
}