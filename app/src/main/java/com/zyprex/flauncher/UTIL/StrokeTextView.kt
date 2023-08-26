package com.zyprex.flauncher.UTIL

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView

class StrokeTextView: TextView {

    private var borderText: TextView? = null

    constructor(context: Context): super(context) {
        borderText = TextView(context)
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        borderText = TextView(context, attrs)
        init()

    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        borderText = TextView(context, attrs, defStyle)
        init()
    }

    fun init() {
        borderText?.let {
            val tp1 = it.paint
            tp1.strokeWidth = 5.5f
            tp1.style = Paint.Style.STROKE
            it.setTextColor(Color.parseColor("#62000000"))
        }
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        super.setLayoutParams(params)
        borderText?.layoutParams = params
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tt = borderText?.text
        if (tt == null || tt != this.text) {
            borderText?.text = this.text
            this.postInvalidate()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        borderText?.measure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        borderText?.layout(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        borderText?.draw(canvas)
        super.onDraw(canvas)
    }

    override fun setGravity(gravity: Int) {
        borderText?.gravity = gravity
        super.setGravity(gravity)
    }

    override fun setTextSize(size: Float) {
        borderText?.textSize = size
        super.setTextSize(size)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        borderText?.setPadding(left, top, right, bottom)
        super.setPadding(left, top, right, bottom)
    }

    override fun setLines(lines: Int) {
        borderText?.setLines(lines)
        super.setLines(lines)
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        borderText?.ellipsize = where
        super.setEllipsize(where)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        borderText?.setText(text, type)
        super.setText(text, type)
    }

    override fun setTypeface(tf: Typeface?) {
        borderText?.setTypeface(tf)
        super.setTypeface(tf)
    }

}