package com.ojhdtapp.action.ui.content

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView

class HighlightMaterialCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}