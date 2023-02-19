package com.southernsunrise.playme.fragments.mainFragmentScreens

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentPlayerBinding
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.services.MusicService
import com.southernsunrise.playme.utilities.Constants
import com.southernsunrise.playme.utilities.Helper

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var songCoverImageView: ImageView
    private lateinit var songTitleTextView: TextView
    private lateinit var songArtistNameTextView: TextView
    private lateinit var addRemoveFromFavoritesButton: ImageButton
    private lateinit var toolbar: Toolbar

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var favoriteImageButton: ImageButton
    private var trackPlayingProgressSeekBar: SeekBar? = null


    private lateinit var minutesPassedTextView: TextView
    private lateinit var minutesLeftTextView: TextView
    private lateinit var secondsPassedTextView: TextView
    private lateinit var secondsLeftTextView: TextView

    private lateinit var backButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    private lateinit var rewindButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var playStopButton: ImageButton
    private lateinit var fastForwardButton: ImageButton
    private lateinit var loopButton: ImageButton

    private lateinit var trackToBePlayed: Track

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var exoPlayerControlView: StyledPlayerControlView

    private lateinit var musicServiceIntent: Intent

    val TAG = "PlayerFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        musicServiceIntent = Intent(requireActivity(), MusicService()::class.java)
        arguments?.let { trackBundle ->
            trackToBePlayed = trackBundle.getSerializable(Constants.TRACK) as Track
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val view = binding.root

        initializeViews()
        setupViews()


        return view
    }

    private fun setupViews() {
        setupSongCoverImageView()
        setupTextViews()
        setupBackButton()
    }

    private fun setupBackButton() {
        backButton.setOnClickListener { this.findNavController().popBackStack() }
    }


    private fun setupSongCoverImageView() {
        songCoverImageView.apply {
            clipToOutline = true
            scaleType = ImageView.ScaleType.CENTER_CROP
            Helper.loadImageIntoImageViewAndSetupViewBackgroundsWithPalette(
                arrayListOf(this, binding.playerRoot),
                ApiConstants.getImageLink(ApiConstants.ALBUMS, trackToBePlayed.albumId, "500x500")
            )
            Log.i("iddd", binding.playerRoot.id.toString())
        }
    }

    private fun setupTextViews() {
        songTitleTextView.text = trackToBePlayed.name
        songArtistNameTextView.text = trackToBePlayed.artistName
        binding.tvMinutesLeft.text = (trackToBePlayed.playbackSeconds / 60).toString()
        binding.tvSecondsLeft.text = (trackToBePlayed.playbackSeconds % 60).toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViews() {

        songCoverImageView = binding.ivTrackImage

        songArtistNameTextView = binding.tvArtistName
        songTitleTextView = binding.tvTrackTitle

        trackPlayingProgressSeekBar = binding.seekbar

        minutesPassedTextView = binding.tvMinutesPassed
        secondsPassedTextView = binding.tvSecondsPassed
        minutesLeftTextView = binding.tvMinutesLeft
        secondsLeftTextView = binding.tvSecondsLeft
        backButton = binding.ibBack


        exoPlayerControlView = binding.exoplayerControlView
        exoPlayerControlView.apply {
            findViewById<ImageView>(R.id.exo_play_pause).setOnClickListener {
                val trackList = ArrayList<Track>()
                trackList.add(trackToBePlayed)
                musicServiceIntent.putExtra(
                    "trackList",
                    trackList as List<Track> as java.io.Serializable
                )
                requireActivity().startForegroundService(musicServiceIntent)

            }
        }


    }


}
