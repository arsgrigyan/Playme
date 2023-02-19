package com.southernsunrise.playme.fragments.browseFragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.southernsunrise.playme.adapters.GridViewAdapter
import com.southernsunrise.playme.adapters.ResponseObjectsRecyclerViewAdapter
import com.southernsunrise.playme.databinding.FragmentPlaylistDetailsBinding
import com.southernsunrise.playme.dataObjectModels.playlist.Playlist
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.services.MusicService
import com.southernsunrise.playme.utilities.Constants
import com.southernsunrise.playme.utilities.Helper
import com.southernsunrise.playme.viewModels.PlaylistDetailsViewModel
import kotlin.math.abs

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding

    private lateinit var backImageButton: ImageButton
    private lateinit var playlistNameToolbarTextView: TextView
    private lateinit var playlistNameTextView: TextView
    private lateinit var playlistDescriptionTextView: TextView
    private lateinit var playlistAuthorPhotoImageView: ImageView
    private lateinit var playlistPhotoImageView: ImageView
    private lateinit var playlistAuthorNameTextView: TextView
    private lateinit var playlistLikesCountTextView: TextView
    private lateinit var playlistTracksArtistsNamesTextView: TextView
    private lateinit var addPlaylistToFavoritesButton: ImageButton
    private lateinit var moreOnPlaylistMenuButton: ImageButton
    private lateinit var playlistPlayImageButton: ImageButton
    private lateinit var similarPlaylistsRecyclerView: RecyclerView
    private lateinit var similarPlaylistsRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter

    private lateinit var playlistAppBarLayout: AppBarLayout
    private lateinit var playlistCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var playlistDetailsConstraintLayout: ConstraintLayout


    private lateinit var similarPlaylistsGridView: GridView
    private lateinit var similarPlaylistsGridViewAdapter: GridViewAdapter

    private lateinit var musicServiceIntent:Intent

    var playlist: Playlist? = null

    private lateinit var playlistDetailsViewModel: PlaylistDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlist = it.getSerializable(Constants.PLAYLIST) as? Playlist
            playlist?.let { it2 ->
                playlistDetailsViewModel = PlaylistDetailsViewModel(it2)
            }


        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        val view = binding?.root

        playlist?.let {
            initializeViews()
            setupViews()
            setupObservers()
        }

        musicServiceIntent = Intent(requireContext(), MusicService::class.java)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        setupPlaylistTracksObserver()
        setupSimilarPlaylistsObserver()
    }

    private fun setupSimilarPlaylistsObserver() {
        playlistDetailsViewModel.similarPlaylistsLiveData.observe(viewLifecycleOwner) {
            val playlists: ArrayList<Playlist> = it.playlists as ArrayList<Playlist>

            // if similar playlists list contains currently displayed playlist, it is removed from similars
            this@PlaylistDetailsFragment.playlist?.let { currentPlaylist ->
                if (playlists.contains(currentPlaylist)) playlists.remove(currentPlaylist)
            }
            for (playlist in playlists) {
                if (playlists.indexOf(playlist) < 6) {
                    updateRecyclerView(similarPlaylistsRecyclerView, playlist)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPlaylistTracksObserver() {
        playlistDetailsViewModel.playlistTracksLiveData.observe(viewLifecycleOwner) {
            val tracksList: List<Track> = it.tracks
            playlistTracksArtistsNamesTextView.text =
                Helper.getArtistAndTrackNamesText(Pair<Any, Any>(playlist!!, tracksList))

            playlistPlayImageButton.setOnClickListener {
                startPlaylistPlayService(tracksList)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startPlaylistPlayService(trackList:List<Track>) {
            musicServiceIntent.putExtra("trackList", trackList as java.io.Serializable)
            requireActivity().startForegroundService(musicServiceIntent)
    }


    private fun initializeViews() {
        binding?.let {
            backImageButton = it.ibBack
            playlistNameToolbarTextView = it.tvToolbarPlaylistName
            playlistNameTextView = it.tvPlaylistName
            playlistDescriptionTextView = it.tvPlaylistDescription
            playlistAuthorPhotoImageView = it.ivPlaylistAuthorPhoto
            playlistPhotoImageView = it.ivPlaylistPhoto
            playlistAuthorNameTextView = it.tvPlaylistAuthorName
            playlistLikesCountTextView = it.tvPlaylistLikeCount
            playlistTracksArtistsNamesTextView = it.tvPlaylistTracksArtistsNames
            addPlaylistToFavoritesButton = it.ibFavorite
            moreOnPlaylistMenuButton = it.ibMenu
            playlistPlayImageButton = it.ibPlaylistPlay
            similarPlaylistsRecyclerView = it.rvSimilarPlaylists

            playlistAppBarLayout = it.playlistAppBarLayout
            playlistCollapsingToolbarLayout = it.ctlPlaylist
            playlistDetailsConstraintLayout = it.clPlaylistDetails

            //similarPlaylistsGridView = it.gvSimilarPlaylists
        }

    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun setupViews() {
        playlist?.let {
            playlistNameToolbarTextView.text = it.name
            playlistNameTextView.text = it.name
            playlistDescriptionTextView.text = it.description.split(".")[0]
            playlistLikesCountTextView.text =
                it.favoriteCount.toString() + " likes • " + it.trackCount + " tracks"

            setupBackButton()
            setupSimilarPlaylistsRecyclerView()
            setupPlaylistImageViewAndViewBackgrounds()
            setOnAppBarLayoutOffsetChangeListener()
            Helper.setViewPaddings(this, listOf(binding!!.nsvPlaylistDetails))
        }

    }


    private fun setupBackButton() {
        backImageButton.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setOnAppBarLayoutOffsetChangeListener() {
        playlistAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            // change toolbar's artist name textView's alpha depending on appBarLayout offset
            playlistNameToolbarTextView.alpha =
                abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
            playlistNameTextView.alpha = (1f - playlistNameToolbarTextView.alpha)

            // scrim drawable gradually gets visible and gone on vertical offset change
            playlistCollapsingToolbarLayout.background.alpha =
                (255 * abs(100 * verticalOffset / appBarLayout.totalScrollRange) / 100)


        })

    }

    private fun setupPlaylistImageViewAndViewBackgrounds() {
        val playlistImageUrl =
            ApiConstants.getImageLink(ApiConstants.PLAYLISTS, playlist!!.id, "1200x400")
        Helper.loadImageIntoImageViewAndSetupViewBackgroundsWithPalette(
            arrayListOf(
                playlistPhotoImageView,
                playlistCollapsingToolbarLayout,
                playlistDetailsConstraintLayout,
                playlistPlayImageButton
            ),
            playlistImageUrl
        )
    }


    private fun setupSimilarPlaylistsRecyclerView() {
        similarPlaylistsRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = true, spanCount = 2)
        similarPlaylistsRecyclerView.adapter = similarPlaylistsRecyclerViewAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecyclerView(recyclerView: RecyclerView, obj: Any) {
        when (recyclerView) {
            similarPlaylistsRecyclerView -> {
                similarPlaylistsRecyclerViewAdapter.responseObjectsList.add(obj as Playlist)
                similarPlaylistsRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }


}