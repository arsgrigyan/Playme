package com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentSearchBinding
import com.southernsunrise.playme.utilities.Helper


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    val binding get() = _binding

    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var genresAndSearchResultsFragmentContainer: Fragment
    private lateinit var searchAppBarLayout: AppBarLayout
    private lateinit var searchViewCloseButton: ImageView
    private lateinit var searchProgressBar: ProgressBar
    var onBackPressedCallback: OnBackPressedCallback? = null

    var searchResultsFragmentToBeClosedOnSearchCloseButtonClick = true

    // var searchResultsFragmentToBeVisibleWhenResumed: Boolean = false


    val TAG = "SearchFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding?.root

        initializeViews()
        setupViews()



        return view
    }


    private fun setupViews() {
        setupSearchView()
        setupSearchViewCloseButton()
        Helper.setViewPaddings(this, listOf(binding!!.nsvSearch))
    }



    private fun initializeViews() {
        genresAndSearchResultsFragmentContainer =
            this.childFragmentManager.findFragmentById(R.id.genres_search_results_container) as Fragment

        searchAppBarLayout = binding!!.searchAppBarLayout
        searchView = binding!!.searchView
        searchProgressBar = binding!!.pbSearchFragment

    }

    private fun setupSearchViewCloseButton() {
        searchViewCloseButton =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchViewCloseButton.setOnClickListener {

            clearSearchQuery()

            if (searchResultsFragmentToBeClosedOnSearchCloseButtonClick) {
                // when search results fragment is visible and searchView is not focused searchResults fragment is closed and genres' is opened on searchViewCloseButton click
                // searchAppBarLayout gets expanded in case user has scrolled search results and collapsed it
                searchAppBarLayoutExpanded(true)
                if (searchResultsFragmentIsVisible()) navigateToGenresFragment()

                // disabling onBackPressedCallback added when searchView focus gets cleared but search results fragment is still visible
                onBackPressedCallback?.let {
                    if (it.isEnabled) it.remove()
                }
                searchResultsFragmentToBeClosedOnSearchCloseButtonClick = false

            }

        }
    }

    private fun clearSearchQuery() {
        searchView.setQuery("", false)
    }


    private fun setupSearchView() {

        searchView.apply {
            setOnQueryTextFocusChangeListener(object : View.OnFocusChangeListener {

                override fun onFocusChange(p0: View?, focused: Boolean) {
                    if (focused) {
                        // when searchView gets focused searchAppBar layout collapses and search results fragment opens
                        searchAppBarLayoutExpanded(false)
                        if (!searchResultsFragmentIsVisible()) {
                            navigateToSearchResultsFragment()
                        }
                        // if searchView is focused search results fragment shouldn't be closed on
                        // searchViewCloseButton click (only searchView query cleared)
                        searchResultsFragmentToBeClosedOnSearchCloseButtonClick = false
                    } else {
                        // when searchView loses focus searchBarLayout gets expanded
                        searchAppBarLayoutExpanded(true)

                        if (searchView.query.isNullOrBlank()) {
                            // when user hasn't typed anything and there's no search result genres fragment gets visible instead
                            // of results fragment when searchView loses focus
                            if (searchResultsFragmentIsVisible()) {
                                navigateToGenresFragment()
                            }

                        } else {
                            // if user has typed something then after losing focus results fragment is still visible but onBackPressedCallBack is added
                            // to navigate user back to the genres fragment when they press the back button
                            searchResultsFragmentToBeClosedOnSearchCloseButtonClick = true
                            onBackPressedCallback = object : OnBackPressedCallback(true) {
                                override fun handleOnBackPressed() {
                                    if (searchResultsFragmentIsVisible()) {
                                        searchViewCloseButton.callOnClick()
                                    }
                                }

                            }
                            requireActivity().onBackPressedDispatcher.addCallback(
                                viewLifecycleOwner,
                                onBackPressedCallback!!
                            )
                        }

                    }
                }

            })


            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                val handler: Handler = Handler()
                override fun onQueryTextChange(newText: String?): Boolean {
                    handler.removeCallbacksAndMessages(null)
                    getSearchResultsFragment()?.searchGreetErrorTextVisible(false)

                    // Search results are cleared every time user types something, also search call is cancelled
                    clearSearchResultsAndCancelCurrentSearchCall()

                    if (newText?.length == 0) {
                        // search greet text visible when there is no query
                        getSearchResultsFragment()?.searchGreetErrorTextVisible(true)
                    } else {
                        try {
                            // when query text is changed previous results are cleared and a new call is done
                            handler.postDelayed(Runnable {
                                doSearchApiCall(newText.toString())

                            }, 400)

                        } catch (e: java.lang.Exception) {
                            Log.e(TAG, e.message.toString())
                        }

                        // search greet text not visible when user types something

                    }

                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    // clearing focus wen submit button is triggered 'cause search request has already been made in onQueryTextChange()
                    searchView.clearFocus()
                    return false
                }


            })

        }
    }


    private fun doSearchApiCall(query: String) {
        try {
            // making the search api call from search results fragment
            getSearchResultsFragment()?.getSearchResults(
                query
            )
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())

        }

    }


    private fun clearSearchResultsAndCancelCurrentSearchCall() {
        if (searchResultsFragmentIsVisible()) {
            val searchResultsFragment =
                genresAndSearchResultsFragmentContainer.childFragmentManager.fragments[0] as SearchResultsFragment
            searchResultsFragment.clearSearchResultsAndCancelCurrentSearchCall(true)
        }
    }

    fun getSearchResultsFragment(): SearchResultsFragment? {
        return if (searchResultsFragmentIsVisible()) {
            genresAndSearchResultsFragmentContainer.childFragmentManager.fragments.get(0) as SearchResultsFragment
        } else null
    }


    private fun navigateToSearchResultsFragment() {
        genresAndSearchResultsFragmentContainer
            .findNavController().navigate(R.id.action_genresFragment_to_searchResultsFragment)
    }

    private fun navigateToGenresFragment() {
        genresAndSearchResultsFragmentContainer
            .findNavController().navigate(R.id.action_searchResultsFragment_to_genresFragment)

    }

    private fun searchAppBarLayoutExpanded(expanded: Boolean) {
        searchAppBarLayout.setExpanded(expanded)
    }

    private fun searchResultsFragmentIsVisible(): Boolean {
        return this.childFragmentManager.findFragmentById(R.id.genres_search_results_container)?.childFragmentManager?.fragments?.get(
            0
        ) is SearchResultsFragment
    }

    fun progressBarVisible(visible: Boolean) {
        searchProgressBar.isVisible = visible
    }


}
