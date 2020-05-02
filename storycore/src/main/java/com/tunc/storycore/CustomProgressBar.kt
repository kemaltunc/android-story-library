package com.tunc.storycore

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.custom_progressbar.view.*

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var view: View = LayoutInflater.from(context)
        .inflate(R.layout.custom_progressbar, this, true)

    init {
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT, 1f
        )
        this.layoutParams = params
    }

    fun setProgressTint(color: Int) {
        view.custom_progresss.progressTintList = ContextCompat.getColorStateList(context, color)
    }

    fun setProgressBackGroundTint(color: Int) {
        view.custom_progresss.progressBackgroundTintList =
            ContextCompat.getColorStateList(context, color)
    }

    fun getProgress(): ProgressBar = view.custom_progresss

}