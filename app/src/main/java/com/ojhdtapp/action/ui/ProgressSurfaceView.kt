package com.ojhdtapp.action.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.withTranslation
import com.ojhdtapp.action.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProgressSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs) {
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressSurfaceView,
            0, 0
        ).apply {
            try {
                targetProgress = getInteger(R.styleable.ProgressSurfaceView_targetProgress, 0)
                paintColor = getColor(R.styleable.ProgressSurfaceView_color, TypedValue().apply {
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
        holder.addCallback(ProgressSurfaceViewCallback())
    }

    public fun setTargetProgress(value: Int) {
        targetProgress = value
    }

    private var mwidth = 0f
    private var mheight = 0f
    private var paintColor: Int? = null
    private val fillColorPrimaryPaint = Paint().apply {
        style = Paint.Style.FILL
        color = paintColor!!
    }
    private var targetProgress = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mwidth = w.toFloat()
        mheight = h.toFloat()
    }

    inner class ProgressSurfaceViewCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            ValueAnimator.ofInt(0, targetProgress).apply {
                duration = 1000
                addUpdateListener { updatedAnimation ->
                    val canvas = holder.lockCanvas()
                    Log.d("aaa", updatedAnimation.animatedValue.toString())
                    canvas?.drawColor(Color.WHITE)
                    canvas?.withTranslation(0f, mheight) {
                        val currentHeight =
                            mheight * (((updatedAnimation.animatedValue as Int).toFloat()).div(
                                100f
                            ))
                        drawRect(0f, -currentHeight, mwidth, 0f, fillColorPrimaryPaint)
                    }
                    holder.unlockCanvasAndPost(canvas)
                }
                start()

            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
        }

    }
}