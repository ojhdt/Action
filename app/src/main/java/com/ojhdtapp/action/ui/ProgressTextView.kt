package com.ojhdtapp.action.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.ojhdtapp.action.R

class ProgressTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressTextView,
            0, 0
        ).apply {
            try {
                progress = getInteger(R.styleable.ProgressTextView_progress, 0)
                getString(R.styleable.ProgressTextView_suffix)?.let{
                    suffix = it
                }
            } finally {
                recycle()
            }
        }
    }

    private var progress: Int = 0
    private var targetProgress: Int = 0
    private var suffix:String? = null
    public fun setProgress(value: Int) {
        progress = value
        invalidate()
    }

    public fun getProgress(): Int {
        return progress
    }
    public fun setTargetProgress(value: Int){
        targetProgress = value
        ValueAnimator.ofInt(0, targetProgress).apply {
            duration = 1000
            addUpdateListener { updatedAnimation ->
                progress = updatedAnimation.animatedValue as Int
                text = if(suffix.isNullOrEmpty()) progress.toString() else progress.toString().plus(suffix)
            }
            start()
        }
    }
}