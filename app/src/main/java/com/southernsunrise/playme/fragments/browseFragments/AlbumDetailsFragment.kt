package com.southernsunrise.playme.fragments.browseFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.adapters.ResponseObjectsRecyclerViewAdapter
import com.southernsunrise.playme.databinding.FragmentAlbumDetailsBinding
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.services.MusicService
import com.southernsunrise.playme.utilities.Constants
import com.southernsunrise.playme.utilities.Helper
import com.southernsunrise.playme.viewModels.AlbumDetailsViewModel
import java.util.*
import kotlin.collections.List
import kotlin.math.abs


class AlbumDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAlbumDetailsBinding
    private lateinit var albumCoverImageView: ImageView
    private lateinit var albumNameTextView: TextView
    private lateinit var albumNameToolbarTextView: TextView


    private lateinit var backArrowImageButton: ImageButton
    private lateinit var albumPlayButton: ImageButton

    private lateinit var artistPhotoImageView: ImageView
    private lateinit var artistNameTextView: TextView
    private lateinit var albumTypeAndDateTextView: TextView

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var albumCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var albumDetailsConstraintLayout: ConstraintLayout
    private lateinit var similarAlbumsConstraintLayout: ConstraintLayout

    private lateinit var albumArtistsAndTracksNamesTextView: TextView
    private lateinit var albumFullReleaseDateTextView: TextView
    private lateinit var contributingArtistsRecyclerView: RecyclerView
    private lateinit var contributingArtistsRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter
    private lateinit var similarAlbumsRecyclerView: RecyclerView
    private lateinit var similarAlbumsRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter

    private lateinit var albumDetailsViewModel: AlbumDetailsViewModel

    private lateinit var musicServiceIntent: Intent


    val TAG = "AlbumDetailsFragment"

    var album: Album? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            album = it.getSerializable(Constants.ALBUM) as? Album
        }
        album?.let { album ->
            albumDetailsViewModel = AlbumDetailsViewModel(album)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        musicServiceIntent = Intent(requireActivity(), MusicService::class.java)

        album?.let {
            initializeViews()
            setupViews()
            setupDataObservers()
        }


        return view
    }

    private fun initializeViews() {
        albumCoverImageView = binding.ivAlbumCover
        albumNameTextView = binding.tvAlbumName
        albumNameToolbarTextView = binding.tvToolbarAlbumName

        albumPlayButton = binding.ibAlbumPlay

        backArrowImageButton = binding.ibArrowBack

        artistPhotoImageView = binding.ivAlbumArtist
        artistNameTextView = binding.tvArtistName
        albumTypeAndDateTextView = binding.tvAlbumDate

        appBarLayout = binding.albumAppBarLayout
        albumCollapsingToolbarLayout = binding.ctlAlbum
        albumDetailsConstraintLayout = binding.clAlbumDetailsPlayBtn
        similarAlbumsConstraintLayout = binding.clSimilarAlbums

        albumFullReleaseDateTextView = binding.tvAlbumReleaseDate

        albumArtistsAndTracksNamesTextView = binding.tvAlbumArtistsTracksName

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun setupViews() {
        backArrowImageButton.setOnClickListener {
            this.findNavController().popBackStack()
        }
        setupTextViews()
        setupAlbumPhotoImageView()
        setupArtistPhotoImageView()
        setupContributingArtistsRecyclerView()
        setupSimilarAlbumsRecyclerView()
        setOnAppBarLayoutOffsetChangeListener()
        Helper.setViewPaddings(this, listOf(similarAlbumsRecyclerView, binding.nsvAlbumDetails))
    }

    private fun setupTextViews() {
        album?.let {
            albumNameTextView.text = it.name
            albumNameToolbarTextView.text = it.name

            artistNameTextView.text = it.artistName

            val albumFullReleasedDate = it.originallyReleased
            val albumReleasedYear = albumFullReleasedDate.subSequence(0, 4)
            albumTypeAndDateTextView.text = if (it.isSingle) {
                "Single • $albumReleasedYear"
            } else "Album • $albumReleasedYear"

            albumFullReleaseDateTextView.text = Helper.getAlbumFullDateText(it)
        }
    }

    private fun setupArtistPhotoImageView() {
        val artistImageUrl = album?.contributingArtists?.let { it1 ->
            ApiConstants.getImageLink(
                ApiConstants.ARTISTS,
                it1.primaryArtist,
                "200x200"
            )
        }
        artistImageUrl?.let { it -> Helper.loadPhotoIntoImageView(artistPhotoImageView, it) }
    }

    private fun setupAlbumPhotoImageView() {
        // load album image into imageView, get palette and set view backgrounds
        album?.let {
            val imageUrl = ApiConstants.getImageLink(ApiConstants.ALBUMS, it.id, "500x500")
            Helper.loadImageIntoImageViewAndSetupViewBackgroundsWithPalette(
                arrayListOf(
                    albumCoverImageView,
                    albumPlayButton,
                    albumCollapsingToolbarLayout,
                    albumDetailsConstraintLayout
                ), imageUrl
            )
        }
    }

    private fun setupContributingArtistsRecyclerView() {
        contributingArtistsRecyclerView = binding.rvContributingArtists
        contributingArtistsRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = true)
        contributingArtistsRecyclerView.adapter = contributingArtistsRecyclerViewAdapter
    }

    private fun setupSimilarAlbumsRecyclerView() {
        similarAlbumsRecyclerView = binding.rvSimilarAlbums
        similarAlbumsRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = false)
        similarAlbumsRecyclerView.adapter = similarAlbumsRecyclerViewAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDataObservers() {
        setupAlbumTracksObserver()
        setupContributingArtistsObserver()
        setupSimilarAlbumsObserver()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAlbumTracksObserver() {
        albumDetailsViewModel.albumTracksLiveData.observe(
            viewLifecycleOwner
        ) {
            val tracksList: List<Track> = it.tracks

            albumArtistsAndTracksNamesTextView.text =
                Helper.getArtistAndTrackNamesText(Pair(album!!, tracksList))

            albumPlayButton.setOnClickListener{
                startAlbumPlayService(tracksList)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAlbumPlayService(trackList: List<Track>) {
        musicServiceIntent.putExtra("trackList", trackList as java.io.Serializable)
        requireActivity().startForegroundService(musicServiceIntent)
    }

    private fun setupContributingArtistsObserver() {
        albumDetailsViewModel.contributingArtistsLiveData.observe(
            viewLifecycleOwner
        ) {
            val artistsList = it.artists
            for (artist in it.artists) {
                updateContributingArtistsRecyclerView(artist)
            }
        }

    }

    private fun setupSimilarAlbumsObserver() {
        albumDetailsViewModel.similarAlbumsLiveData.observe(
            viewLifecycleOwner
        ) {
            similarAlbumsConstraintLayout.isVisible = true
            for (album in it.albums) {
                updateSimilarAlbumsRecyclerView(album)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSimilarAlbumsRecyclerView(album: Album) {
        similarAlbumsRecyclerViewAdapter.responseObjectsList.add(album)
        similarAlbumsRecyclerViewAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateContributingArtistsRecyclerView(artist: Artist) {
        contributingArtistsRecyclerViewAdapter.responseObjectsList.add(artist)
        contributingArtistsRecyclerViewAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setOnAppBarLayoutOffsetChangeListener() {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            // change album name  textViews' alpha depending on appBarLayout offset
            albumNameToolbarTextView.alpha =
                Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
            albumNameTextView.alpha = (1f - albumNameToolbarTextView.alpha)

            val scaleValue =
                (1 - abs(verticalOffset.toDouble() / appBarLayout.totalScrollRange.toDouble()) / 2).toFloat()

            albumCoverImageView.apply {
                this.animate().scaleX(scaleValue).scaleY(scaleValue).setDuration(0).start()
                this.alpha = albumNameTextView.alpha
            }


            Log.i("scalevalueee", scaleValue.toString())
            Log.i("vOffset", verticalOffset.toString())

        }
        )
    }

    private fun setViewPaddings(){
        val displayDimen = (requireActivity() as MainActivity).getDisplayMatrix()
        val startPaddingValue = displayDimen["width"]?.times(0.05)?.toInt()

        // recyclerView has 5% padding from start
        startPaddingValue?.let {
            similarAlbumsRecyclerView.setPadding(startPaddingValue, 0, 0, 0)

        }
    }

}
