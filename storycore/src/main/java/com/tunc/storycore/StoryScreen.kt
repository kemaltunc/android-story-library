package com.tunc.storycore

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.story_screen.view.*

class StoryScreen : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    var view: View = LayoutInflater.from(context).inflate(R.layout.story_screen, this, true)

    private var storyIndex = 0
    private var pressTime = 0L
    private var limit = 800L


    var storyCount: Int = 0
    var storyDuration: Long = 0L
    var proggressTint: Int = R.color.orange
    var progressBackGroundTint: Int = R.color.white
    lateinit var currentView: View


    var storyProgress = ArrayList<ProgressBar>()
    lateinit var storyAnimator: ObjectAnimator
    lateinit var storyCallback: StoryCallBack

    init {
        view.right_frame.setOnTouchListener(onTouchListener())
        view.left_frame.setOnTouchListener(onTouchListener())
    }

    fun build(storyCallback: StoryCallBack) {
        this.storyCallback = storyCallback

        for (i in 0 until storyCount) {
            val progress = CustomProgressBar(context)
            progress.setProgressTint(proggressTint)
            progress.setProgressBackGroundTint(progressBackGroundTint)
            storyProgress.add(progress.getProgress())
            view.story_screen_progress_list.addView(progress)
        }
    }

    fun start() {
        storyCallback.setView(0)
    }

    fun setStoryView(view: View) {
        currentView = view
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        currentView.layoutParams = params
        currentlyDisplayedView.removeAllViews()
        currentlyDisplayedView.addView(currentView)
        storyCallback.storyView(storyIndex, currentView)
    }

    fun ready() {
        play(storyIndex)
    }

    private fun play(index: Int) {
        when {
            index < 0 -> {
                finished()
            }
            index < storyProgress.size -> {
                storyIndex = index
                storyAnimator =
                    ObjectAnimator.ofInt(storyProgress[index], "progress", 100)
                storyAnimator.duration = storyDuration
                storyAnimator.start()

                storyAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (storyIndex < storyProgress.size - 1) storyCallback.setView(++storyIndex)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        animation?.apply { removeAllListeners() }
                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }
                })
            }
            else -> {
                finished()
            }
        }

    }


    fun next() {
        storyAnimator.end()
    }

    private fun prev() {
        if (storyIndex > 0 && storyIndex < storyProgress.size) {
            storyAnimator.cancel()
            storyProgress[storyIndex].progress = 0
            storyProgress[storyIndex - 1].progress = 0
            storyIndex -= 1
            storyCallback.setView(storyIndex)
        } else {
            storyCallback.finished()
        }
    }

    fun setNewDuration(duration: Long) {
        storyDuration = duration
    }

    fun destroy() {
        storyAnimator.cancel()
    }

    private fun finished() {
        storyAnimator.cancel()
        storyCallback.finished()
    }

    fun pause() {
        if (this::storyAnimator.isInitialized) {
            storyAnimator.pause()
        }
    }

    fun resume() {
        if (this::storyAnimator.isInitialized) {
            storyAnimator.resume()
        }
    }


    private fun onTouchListener() = object : OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    storyCallback.storyPause()
                }
                MotionEvent.ACTION_UP -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - pressTime < limit) {
                        if (v?.id == view.left_frame.id) {
                            prev()
                        } else if (v?.id == view.right_frame.id) {
                            next()
                        }
                    } else {
                        storyCallback.storyResume()
                    }
                }
                else -> {
                    return false
                }
            }
            return true
        }
    }

}




