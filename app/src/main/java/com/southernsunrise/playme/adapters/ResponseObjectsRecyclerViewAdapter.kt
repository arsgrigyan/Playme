package com.southernsunrise.playme.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.southernsunrise.playme.R
import com.southernsunrise.playme.fragments.mainFragmentScreens.*
import com.southernsunrise.playme.fragments.mainFragmentScreens.homeFragmentScreens.*
import com.southernsunrise.playme.fragments.mainFragmentScreens.libraryFragmentScreens.LibraryFragmentContainer
import com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens.SearchFragment
import com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens.SearchFragmentContainer
import com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens.SearchResultsFragment
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.genre.Genre
import com.southernsunrise.playme.dataObjectModels.playlist.Playlist
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.southernsunrise.playme.fragments.browseFragments.AlbumDetailsFragment
import com.southernsunrise.playme.fragments.browseFragments.ArtistDetailsFragment
import com.southernsunrise.playme.fragments.browseFragments.PlaylistDetailsFragment
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.utilities.Constants
import com.southernsunrise.playme.utilities.Constants.ARTIST
import com.southernsunrise.playme.utilities.Constants.PLAYLIST
import com.southernsunrise.playme.utilities.Constants.TRACK
import com.southernsunrise.playme.utilities.Helper
import com.squareup.picasso.Picasso

class ResponseObjectsRecyclerViewAdapter(
    val fragment: Fragment,
    val responseObjectsList: ArrayList<Any> = ArrayList(),
    val isOrientationVertical: Boolean,
    val spanCount: Int = 1
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return responseObjectsList.size
    }

    override fun getItemViewType(position: Int): Int {


        return when (responseObjectsList[position]) {
            is Album -> 1

            is Playlist -> 2

            is Artist -> 3

            is Genre -> 4

            is Track -> 5

            else -> 88

        }

    }


    class AlbumViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val albumCoverPhotoImageView: ImageView =
            ItemView.findViewById(R.id.iv_album_playlist_photo)
        val albumNameTextView: TextView = ItemView.findViewById(R.id.tv_album_playlist_name)
        val albumDateNameTextView: TextView =
            ItemView.findViewById(R.id.tv_album_date_playlist_artists_name)

    }

    class PlaylistViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val playlistPhotoImageView: ImageView =
            ItemView.findViewById(R.id.iv_album_playlist_photo)
        val playlistNameTextView: TextView = ItemView.findViewById(R.id.tv_album_playlist_name)
        val playlistArtistsNameTextView: TextView =
            ItemView.findViewById(R.id.tv_album_date_playlist_artists_name)

    }

    class ArtistViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val artistPhotoImageView: ImageView = ItemView.findViewById(R.id.iv_artist_photo)
        val artistNameTextView: TextView = ItemView.findViewById(R.id.tv_artist_name)
    }

    class GenreViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val genreTitleTextView: TextView = itemView.findViewById(R.id.tv_genre_title)
        val genreImageImageView: ImageView = itemView.findViewById(R.id.iv_genre_img)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {// Album
                val view = if (isOrientationVertical) {
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_album_vertical_list_listitem, parent, false)
                } else LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_album_horizontal_list_listitem, parent, false)
                AlbumViewHolder(view)
            }

            2 -> { // playlist
                val view = if (isOrientationVertical) {
                    if (spanCount == 1) {
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.layout_artist_vertical_list_listitem, parent, false)
                    } else LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_album_playlist_grid_listitem, parent, false)
                } else LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_album_horizontal_list_listitem, parent, false)
                PlaylistViewHolder(view)

            }

            3 -> { // artist
                val view = if (isOrientationVertical) {
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_artist_vertical_list_listitem, parent, false)
                } else LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_artist_horizontal_list_listitem, parent, false)
                // AlbumPlaylistHorizontalViewHolder
                ArtistViewHolder(view)
            }

            4 -> { // Genre
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.genres_list_listitem, parent, false)
                return GenreViewHolder(view)
            }

            else -> { // Track
                // track list item is the same as the album and playlists'
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_album_vertical_list_listitem, parent, false)

                AlbumViewHolder(view)
            }


        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listObj: Any = responseObjectsList[position]
        setupItemView(holder, position, listObj)

    }

    @SuppressLint("SetTextI18n")
    private fun setupItemView(holder: RecyclerView.ViewHolder, position: Int, listObj: Any) {
        val listObject: Any = listObj

        when (holder.itemViewType) {
            1 -> { // album
                holder as AlbumViewHolder
                val album = listObject as Album
                val albumCoverImageView = holder.albumCoverPhotoImageView
                val albumTitleTextView = holder.albumNameTextView
                val albumReleaseDateTextView = holder.albumDateNameTextView
                album.let {
                    loadObjPhoto(it, albumCoverImageView)
                    albumTitleTextView.text = it.name
                    albumReleaseDateTextView.text =
                            /* it.originallyReleased.subSequence(0, 4)
                                 .toString()*/if (it.isSingle) {
                        "Single" + " • " + it.artistName
                    } else "Album" + " • " + it.artistName
                }

                // setListItemViewOnTouchListener(itemView, listObj)

            }

            2 -> { //playlist
                holder as PlaylistViewHolder
                val playlist = listObj as Playlist
                val sampleArtistsList: List<Artist>? = playlist.sampleArtistObjects

                val playlistPhotoImageView: ImageView = holder.playlistPhotoImageView
                val playlistNameTextView: TextView = holder.playlistNameTextView
                val playlistSongsArtistsNameTextView: TextView =
                    holder.playlistArtistsNameTextView

                if (spanCount != 1) {
                    playlistSongsArtistsNameTextView.isGone = true
                }


                playlist.let {
                    loadObjPhoto(it, playlistPhotoImageView)
                    playlistNameTextView.text = it.name
                }

                val artistsNamesString: StringBuilder = StringBuilder()

                sampleArtistsList?.let {
                    for (artist in it) {
                        if (it.indexOf(artist) != 0) {
                            artistsNamesString.append(", ${artist.name}")
                        } else artistsNamesString.append(artist.name)
                    }
                } ?: run {
                    // artistsNamesString.append()
                }
                playlistSongsArtistsNameTextView.text = artistsNamesString.toString()


            }

            3 -> { // artist
                holder as ArtistViewHolder
                listObj as Artist
                val artistNameTextView = holder.artistNameTextView
                val artistPhotoImageView = holder.artistPhotoImageView


                artistPhotoImageView.apply {
                    clipToOutline = true
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    loadObjPhoto(listObj, artistPhotoImageView)
                }
                artistNameTextView.text = if (listObj.id != "art.0") {
                    listObj.name
                } else "Various Artists"

            }

            4 -> { // Genre
                holder as GenreViewHolder
                listObj as Genre

                val genreTitleTextView = holder.genreTitleTextView
                val genreImageImageView = holder.genreImageImageView

                listObj.let {
                    genreTitleTextView.text = it.name
                    loadObjPhoto(listObj, genreImageImageView)
                }


            }

            5 -> { // Track
                holder as AlbumViewHolder
                listObj as Track
                val trackCoverImageView = holder.albumCoverPhotoImageView
                val trackNameTextView = holder.albumNameTextView
                val trackArtistNameTextView = holder.albumDateNameTextView

                listObj.let {
                    trackNameTextView.text = it.name
                    trackArtistNameTextView.text = "Song • ${it.artistName}"
                }
                loadObjPhoto(listObj, trackCoverImageView)

            }


        }
        holder.itemView.setOnClickListener {
            onItemViewClickListener(listObject)
        }

    }


    private fun onItemViewClickListener(listObj: Any) {
        listObj.let {
            when (it) {
                is Album -> {
                    navigateToAlbumDetailsFragment(it)
                }
                is Artist -> {
                    if (it.id != "art.0")
                        navigateToArtistDetailsFragment(it)
                }
                is Playlist -> {
                    navigateToPlaylistDetailsFragment(it)
                }
                is Track -> {
                    navigateToPlayerFragment(it)
                }
            }
        }
    }


    private fun loadObjPhoto(listObj: Any, imageView: ImageView) {
        var defaultImageResource: Int = 0
        var width: Int = 300
        var height: Int = 300
        var objType: String = ""
        var objID: String = ""
        var imageLink: String = ""
        when (listObj) {
            is Playlist -> {
                objType = ApiConstants.PLAYLISTS
                objID = listObj.id
                defaultImageResource = R.drawable.img_default_track_listitem
            }
            is Album -> {
                objType = ApiConstants.ALBUMS
                objID = listObj.id
                defaultImageResource = R.drawable.img_default_album_listitem

            }
            is Artist -> {
                objType = ApiConstants.ARTISTS
                objID = listObj.id
                defaultImageResource = R.drawable.img_deafault_artist_listitem
            }
            is Track -> {
                objType = ApiConstants.ALBUMS
                objID = listObj.albumId
                defaultImageResource = R.drawable.img_default_track_listitem
            }
            is Genre -> {
                objType = ApiConstants.GENRES
                width = 800
                objID = listObj.name
                height = 420
                defaultImageResource = R.color.transparent
            }
        }
        if (listObj !is Genre) {
            imageLink = ApiConstants.getImageLink(objType, objID, "${width}x${height}")
            Picasso.get().load(imageLink)
                .placeholder(defaultImageResource)
                .error(defaultImageResource)
                .resize(width, height)
                .centerCrop()
                .into(imageView)

        } else {
            Helper.loadGenreImage(listObj.name, imageView)

        }



    }



    private fun navigateToPlaylistDetailsFragment(playlist: Playlist) {

        val playlistBundle = Bundle()
        playlistBundle.putSerializable(PLAYLIST, playlist)

        val playlistDetailsFragment: PlaylistDetailsFragment = PlaylistDetailsFragment()
        playlistDetailsFragment.arguments = playlistBundle

        val containerFragment = fragment.parentFragment?.parentFragment

        when (fragment) {
            is MusicFragment -> (fragment.parentFragment?.parentFragment?.parentFragment?.parentFragment as HomeFragmentContainer).navigateTo(
                R.id.home_playlistDetailsFragment,
                playlistBundle
            )

            is SearchResultsFragment -> {

                // navigate to another ArtistDetailsFragment instance from current ArtistDetailsFragment
                val searchFragment = fragment.parentFragment?.parentFragment as SearchFragment
                //  searchFragment.searchResultsFragmentToBeVisibleWhenResumed(true)
                //  searchFragment.findNavController().navigate(R.id.action_searchFragment_to_artistDetailsFragment, artistBundle)


                (searchFragment.parentFragment?.parentFragment as SearchFragmentContainer).navigateTo(
                    R.id.search_playlistDetailsFragment,
                    playlistBundle
                )


            }

            is ArtistDetailsFragment -> {

                when (containerFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.home_playlistDetailsFragment,
                            playlistBundle
                        )
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.search_playlistDetailsFragment,
                            playlistBundle
                        )

                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.library_playlistDetailsFragment,
                            playlistBundle
                        )

                    }
                }

            }

            is AlbumDetailsFragment -> {

                when (containerFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.home_playlistDetailsFragment,
                            playlistBundle
                        )
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.search_playlistDetailsFragment,
                            playlistBundle
                        )

                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.library_playlistDetailsFragment,
                            playlistBundle
                        )

                    }
                }

            }

            is PlaylistDetailsFragment -> {

                when (containerFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.home_playlistDetailsFragment,
                            playlistBundle
                        )
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.search_playlistDetailsFragment,
                            playlistBundle
                        )

                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.library_playlistDetailsFragment,
                            playlistBundle
                        )

                    }
                }

            }


        }


    }

    private fun navigateToArtistDetailsFragment(artist: Artist) {
        val artistBundle = Bundle()
        artistBundle.putSerializable(ARTIST, artist)

        val artistDetailsFragment: ArtistDetailsFragment = ArtistDetailsFragment()
        artistDetailsFragment.arguments = artistBundle


        when (fragment) {
            is MusicFragment -> (fragment.parentFragment?.parentFragment?.parentFragment?.parentFragment as HomeFragmentContainer).navigateTo(
                R.id.home_artistDetailsFragment,
                artistBundle
            )


            is SearchResultsFragment -> {

                // navigate to another ArtistDetailsFragment instance from current ArtistDetailsFragment
                val searchFragment = fragment.parentFragment?.parentFragment as SearchFragment

                (searchFragment.parentFragment?.parentFragment as SearchFragmentContainer).navigateTo(
                    R.id.search_artistDetailsFragment,
                    artistBundle
                )


            }

            is ArtistDetailsFragment -> {

                when (val containerFragment = fragment.parentFragment?.parentFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(R.id.home_artistDetailsFragment, artistBundle)
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.search_artistDetailsFragment,
                            artistBundle
                        )

                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.library_artistDetailsFragment,
                            artistBundle
                        )

                    }
                }

                // navigate to another ArtistDetailsFragment instance from current ArtistDetailsFragment
                /*  (fragment.parentFragment?.parentFragment as MainFragment).navigateTo(
                      artistBundle,
                      R.id.artistDetailsFragment
                  )

                 */
            }

            is AlbumDetailsFragment -> {

                when (val containerFragment = fragment.parentFragment?.parentFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(R.id.home_artistDetailsFragment, artistBundle)
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.search_artistDetailsFragment,
                            artistBundle
                        )

                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(
                            R.id.library_artistDetailsFragment,
                            artistBundle
                        )

                    }
                }

            }


        }

    }

    private fun navigateToAlbumDetailsFragment(album: Album) {
        val albumBundle = Bundle().apply {
            putSerializable(Constants.ALBUM, album)
        }
        val albumDetailsFragment: AlbumDetailsFragment = AlbumDetailsFragment()
        albumDetailsFragment.arguments = albumBundle

        when (fragment) {

            is MusicFragment -> (fragment.parentFragment?.parentFragment?.parentFragment?.parentFragment as HomeFragmentContainer).navigateTo(
                R.id.home_albumDetailsFragment,
                albumBundle
            )

            is ArtistDetailsFragment -> {

                when (val containerFragment = fragment.parentFragment?.parentFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(R.id.home_albumDetailsFragment, albumBundle)
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(R.id.search_albumDetailsFragment, albumBundle)
                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(R.id.home_albumDetailsFragment, albumBundle)
                    }
                }

            }

            is AlbumDetailsFragment -> {

                when (val containerFragment = fragment.parentFragment?.parentFragment) {
                    is HomeFragmentContainer -> {
                        containerFragment.navigateTo(R.id.home_albumDetailsFragment, albumBundle)
                    }
                    is SearchFragmentContainer -> {
                        containerFragment.navigateTo(R.id.search_albumDetailsFragment, albumBundle)
                    }
                    is LibraryFragmentContainer -> {
                        containerFragment.navigateTo(R.id.home_albumDetailsFragment, albumBundle)
                    }
                }

            }

            is SearchResultsFragment -> {

                val searchFragment = fragment.parentFragment?.parentFragment as SearchFragment

                (searchFragment.parentFragment?.parentFragment as SearchFragmentContainer).navigateTo(
                    R.id.search_albumDetailsFragment,
                    albumBundle
                )


            }
        }


    }

    private fun navigateToPlayerFragment(track: Track) {
        val trackBundle: Bundle = Bundle()
        trackBundle.putSerializable(TRACK, track)

        val playerFragment = PlayerFragment()
        playerFragment.arguments = trackBundle

        when (fragment) {
            is SearchResultsFragment -> {
                val mainFragment =
                    fragment.parentFragment?.parentFragment?.parentFragment?.parentFragment?.parentFragment?.parentFragment as MainFragment
                mainFragment.findNavController()
                    .navigate(R.id.action_mainFragment_to_playerFragment, trackBundle)
            }
        }
    }

}


