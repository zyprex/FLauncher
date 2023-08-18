package com.zyprex.flauncher.UI.AppListConfig

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.MainActivity
import kotlin.math.abs

class AppListConfigFragment : Fragment() {

    lateinit var appIndex: AppIndex
    private lateinit var adapter: AppListConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        displayPrompt("AppListConfig")
        activity?.let { activity ->
            val screenWidth = activity.resources.displayMetrics.widthPixels
            val appIndex = AppIndex(activity as MainActivity)
            val rv = RecyclerView(activity)
            rv.layoutParams = LinearLayout.LayoutParams(
                screenWidth + MainActivity.ITEM_MARGIN,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            rv.layoutManager = GridLayoutManager(activity, 2)
            adapter = AppListConfigAdapter(appIndex.data())
            rv.adapter = adapter
            val touchCallBack = MyItemTouchCallBack(adapter, appIndex)
            val itemTouchHelper = ItemTouchHelper(touchCallBack)
            itemTouchHelper.attachToRecyclerView(rv)

            rv.id = MainActivity.APPLIST_ID
            //rv.isLongClickable = true
            return rv
        }
        return null
    }

    class MyItemTouchCallBack(val adapter: AppListConfigAdapter, val appIndex: AppIndex)
        : ItemTouchHelper.Callback() {
        override fun isItemViewSwipeEnabled(): Boolean = true
        override fun isLongPressDragEnabled(): Boolean = false
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos =viewHolder.adapterPosition
            val app = appIndex.data()[pos]
            val context = appIndex.context
            AppIndex.addFav(context, app)
            //adapter.notifyItemChanged(pos)
        }

//        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//            super.onSelectedChanged(viewHolder, actionState)
//        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = 1f
            Log.d("AppListConfigFragment", "${viewHolder.itemView.width}")
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val width = viewHolder.itemView.width
                val alpha = 1.0f - abs(dX) / width
                viewHolder.itemView.alpha = alpha
                //viewHolder.itemView.translationX = dX
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

    }

    private fun displayPrompt(str: String) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.displayPrompt(str)
        }
    }
}