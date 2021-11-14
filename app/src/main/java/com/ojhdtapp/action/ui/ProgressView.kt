package com.ojhdtapp.action.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.ojhdtapp.action.R

class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mwidth = 0f
    private var mheight = 0f
    private var progress = 0
    private val fillcolorPrimaryPaint = Paint().apply {
        style = Paint.Style.FILL
        color = TypedValue().apply {
            context.theme.resolveAttribute(
                android.R.attr.colorPrimary,
                this,
                true
            )
        }.data
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mwidth = w.toFloat()
        mheight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}