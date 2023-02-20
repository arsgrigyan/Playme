package com.southernsunrise.playme.fragments.mainFragmentScreens.homeFragmentScreens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.southernsunrise.playme.adapters.ResponseObjectsRecyclerViewAdapter
import com.southernsunrise.playme.databinding.FragmentMusicBinding
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.album.AlbumModel
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.artist.ArtistModel
import com.southernsunrise.playme.dataObjectModels.playlist.Playlist
import com.southernsunrise.playme.utilities.Helper
import com.southernsunrise.playme.viewModels.MusicFragmentViewModel


class MusicFragment : Fragment() {

    private lateinit var _binding: FragmentMusicBinding
    private val binding get() = _binding

    private lateinit var suggestedArtistsListRecyclerView: RecyclerView
    private lateinit var popularAlbumsListRecyclerView: RecyclerView
    private lateinit var playlistsOfTheDayRecyclerView: RecyclerView

    private lateinit var playlistsOfTheDayRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter
    private lateinit var suggestedArtistsListRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter
    private lateinit var popularAlbumsListRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter

    private val musicFragmentViewModel: MusicFragmentViewModel by lazy {
        ViewModelProvider(this).get(MusicFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        setupViews()
        setupObservers()


        return view
    }


    private fun setupViews() {
        setupSuggestedArtistsListRecyclerView()
        setupSuggestedTodaySongsListRecyclerView()
        setupPlaylistsOfTheDayRecyclerView()

        setViewPaddings()
    }

    private fun setViewPaddings() {
        Helper.setViewPaddings(
            this,
            listOf(
                suggestedArtistsListRecyclerView,
                popularAlbumsListRecyclerView,
                playlistsOfTheDayRecyclerView,
                binding.tvSuggestedArtists,
                binding.tvTopAlbums,
                binding.tvDaysPlaylists
            )
        )

    }

    private fun setupObservers() {

        setupTopArtistsListObserver()
        setupTopAlbumsListObserver()
        setupPlaylistsOfTheDayListObserver()
        setupScreenStateLoadingObserver()


    }


    private fun setupScreenStateLoadingObserver() {
        musicFragmentViewModel.screenStateLoading.observe(viewLifecycleOwner) {
            val homeFragment = parentFragment?.parentFragment as HomeFragment
            homeFragment.apply {
                showLoadingScreen(
                    it, it
                )

            }

        }
    }


    private fun setupTopArtistsListObserver() {
        musicFragmentViewModel.topArtistsLiveData.observe(
            viewLifecycleOwner
        ) {
            loadTopArtistsRecyclerView(it)
        }
    }


    private fun setupTopAlbumsListObserver() {
        musicFragmentViewModel.topAlbumsLiveData.observe(
            viewLifecycleOwner
        ) {
            loadAlbumRecyclerView(it)
        }

    }

    private fun setupPlaylistsOfTheDayListObserver() {
        musicFragmentViewModel.playlistsOfTheDayLiveData.observe(
            viewLifecycleOwner
        ) {
            val playlists = it.playlists
            if (playlists.isNotEmpty()) {
                for (playlist in playlists) {
                    addPlaylistToPlaylistsAdapterAndUpdate(playlist)
                }
            }

        }
    }


    private fun loadTopArtistsRecyclerView(artistModel: ArtistModel) {
        val artistsList = artistModel.artists
        if (artistsList.isNotEmpty()) {
            for (artist in artistsList) {
                addArtistToArtistListAdapterAndUpdate(artist)
            }
        }
    }


    private fun loadAlbumRecyclerView(albumModel: AlbumModel?) {
        albumModel?.let {
            val albumsList = albumModel.albums
            if (albumsList.isNotEmpty()) {
                for (album in albumsList) {
                    addAlbumToAlbumListAdapterAndUpdate(album)
                }
            }
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun addPlaylistToPlaylistsAdapterAndUpdate(
        playlist: Playlist
    ) {
        playlistsOfTheDayRecyclerViewAdapter.responseObjectsList.add(playlist)
        playlistsOfTheDayRecyclerViewAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addAlbumToAlbumListAdapterAndUpdate(album: Album) {
        popularAlbumsListRecyclerViewAdapter.responseObjectsList.add(album)
        popularAlbumsListRecyclerViewAdapter.notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addArtistToArtistListAdapterAndUpdate(artist: Artist) {
        suggestedArtistsListRecyclerViewAdapter.responseObjectsList.add(artist)
        suggestedArtistsListRecyclerViewAdapter.notifyDataSetChanged()
    }


    private fun setupSuggestedTodaySongsListRecyclerView() {
        popularAlbumsListRecyclerView = binding.rvPopularAlbums
        popularAlbumsListRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = false)
        popularAlbumsListRecyclerView.adapter = popularAlbumsListRecyclerViewAdapter
    }

    private fun setupSuggestedArtistsListRecyclerView() {
        suggestedArtistsListRecyclerView = binding.rvSuggestedArtists
        suggestedArtistsListRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = false)
        suggestedArtistsListRecyclerView.adapter = suggestedArtistsListRecyclerViewAdapter
    }

    private fun setupPlaylistsOfTheDayRecyclerView() {
        playlistsOfTheDayRecyclerView = binding.rvDaysPlaylists
        playlistsOfTheDayRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = false)
        playlistsOfTheDayRecyclerView.adapter = playlistsOfTheDayRecyclerViewAdapter
    }


}