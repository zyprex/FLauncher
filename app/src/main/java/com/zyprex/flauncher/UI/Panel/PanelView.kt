package com.zyprex.flauncher.UI.Panel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zyprex.flauncher.DT.ActionInfo
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.UI.MainActivity
import com.zyprex.flauncher.UTIL.appendLineToFile
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.max

class PanelView: TextView {
    constructor(context: Context): super(context)

    private var isDown = false
    private var mPaint = Paint().apply {
        strokeWidth = 4f
        color = Color.parseColor("#00AF00")
    }

    private var grid = 50 * context.resources.displayMetrics.density
    private var x0 = 0f
    private var y0 = 0f
    private var x1 = 0f
    private var y1 = 0f
    private var multiTouchCount = 0
    private var pressStartTime = 0L
    private var timer: Timer? = null

    private val verdict: PanelVerdict = PanelVerdict(context)

    var actionCode = ""

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val consumed = touchAction(event)
        if (consumed) {
            return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (isDown) {
                val gh = grid / 2
                val w = this.width.toFloat()
                val h = this.height.toFloat()
                // draw # shape
                it.drawLines(floatArrayOf(
                    0f, y0 - gh, w, y0 - gh,
                    0f, y0 + gh, w, y0 + gh,
                    x0 - gh, 0f, x0 - gh, h,
                    x0 + gh, 0f, x0 + gh, h,
                ), mPaint)
            } else {
                it.drawColor(0, PorterDuff.Mode.CLEAR)
            }
        }
    }

    private fun touchAction(event: MotionEvent?): Boolean {
        // use event.getActionMasked() detect multi-touch event
        return when(event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isDown = true
                multiTouchCount = 1
                x0 = event.x
                y0 = event.y
                pressStartTime = Date().time
                if (timer != null && timer is Timer) timer?.cancel()
                invalidate()
                true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                //Log.d("PanelView", "${event.pointerCount}")
                multiTouchCount = max(event.pointerCount, multiTouchCount)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                x1 = event.x
                y1 = event.y
                true
            }
            MotionEvent.ACTION_UP -> {
                isDown = false
                val diffX = x0 - x1
                val diffY = y0 - y1
                val diffTime = Date().time - pressStartTime
                actionCode += actionCodeSewer(diffX, diffY, diffTime, multiTouchCount)
                displayPrompt(actionCode)
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        timer = null
                        // handle actionSNum result
                        verdict.actionStart(actionCode)
                        actionCode = ""
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            displayPrompt("Panel")
                        }
                    }
                }, 1000)
                invalidate()
                true
            }
            else -> false
        }
    }

    private fun actionCodeSewer(diffX: Float, diffY: Float, diffTime: Long, multiTouchCount: Int): String {
        return if (multiTouchCount == 1) {
            when {
                (abs(diffX) < grid && diffY >  grid) -> "8"
                (abs(diffX) < grid && diffY < -grid) -> "2"
                (abs(diffY) < grid && diffX >  grid) -> "4"
                (abs(diffY) < grid && diffX < -grid) -> "6"
                (diffX < -grid && diffY < -grid) -> "3"
                (diffX >  grid && diffY < -grid) -> "1"
                (diffX < -grid && diffY >  grid) -> "9"
                (diffX >  grid && diffY >  grid) -> "7"
                else -> if (diffTime < 500) "5" else "0"
            }
        } else {
            when(multiTouchCount) {
                2 -> "D"
                3 -> "T"
                4 -> "Q"
                5 -> "P"
                6 -> "H"
                7 -> "S"
                8 -> "O"
                9 -> "N"
                10 -> "F"
                else -> ""
            }
        }
    }

    private fun displayPrompt(str: String) {
        if (context != null) {
            val mainActivity = context as MainActivity
            mainActivity.displayPrompt(str)
        }
    }

    fun instantRun() {
        timer?.cancel()
        val ok = verdict.actionStart(actionCode)
        if (ok == false) {
            interactiveAddPanelConfig(actionCode)
        }
        actionCode = ""
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            displayPrompt("Panel")
        }
    }

    private fun appendPanelConfig(str: String, actionCode: String, param: String) {
        appendLineToFile(context, AppIndex.panelFileName, "${str}#${actionCode}#${param}")
    }

    fun interactiveAddPanelConfig(actionCode: String) {
        AlertDialog.Builder(context).apply {
            setTitle("Add Item to Panel Config")
            setItems(PanelVerdict.type) { _, i ->
                val input = EditText(context)
                AlertDialog.Builder(context).apply {
                    setTitle(PanelVerdict.type[i])
                    setMessage(PanelVerdict.typeInfo[i])
                    setView(input)
                    setPositiveButton("OK") { _, _ ->
                        PanelVerdict.actionInfoList.add(ActionInfo(PanelVerdict.type[i], actionCode, input.text.toString()))
                        appendPanelConfig(PanelVerdict.type[i], actionCode, input.text.toString())
                    }
                    setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
                }.show()
            }
        }.show()
    }
}