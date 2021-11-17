package com.ojhdtapp.action.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.withTranslation
import com.ojhdtapp.action.R

class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressView,
            0, 0
        ).apply {
            try {
                progress = getInteger(R.styleable.ProgressView_progress, 0)
                paintColor = getColor(R.styleable.ProgressView_color, TypedValue().apply {
                    context.theme.resolveAttribute(
                        android.R.attr.colorPrimary,
                        this,
                        true
                    )
                }.data)
            } finally {
                recycle()
            }
        }
    }
    public fun setProgress(value:Int){
        progress = value
        invalidate()
    }
    public fun getProgress():Int{
        return progress
    }
    private var mwidth = 0f
    private var mheight = 0f
    private var progress: Int = 0
    private var paintColor: Int? = null
    private val fillcolorPrimaryPaint = Paint().apply {
        style = Paint.Style.FILL
        color = paintColor!!
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mwidth = w.toFloat()
        mheight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.withTranslation(0f, mheight) {
            val currentHeight = mheight * ((progress!!.toFloat()).div(100f))
            drawRect(0f, -currentHeight, mwidth, 0f, fillcolorPrimaryPaint)
        }
    }
}