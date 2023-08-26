package com.zyprex.flauncher.UI.AppListConfig

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.dp2px
import com.zyprex.flauncher.UTIL.emphasisFirstChar
import kotlin.math.abs

class AppListConfigFragment : Fragment() {

    lateinit var appIndex: AppIndex
    lateinit var adapter: AppListConfigAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        displayPrompt("AppListConfig")
        activity?.let { context ->
            val screenWidth = context.resources.displayMetrics.widthPixels
            val appIndex = AppIndex(context as MainActivity)
            val appListData = appIndex.data()

            val layout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.VERTICAL
            }
            val hscrollView = HorizontalScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                id = MainActivity.CPOS_ID
                //setBackgroundColor(Color.parseColor("#00000000"))
            }
            val wrapper = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            adapter = AppListConfigAdapter(appListData)
            val rv = RecyclerView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    screenWidth + MainActivity.ITEM_MARGIN,
                    0,
                    1.0f
                )
                layoutManager = GridLayoutManager(context, 2)
                id = MainActivity.APPLIST_ID
                //isLongClickable = true
            }
            rv.adapter = adapter

            val touchCallBack = MyItemTouchCallBack(adapter, appIndex)
            val itemTouchHelper = ItemTouchHelper(touchCallBack)
            itemTouchHelper.attachToRecyclerView(rv)

            // init Buttons in hscrollView
            var indx = 0

            for (app in appListData) {
                if (indx == 0 || indx > 0 && appListData[indx-1].label[0] != app.label[0]) {
                    val btn = TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        textSize = 20f
                        setPadding(
                            dp2px(context, 2),
                            0,
                            dp2px(context, 2),
                            0)
                        text = emphasisFirstChar(app.label.substring(0, 1))
                        setShadowLayer(2f, 2f, 2f, Color.BLACK)
                        setOnClickListener {
                            val toPos = appListData.indexOfFirst { i -> i.label[0] == this.text[0]}
                            rv.scrollToPosition(toPos)
                            //Toast.makeText(context, "${this.text} ${toPos}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    wrapper.addView(btn)
                }
                indx++
            }
            hscrollView.addView(wrapper)
            layout.addView(rv)
            layout.addView(hscrollView)
            return layout
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
            appIndex.dataFavAdd(app)
            adapter.notifyItemChanged(pos)
        }

//        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//            super.onSelectedChanged(viewHolder, actionState)
//        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = 1f
            //Log.d("AppListConfigFragment", "${viewHolder.itemView.width}")
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