package com.tunc.myapplicationw

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.tunc.storycore.StoryCallBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StoryCallBack {

    var storyList = listOf(
        Stories(0, "https://i.stack.imgur.com/c0xUo.jpg"),
        Stories(
            1,
            "http://techslides.com/demos/sample-videos/small.mp4"
        ),
        Stories(0, "https://i.stack.imgur.com/c0xUo.jpg"),
        Stories(
            1,
            "http://techslides.com/demos/sample-videos/small.mp4"
        )
    )

    lateinit var player: SimpleExoPlayer
    lateinit var currentView: View

    data class Stories(
        var type: Int,
        var url: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main_story_screen.run {
            storyCount = storyList.size
            storyDuration = 5000
            proggressTint = R.color.orange
            progressBackGroundTint = R.color.white
            build(this@MainActivity)
            start()
        }
    }

    override fun started() {
        Toast.makeText(this, "started", Toast.LENGTH_LONG).show()
    }

    override fun finished() {
        Toast.makeText(this, "finished", Toast.LENGTH_LONG).show()
    }

    override fun setView(index: Int) {
        if (storyList[index].type == 0) {
            activity_main_story_screen.setStoryView(ImageView(this))
        } else {
            activity_main_story_screen.setStoryView(PlayerView(this))
        }
    }


    override fun storyView(index: Int, view: View) {
        if (view is ImageView) {
            showImage(index, view)
        } else if (view is PlayerView) {
            showVideo(index, view)
        }
        currentView = view
    }

    private fun showImage(index: Int, imageView: ImageView) {
        Glide.with(this)
            .load(storyList[index].url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    activity_main_story_screen.ready()
                    return false
                }

            })
            .into(imageView)
    }

    private fun showVideo(index: Int, playerView: PlayerView) {


        lateinit var bandwidthMeter: BandwidthMeter
        lateinit var mediaSource: ExtractorMediaSource
        lateinit var mediaDataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory
        lateinit var trackSelector: DefaultTrackSelector


        playerView.hideController()
        playerView.useController = false



        bandwidthMeter = DefaultBandwidthMeter()

        val userAgent = Util.getUserAgent(this, this.applicationInfo.name)
        mediaDataSourceFactory = DefaultDataSourceFactory(
            this,
            userAgent,
            bandwidthMeter
        )

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)

        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        mediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse(storyList[index].url))


        player.prepare(mediaSource, false, false)


        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

        playerView.requestFocus()

        playerView.player = player

        player.playWhenReady = true


        player.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {

            }

            override fun onRenderedFirstFrame() {

            }
        })

        player.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {

            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {

            }

            override fun onLoadingChanged(isLoading: Boolean) {


            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    activity_main_story_screen.setNewDuration(player.duration)
                    activity_main_story_screen.ready()
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

            }

            override fun onPlayerError(error: ExoPlaybackException) {
            }

            override fun onPositionDiscontinuity(reason: Int) {

            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

            }

            override fun onSeekProcessed() {}
        })
    }


    override fun storyPause() {
        if (currentView is PlayerView && this::player.isInitialized) {
            player.playWhenReady = false
        }
        activity_main_story_screen.pause()
    }

    override fun storyResume() {
        if (currentView is PlayerView && this::player.isInitialized) {
            player.playWhenReady = true
        }
        activity_main_story_screen.resume()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (currentView is PlayerView && this::player.isInitialized) {
            player.stop()
            player.release()
        }
        activity_main_story_screen.destroy()
    }

    override fun onPause() {
        super.onPause()
        storyPause()
    }

    override fun onResume() {
        super.onResume()
        storyResume()
    }

}
