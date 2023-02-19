package com.southernsunrise.playme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.album.AlbumModel
import com.southernsunrise.playme.dataObjectModels.artist.ArtistModel
import com.southernsunrise.playme.dataObjectModels.track.TrackModel
import com.southernsunrise.playme.retrofit.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumDetailsViewModel(val album: Album) : ViewModel() {
    val apiService = RetrofitInstance.apiService

    private var _albumTracksLiveData: MutableLiveData<TrackModel> = MutableLiveData()
    private var _contributingArtistsLiveData: MutableLiveData<ArtistModel> = MutableLiveData()
    private var _similarAlbumsLiveData: MutableLiveData<AlbumModel> = MutableLiveData()

    val albumTracksLiveData: LiveData<TrackModel> get() = _albumTracksLiveData
    val contributingArtistsLiveData: LiveData<ArtistModel> get() = _contributingArtistsLiveData
    val similarAlbumsLiveData: LiveData<AlbumModel> get() = _similarAlbumsLiveData

    init {
        getAlbumTracks()
        getContributingArtists()
        getSimilarAlbums()
    }

    private fun getAlbumTracks() {
        val albumTracksCall = apiService.getAlbumPlaylistTracks(objType = "albums", album.id)
        albumTracksCall.enqueue(object : Callback<TrackModel> {
            override fun onResponse(call: Call<TrackModel>, response: Response<TrackModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _albumTracksLiveData.postValue(it)
                    }
                }

            }

            override fun onFailure(call: Call<TrackModel>, t: Throwable) {
            }

        })
    }

    private fun getContributingArtists() {
        val contributingArtists = album.contributingArtists
        contributingArtists?.let {
            var artistsIdString: String = contributingArtists.primaryArtist.toString()
            if (it.guestMusician != null) {
                artistsIdString += "," + it.guestMusician
            }
            if (it.featuredPerformer != null) {
                artistsIdString += "," + it.featuredPerformer
            }

            val contributingArtistsCall = apiService.getArtistById(artistsIdString)
            contributingArtistsCall.enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.artists.isNotEmpty()) {
                                _contributingArtistsLiveData.postValue(it)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                }
            })
        }


    }


    private fun getSimilarAlbums() {
        val similarAlbumsCall = apiService.getSimilarAlbumsByAlbumID(album.id)
        similarAlbumsCall.enqueue(object : Callback<AlbumModel> {
            override fun onResponse(call: Call<AlbumModel>, response: Response<AlbumModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {

                        if (it.albums.isNotEmpty() && it.albums.size != 1) {
                            _similarAlbumsLiveData.postValue(it)
                        } else getAlbumArtistsNewReleases()


                    }

                }
            }

            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
            }
        })
    }

    private fun getAlbumArtistsNewReleases() {
        val newReleasesCall =
            apiService.getNewReleasesByArtistId(album.contributingArtists?.primaryArtist!!)
        newReleasesCall.enqueue(object : Callback<AlbumModel> {
            override fun onResponse(call: Call<AlbumModel>, response: Response<AlbumModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {

                        if (it.albums.isNotEmpty()) {
                            _similarAlbumsLiveData.postValue(it)
                        }

                    }

                }
            }

            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
            }
        })
    }



}