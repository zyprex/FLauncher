package com.zyprex.flauncher.Panel

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.zyprex.flauncher.AppIndex
import com.zyprex.flauncher.MainActivity
import com.zyprex.flauncher.appendLineToFile


class PanelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity == null) {
            return null
        }
        displayPrompt("Panel")
        val panel = PanelView(activity as MainActivity)
        panel.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        //panel.setBackgroundColor(Color.parseColor("#40000000"))
        panel.id = MainActivity.PANEL_ID
        return panel
    }

    private fun displayPrompt(str: String) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.displayPrompt(str)
        }
    }
}