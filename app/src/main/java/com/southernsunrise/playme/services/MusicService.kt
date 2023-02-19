package com.southernsunrise.playme.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.southernsunrise.playme.R
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.utilities.Constants


class MusicService : android.app.Service() {

    var exoPlayer: ExoPlayer? = null

    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var mediaDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter
    private lateinit var notificationListener: NotificationListener
    private var trackList: List<Track>? = null
    private var trackImageBitmap: Bitmap? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build()

        setupExoPlayer()

        // audio focus attributes
        val audioAttributes = com.google.android.exoplayer2.audio.AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()


        exoPlayer?.setAudioAttributes(audioAttributes, true)


    }

    private fun setupExoPlayer() {
        exoPlayer?.playWhenReady = true
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_READY -> {
                        setupMediaNotification()
                    }
                    Player.STATE_ENDED -> {
                        destroyExoPlayer()
                        this@MusicService.onDestroy()
                    }
                }
            }

        })
    }

    private fun destroyExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun setupMediaNotification() {

        setupMediaDescriptionAdapter()
        setupPlayerNotificationListener()

        // notification manager
        val channelID = resources.getString(R.string.app_name) + "Music Channel"
        val notificationID = 111


        notificationManager = PlayerNotificationManager.Builder(this, notificationID, channelID)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setChannelImportance(IMPORTANCE_LOW)
            .setSmallIconResourceId(R.drawable.app_icon)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setNextActionIconResourceId(R.drawable.ic_next)
            .setPreviousActionIconResourceId(R.drawable.ic_previous)
            .setPlayActionIconResourceId(R.drawable.ic_play)
            .setPauseActionIconResourceId(com.google.android.exoplayer2.R.drawable.exo_icon_pause)
            .setChannelNameResourceId(R.string.app_name)
            .build()

        notificationManager.setPlayer(exoPlayer)
        notificationManager.setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManager.setUseFastForwardAction(false)
        notificationManager.setUseRewindAction(false)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // tracksList = listOf(intent?.extras?.getSerializable("tracksList") as  Pair<Track, Drawable>)
        trackList = intent?.extras?.getSerializable("trackList") as List<Track>
        // trackImageBitmap = intent?.extras?.get("albumImageDrawable") as Bitmap?
        prepareExoPlayer()
        return super.onStartCommand(intent, flags, startId)

    }


    override fun onDestroy() {
        //release the player
        exoPlayer?.let {
            if (it.isPlaying) it.stop()
            notificationManager.setPlayer(null)
            it.release()
            stopForeground(true)
            stopSelf()
        }

        super.onDestroy()

    }

    fun setupPlayerNotificationListener() {
        notificationListener = object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                super.onNotificationCancelled(notificationId, dismissedByUser)
                stopForeground(true)
                exoPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                    }
                }

            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                super.onNotificationPosted(notificationId, notification, ongoing)
                startForeground(notificationId, notification)
            }

        }
    }

    fun setupMediaDescriptionAdapter() {

        mediaDescriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return player.currentMediaItem?.mediaMetadata?.title.toString()

            }


            override fun getCurrentSubText(player: Player): CharSequence? {
                return return player.currentMediaItem?.mediaMetadata?.subtitle
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val pendingIntent = NavDeepLinkBuilder(this@MusicService)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.nav_main)
                    .setDestination(R.id.playerFragment)
                    .setArguments(Bundle().apply {
                        this.putSerializable(
                            Constants.TRACK,
                            trackList?.get(player.currentMediaItemIndex)
                        )
                    })
                    .createPendingIntent()

                return pendingIntent
            }


            override fun getCurrentContentText(player: Player): CharSequence? {
                return null
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                val defaultImageDrawable =
                    this@MusicService.getDrawable(R.drawable.img_default_track_listitem)

                player.currentMediaItem?.mediaId?.let {
                    ApiConstants.getImageLink(
                        ApiConstants.ALBUMS,
                        it,
                        "200x200"
                    )
                }?.let { loadBitmap(it, callback) }
                return defaultImageDrawable?.toBitmap() //or stub image
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            private fun loadBitmap(
                url: String,
                callback: PlayerNotificationManager.BitmapCallback?
            ) {


                Glide.with(this@MusicService)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                        ) {
                            callback?.onBitmap(resource)

                        }


                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }

        }

    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    fun prepareExoPlayer() {

        trackList?.let {
            for (track in it) {
                val trackMetadata = MediaMetadata.Builder()
                    .setAlbumTitle(track.albumName)
                    .setArtist(track.artistName)
                    .setTitle(track.name)
                    .setIsPlayable(true)
                    .build()

                val mediaItem: MediaItem = MediaItem.Builder()
                    .setUri(track.previewURL)
                    .setMediaId(track.albumId)
                    .setMediaMetadata(trackMetadata)
                    .build()
                exoPlayer?.addMediaItem(mediaItem)
            }
        }
        exoPlayer?.prepare()
    }

    /*fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)


    }

     */
}

