package com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.southernsunrise.playme.R
import com.southernsunrise.playme.adapters.ResponseObjectsRecyclerViewAdapter
import com.southernsunrise.playme.databinding.FragmentSearchResultsBinding
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.southernsunrise.playme.viewModels.SearchResultsViewModel


class SearchResultsFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultsBinding
    private lateinit var searchResultsListRecyclerView: RecyclerView
    private lateinit var searchResultsListRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter


    private val searchResultsViewModel: SearchResultsViewModel by lazy {
        ViewModelProvider(this).get(SearchResultsViewModel::class.java)
    }

    var searchResultsLoaded: Boolean = false

    val TAG = "SearchResultsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        val view = binding.root

        setupViews()
        setupObservers()

        return view
    }

    private fun setupViews() {
        setupSearchResultsListRecyclerView()
    }

    private fun setupObservers() {
        //  setupSearchResultDataObserver()
        setupErrorObserver()
        setupLoadingStateObserver()
    }

    private fun setupLoadingStateObserver() {
        searchResultsViewModel.screenStateLoading.observe(viewLifecycleOwner) {
            searchFragmentProgressBarVisible(it)

        }
    }

    private fun setupErrorObserver() {
        searchResultsViewModel.searchResultsErrorLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let { searchGreetErrorTextVisible(true, it) }
        }
    }

    private fun setupSearchResultDataObserver() {
        searchResultsViewModel.searchResultLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                clearSearchResultsAndCancelCurrentSearchCall()

                val albumsList: List<Album> =
                    it.search.data.albums
                val artistsList: List<Artist> =
                    it.search.data.artists
                val tracksList: List<Track> =
                    it.search.data.tracks


                val responseIdsInOrder: List<String> = it.search.order

                for (id in responseIdsInOrder) {
                    id.subSequence(0, 3).let { idType ->
                        when (idType) {
                            "alb" -> {
                                for (album in albumsList) {
                                    updateSearchResultsListRecyclerView(album)
                                }
                            }
                            "art" -> {
                                for (artist in artistsList) {
                                    if (artist.id == id) {
                                        updateSearchResultsListRecyclerView(artist)
                                    }
                                }
                            }
                            "tra" -> {
                                for (track in tracksList) {
                                    if (track.id == id) {
                                        updateSearchResultsListRecyclerView(track)
                                    }
                                }
                            }


                        }
                    }
                }
                searchResultsLoaded = true
                searchGreetErrorTextVisible(false)
            }

        }
    }


    private fun setupSearchResultsListRecyclerView() {
        searchResultsListRecyclerView = binding.rvSearchResults
        searchResultsListRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = true)
        searchResultsListRecyclerView.adapter = searchResultsListRecyclerViewAdapter

    }

    fun getSearchResults(query: String) {
        searchResultsViewModel.getSearchResults(query)
        setupSearchResultDataObserver()
        searchResultsViewModel.clearErrorMessage()
    }


    @SuppressLint("NotifyDataSetChanged", "CheckResult")
    fun updateSearchResultsListRecyclerView(searchResultsListItem: Any, add: Boolean = true) {
        // if (dataAdequate(searchResultsListItem)) {
        if (add) {
            searchResultsListRecyclerViewAdapter.responseObjectsList.add(searchResultsListItem)
        } else searchResultsListRecyclerViewAdapter.responseObjectsList.remove(searchResultsListItem)
        searchResultsListRecyclerViewAdapter.notifyDataSetChanged()
        // }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun clearSearchResultsAndCancelCurrentSearchCall(cancelCurrentCall: Boolean = false) {
        if (searchResultsLoaded) {
            searchResultsListRecyclerViewAdapter.responseObjectsList.clear()
            searchResultsListRecyclerViewAdapter.notifyDataSetChanged()
            searchResultsLoaded = false
        }
        if (cancelCurrentCall) {
            searchResultsViewModel.cancelCurrentCall()

        }
    }

    fun searchGreetErrorTextVisible(visible: Boolean, errorMsg: Pair<String, String?>? = null) {
        if (visible) {

            if (errorMsg != null) {
                binding.tv1.text = errorMsg.first
                errorMsg.second?.let { binding.tv2.text = it }

            } else {
                binding.tv1.text = requireContext().getString(R.string.search_text_1)
                binding.tv2.text = requireContext().getString(R.string.search_text_2)
            }
            binding.clGreetSearchErrorTexts.isVisible = true

        } else binding.clGreetSearchErrorTexts.isGone = true
    }


    private fun searchFragmentProgressBarVisible(visible: Boolean) {
        (parentFragment?.parentFragment as SearchFragment).progressBarVisible(
            visible
        )
    }


    override fun onPause() {
        super.onPause()
        searchFragmentProgressBarVisible(false)
    }

}