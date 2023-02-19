package com.southernsunrise.playme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.album.AlbumModel
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.artist.ArtistModel
import com.southernsunrise.playme.dataObjectModels.genre.GenreModel
import com.southernsunrise.playme.retrofit.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ArtistDetailsViewModel(val artist: Artist) : ViewModel() {
    private val apiService = RetrofitInstance.apiService

    init {
        getArtistTopReleases()
        getArtistGenres()
        getSimilarArtists()
    }


    private var _artistReleasesList: MutableLiveData<Pair<String, List<Album>>> =
        MutableLiveData()
    private var _artistsGenresList: MutableLiveData<GenreModel> = MutableLiveData()
    private var _similarArtistsList: MutableLiveData<ArtistModel> = MutableLiveData()

    val artistReleasesList: LiveData<Pair<String, List<Album>>> get() = _artistReleasesList
    val artistsGenresList: LiveData<GenreModel> get() = _artistsGenresList
    val similarArtistsList: LiveData<ArtistModel> get() = _similarArtistsList

    private fun getArtistTopReleases() {
        val artistTopAlbumsCall = apiService.getArtistTopAlbums(artist.id)

        artistTopAlbumsCall.enqueue(object : Callback<AlbumModel> {
            override fun onResponse(call: Call<AlbumModel>, response: Response<AlbumModel>) {
                if (response.isSuccessful) {
                    response.body()?.albums?.let {
                        if (it.isNotEmpty() && it.size > 2) {
                            _artistReleasesList.postValue(Pair("Popular Releases", it))
                        }else getArtistNewReleases()
                    }
                }
            }

            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
                getArtistNewReleases()
            }

        })
    }

    fun getArtistNewReleases() {
        val artistNewReleasesCall = apiService.getNewReleasesByArtistId(artist.id)

        artistNewReleasesCall.enqueue(object : Callback<AlbumModel> {
            override fun onResponse(call: Call<AlbumModel>, response: Response<AlbumModel>) {
                if (response.isSuccessful) {
                    response.body()?.albums?.let {
                        if (it.isNotEmpty()) {
                            _artistReleasesList.postValue(Pair("New Releases", it))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
            }

        })
    }

    private fun getArtistGenres() {
        val artistGenresIDsList: List<String> = artist.links.genres.ids
        var genresIDsString: String = ""
        for (genreID in artistGenresIDsList) {
            genresIDsString += if (artistGenresIDsList.indexOf(genreID) != 0) {
                ",$genreID"
            } else genreID
        }

        val artistGenresCall = apiService.getGenreByID(genresIDsString)
        artistGenresCall.enqueue(object : Callback<GenreModel> {
            override fun onResponse(call: Call<GenreModel>, response: Response<GenreModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _artistsGenresList.postValue(it)
                    }
                }
            }

            override fun onFailure(call: Call<GenreModel>, t: Throwable) {

            }

        })

    }


    private fun getSimilarArtists() {

        val getSampleArtistsCall = apiService.getSimilarArtistsOfArtist(artist.id)
        getSampleArtistsCall.enqueue(object : Callback<ArtistModel> {
            override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _similarArtistsList.postValue(it)
                    }
                }
            }

            override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
            }

        })


    }

}