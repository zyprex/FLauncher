package com.zyprex.flauncher.AppListConfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.AppIndex
import com.zyprex.flauncher.MainActivity

class AppListConfigFragment : Fragment() {

    lateinit var appIndex: AppIndex

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        displayPrompt("AppListConfig")
        activity?.let { activity ->
            val appIndex = AppIndex(activity as MainActivity)
            val rv = RecyclerView(activity)
            rv.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            rv.layoutManager = GridLayoutManager(activity, 2)
            rv.adapter = AppListConfigAdapter(appIndex.data())
            rv.id = MainActivity.APPLIST_ID
            rv.isLongClickable = true
            return rv
        }
        return null
    }

    private fun displayPrompt(str: String) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.displayPrompt(str)
        }
    }
}