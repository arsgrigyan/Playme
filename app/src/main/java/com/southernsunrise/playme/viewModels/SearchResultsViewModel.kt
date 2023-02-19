package com.southernsunrise.playme.viewModels

import androidx.lifecycle.*
import com.southernsunrise.playme.dataObjectModels.searchResult.SearchResultModel
import com.southernsunrise.playme.retrofit.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultsViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiService
    private var _searchResultLiveData: MutableLiveData<SearchResultModel?> = MutableLiveData()
    val searchResultLiveData: LiveData<SearchResultModel?> get() = _searchResultLiveData

    private var _searchResultsErrorLiveData: MutableLiveData<Pair<String, String?>?> =
        MutableLiveData()
    val searchResultsErrorLiveData: LiveData<Pair<String, String?>?> get() = _searchResultsErrorLiveData


    private var _searchStateLoading = MutableLiveData<Boolean>()
    val screenStateLoading: LiveData<Boolean> get() = _searchStateLoading



    private var searchResultsCall: Call<SearchResultModel>? = null
    private var searchCallCancelled: Boolean = false


    fun getSearchResults(q: String) {
        searchCallCancelled = false
        _searchStateLoading.postValue(true)
        _searchResultLiveData = MutableLiveData()

        searchResultsCall = apiService.getSearchResults(query = q)
        searchResultsCall?.enqueue(object : Callback<SearchResultModel> {
            override fun onResponse(
                call: Call<SearchResultModel>,
                response: Response<SearchResultModel>
            ) {
                _searchStateLoading.postValue(false)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _searchResultLiveData.postValue(it)
                       // _searchResultsErrorLiveData.postValue(null)

                        if (it.meta.returnedCount == 0) { // there is no response for the query
                            postErrorMsg(
                                "Could not find '${q}'",
                                "Try searching again using a different spelling or keyword."
                            )
                        }
                    }
                } else { // something is wrong with api response
                    postErrorMsg("Something went wrong", "Make sure you are connected to internet")
                }
            }

            override fun onFailure(call: Call<SearchResultModel>, t: Throwable) {
                _searchStateLoading.postValue(false)
                if (!searchCallCancelled) {
                    postErrorMsg(t.message.toString(), null)
                }
            }

        })
    }

    fun postErrorMsg(errorMessage: String, subMessage: String?) {
        _searchResultsErrorLiveData.postValue(
            Pair(
                errorMessage,
                subMessage
            )
        )
    }
    fun clearErrorMessage(){
        _searchResultsErrorLiveData.postValue(null)
    }

    fun cancelCurrentCall() {
        searchResultsCall?.cancel()
        searchCallCancelled = true
    }


}