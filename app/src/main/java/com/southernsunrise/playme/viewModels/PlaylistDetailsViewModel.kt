package com.southernsunrise.playme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.southernsunrise.playme.dataObjectModels.playlist.Playlist
import com.southernsunrise.playme.dataObjectModels.playlist.PlaylistModel
import com.southernsunrise.playme.dataObjectModels.track.TrackModel
import com.southernsunrise.playme.retrofit.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistDetailsViewModel(val playlist: Playlist) : ViewModel() {
    val apiService = RetrofitInstance.apiService

    private var _playlistTracksLiveData: MutableLiveData<TrackModel> = MutableLiveData()
    private var _similarPlaylistsLiveData: MutableLiveData<PlaylistModel> = MutableLiveData()

    val playlistTracksLiveData: LiveData<TrackModel> get() = _playlistTracksLiveData
    val similarPlaylistsLiveData: LiveData<PlaylistModel> get() = _similarPlaylistsLiveData


    init {
        getPlaylistTracks()
        getSimilarPlaylistByTag()
    }

    private fun getPlaylistTracks() {
        val albumTracksCall = apiService.getAlbumPlaylistTracks(objType = "playlists", playlist.id)
        albumTracksCall.enqueue(object : Callback<TrackModel> {
            override fun onResponse(call: Call<TrackModel>, response: Response<TrackModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _playlistTracksLiveData.postValue(it)
                    }
                }

            }

            override fun onFailure(call: Call<TrackModel>, t: Throwable) {}

        })
    }

    fun getSimilarPlaylistByTag() {
        playlist.links?.tags?.ids?.get(0)?.let {
            val similarPlaylistsCall = apiService.getPlaylistsByTag(it)
            similarPlaylistsCall.enqueue(object : Callback<PlaylistModel> {
                override fun onResponse(
                    call: Call<PlaylistModel>,
                    response: Response<PlaylistModel>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { playlistModel ->
                            if (playlistModel.playlists.isNotEmpty()) {
                                _similarPlaylistsLiveData.postValue(playlistModel)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PlaylistModel>, t: Throwable) {}
            })

        }
    }

}