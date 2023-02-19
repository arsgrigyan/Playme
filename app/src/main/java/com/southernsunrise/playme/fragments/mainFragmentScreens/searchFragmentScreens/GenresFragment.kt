package com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.southernsunrise.playme.adapters.ResponseObjectsRecyclerViewAdapter
import com.southernsunrise.playme.databinding.FragmentGenresBinding
import com.southernsunrise.playme.dataObjectModels.genre.Genre
import com.southernsunrise.playme.viewModels.GenresFragmentViewModel


class GenresFragment : Fragment() {

    private lateinit var _binding: FragmentGenresBinding
    val binding get() = _binding

    private lateinit var browseAllGenresTextView: TextView
    private lateinit var genresListRecyclerView: RecyclerView
    private lateinit var genresListRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter

    val TAG = this.javaClass.name
    val genresViewModel: GenresFragmentViewModel by lazy {
        ViewModelProvider(this).get(GenresFragmentViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGenresBinding.inflate(inflater, container, false)
        val view = binding.root
        setupViews()
        setupObservers()


        return view

    }

    private fun setupObservers() {
        setupLoadingStateObserver()
        setupGenresDataObserver()
    }

    private fun setupViews() {
        setupGenresRecyclerView()
        setupBrowseAllGenresTextView()
    }

    private fun setupLoadingStateObserver() {
        genresViewModel.loadingStateLiveData.observe(
            viewLifecycleOwner
        ) {
            searchFragmentProgressBarVisible(it)
        }


    }

    private fun setupGenresDataObserver() {
        genresViewModel.allGenresLiveData.observe(
            viewLifecycleOwner
        ) {
            val genresList: List<Genre> = it.genres
            val db = Firebase.firestore
            val storage = Firebase.storage



            if (genresList.isNotEmpty()) {
                for (genre in genresList) {
                    addGenreToAdapterGenresListAndUpdate(genre)
                    updateBrowseAllTextViewVisibility(true)
                }
            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addGenreToAdapterGenresListAndUpdate(genre: Genre) {
        genresListRecyclerViewAdapter.responseObjectsList.add(genre)
        genresListRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun setupGenresRecyclerView() {
        genresListRecyclerView = binding.rvGenres
        genresListRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = true)
        genresListRecyclerView.adapter = genresListRecyclerViewAdapter

    }

    private fun setupBrowseAllGenresTextView() {
        browseAllGenresTextView = binding.tvBrowseAllGenres
    }

    private fun searchFragmentProgressBarVisible(visible: Boolean) {
        (parentFragment?.parentFragment as SearchFragment).progressBarVisible(visible)
    }

    private fun updateBrowseAllTextViewVisibility(
        visible: Boolean
    ) {
        if (visible) {
            browseAllGenresTextView.isVisible = true
        } else browseAllGenresTextView.isGone = true
    }


}