package com.zyprex.flauncher.UI.AppList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.MainActivity

class AppListFragment: Fragment() {

    private lateinit var adapter: AppListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        displayPrompt("AppList")
        activity?.let { activity ->
            val appIndex = AppIndex(activity as MainActivity)
            var dataFav = appIndex.dataFav()
            val rv = RecyclerView(activity)
            rv.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            rv.layoutManager = LinearLayoutManager(activity).apply {
                stackFromEnd = true
            }
            adapter = AppListAdapter(dataFav)
            rv.adapter = adapter
            //rv.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

            val touchCallBack = MyItemTouchCallBack(adapter, appIndex)
            val itemTouchHelper = ItemTouchHelper(touchCallBack)
            itemTouchHelper.attachToRecyclerView(rv)

            rv.id = MainActivity.APPLIST_ID
            return rv
        }
        return null
    }

    class MyItemTouchCallBack(val adapter: AppListAdapter, val appIndex: AppIndex)
        : ItemTouchHelper.Callback() {
        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            appIndex.dataFavRemove(position)
            appIndex.updateFav()
            adapter.notifyItemRemoved(position)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            //onItemTouchListener.onMove(fromPos, toPos)
            appIndex.dataFavSwap(fromPos, toPos)
            adapter.notifyItemMoved(fromPos, toPos)
            adapter.notifyItemChanged(fromPos)
            adapter.notifyItemChanged(toPos)
            return true
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_IDLE -> {
                    appIndex.updateFav()
                }
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

            super.clearView(recyclerView, viewHolder)
        }
    }

    private fun displayPrompt(str: String) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.displayPrompt(str)
        }
    }

}