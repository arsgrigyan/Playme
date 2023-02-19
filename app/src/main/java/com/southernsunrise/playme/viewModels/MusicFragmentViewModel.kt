package com.southernsunrise.playme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.southernsunrise.playme.dataObjectModels.album.AlbumModel
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.artist.ArtistModel
import com.southernsunrise.playme.dataObjectModels.playlist.Playlist
import com.southernsunrise.playme.dataObjectModels.playlist.PlaylistModel
import com.southernsunrise.playme.retrofit.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicFragmentViewModel() : ViewModel() {
    private var _topAlbumsLiveData = MutableLiveData<AlbumModel>()
    private var _topArtistsLiveData = MutableLiveData<ArtistModel>()
    private var _playlistsOfTheDayLiveData = MutableLiveData<PlaylistModel>()
    private var _playlistAndMemberArtistsMapLiveData: MutableLiveData<HashMap<Playlist, List<Artist>>> =
        MutableLiveData()


    val topAlbumsLiveData: LiveData<AlbumModel> get() = _topAlbumsLiveData
    val topArtistsLiveData: LiveData<ArtistModel> get() = _topArtistsLiveData
    val playlistsOfTheDayLiveData: LiveData<PlaylistModel> get() = _playlistsOfTheDayLiveData
    val playlistAndMemberArtistsMapLiveData: LiveData<HashMap<Playlist, List<Artist>>> get() = _playlistAndMemberArtistsMapLiveData


    private var topAlbumsLoading: Boolean = true
    private var topArtistsLoading: Boolean = true
    private var playListsOfTheDayLoading: Boolean = true
    private var _screenStateLoading = MutableLiveData<Boolean>()
    val screenStateLoading get() = _screenStateLoading

    init {
        getTopAlbums()
        getTopArtists()
        getPlaylistsOfTheDay()
    }

    private fun getPlaylistsOfTheDay() {
        val playlistsOfTheDayCall = RetrofitInstance.apiService.getDaysPlaylists()
        playlistsOfTheDayCall.enqueue(object : Callback<PlaylistModel> {
            override fun onResponse(call: Call<PlaylistModel>, response: Response<PlaylistModel>) {

                if (response.isSuccessful) {
                    response.body()?.let {
                        _playlistsOfTheDayLiveData.postValue(it)
                        // getSampleArtistsAndUpdatePlaylists(it)

                    }
                }
                playListsOfTheDayLoading = false
                checkStateLoading()


            }

            override fun onFailure(call: Call<PlaylistModel>, t: Throwable) {
                Log.e("failure:", t.stackTraceToString())
                playListsOfTheDayLoading = false
                checkStateLoading()

            }

        })
    }


    private fun getTopArtists() {
        val topArtistsGetCall = RetrofitInstance.apiService.getTopArtists()
        topArtistsGetCall.enqueue(object : Callback<ArtistModel> {
            override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _topArtistsLiveData.postValue(it)
                    }
                }
                topArtistsLoading = false
                checkStateLoading()
            }

            override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                Log.e("failure:", t.stackTraceToString())
                topArtistsLoading = false
                checkStateLoading()

            }

        })

    }

    private fun getTopAlbums() {
        val topAlbumsGetCall = RetrofitInstance.apiService.getTopAlbums()
        topAlbumsGetCall.enqueue(object : Callback<AlbumModel> {
            override fun onResponse(call: Call<AlbumModel>, response: Response<AlbumModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _topAlbumsLiveData.postValue(it)
                    }
                }
                topAlbumsLoading = false
                checkStateLoading()

            }

            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
                Log.e("failure:", t.stackTraceToString())
                topAlbumsLoading = false
                checkStateLoading()
            }

        })

    }

    fun checkStateLoading() {
        if (!(topAlbumsLoading && topArtistsLoading && playListsOfTheDayLoading)) {
            screenStateLoading.postValue(false)
        } else screenStateLoading.postValue(true)

    }


}

