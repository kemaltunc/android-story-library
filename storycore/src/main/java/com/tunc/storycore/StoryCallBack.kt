package com.tunc.storycore

import android.view.View

interface StoryCallBack {
    fun started()
    fun finished()
    fun setView(index: Int)
    fun storyView(index: Int, view: View)

    fun storyPause()
    fun storyResume()
}