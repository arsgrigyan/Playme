package com.southernsunrise.playme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.southernsunrise.playme.dataObjectModels.genre.GenreModel
import com.southernsunrise.playme.retrofit.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenresFragmentViewModel : ViewModel() {
    val apiService = RetrofitInstance.apiService

    private var _allGenresLiveData: MutableLiveData<GenreModel> = MutableLiveData()
    private var _loadingStateLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val allGenresLiveData: LiveData<GenreModel> get() = _allGenresLiveData
    val loadingStateLiveData: LiveData<Boolean> get() = _loadingStateLiveData


    init {
        loadAllGenres()
    }

    private fun loadAllGenres() {
        val genresListCall = apiService.getAllGenres()
        genresListCall.enqueue(object : Callback<GenreModel> {
            override fun onResponse(call: Call<GenreModel>, response: Response<GenreModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _allGenresLiveData.postValue(it)
                    }
                }
                _loadingStateLiveData.postValue(false)

            }

            override fun onFailure(call: Call<GenreModel>, t: Throwable) {
                _loadingStateLiveData.postValue(false)
            }

        })
    }

}