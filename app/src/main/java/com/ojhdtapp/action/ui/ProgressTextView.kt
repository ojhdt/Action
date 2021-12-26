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
                intValue = getInteger(R.styleable.ProgressTextView_intValue, 0)
                getString(R.styleable.ProgressTextView_suffix)?.let{
                    suffix = it
                }
            } finally {
                recycle()
            }
        }
    }

    private var intValue: Int = 0
    private var suffix:String? = null
    public fun setIntValue(value: Int) {
        intValue = value
        text = if(suffix.isNullOrEmpty()) intValue.toString() else intValue.toString().plus(suffix)
        invalidate()
    }

    public fun getIntValue(): Int {
        return intValue
    }

}