package com.zyprex.flauncher.PanelConfig

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.zyprex.flauncher.AppIndex
import com.zyprex.flauncher.MainActivity

class PanelConfigFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        displayPrompt("PanelConfig")
        val editor = EditText(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isSingleLine = false
        }

        editor.setBackgroundColor(Color.parseColor("#90000000"))
        if (activity!= null) {
            editor.setText(AppIndex.getPanelConfig(activity as MainActivity))
        }
        editor.id = MainActivity.PANEL_CONF_ID
        return editor
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