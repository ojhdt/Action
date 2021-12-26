package com.ojhdtapp.action.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView
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
            } finally {
                recycle()
            }
        }
    }

    private var progress: Int = 0
    public fun setProgress(value: Int) {
        progress = value
        text = progress.toString()
        invalidate()
    }

    public fun getProgress(): Int {
        return progress
    }

}