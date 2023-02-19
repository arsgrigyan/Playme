package com.southernsunrise.playme.fragments.browseFragments

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.adapters.ResponseObjectsRecyclerViewAdapter
import com.southernsunrise.playme.databinding.FragmentArtistDetailsBinding
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.utilities.Constants
import com.southernsunrise.playme.utilities.Helper
import com.southernsunrise.playme.viewModels.ArtistDetailsViewModel
import java.io.Serializable
import kotlin.math.abs


class ArtistDetailsFragment : Fragment() {

    private lateinit var binding: FragmentArtistDetailsBinding
    private lateinit var artistPhotoImageView: ImageView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var playImageButton: ImageButton
    private lateinit var artistNameToolbarTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var artistGenresTextView: TextView

    private lateinit var backToolbarImageButton: ImageButton
    private lateinit var artistPopularAlbumsRecyclerView: RecyclerView
    private lateinit var similarArtistRecyclerView: RecyclerView
    private lateinit var similarArtistRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter
    private lateinit var artistPopularAlbumsRecyclerViewAdapter: ResponseObjectsRecyclerViewAdapter
    private lateinit var artistCollapsingToolbarLayout: com.google.android.material.appbar.CollapsingToolbarLayout
    private lateinit var artistDetailsConstraintLayout: ConstraintLayout

    private lateinit var artist: Artist
    private lateinit var artistDetailsViewModel: ArtistDetailsViewModel


    val TAG = "ArtistDetailsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (!it.isEmpty) {
                val artistObj: Serializable? = it.getSerializable(Constants.ARTIST)
                artist = artistObj as Artist
                artistDetailsViewModel = ArtistDetailsViewModel(artist)

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArtistDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        initializeViews()
        setupViews()
        setupDataObservers()

        return view
    }

    private fun initializeViews() {
        appBarLayout = binding.artistAppBar
        artistCollapsingToolbarLayout = binding.ctlArtist
        artistDetailsConstraintLayout = binding.clArtistDetailsPlayBtn

        artistNameTextView = binding.tvArtistName
        artistNameToolbarTextView = binding.tvToolbarArtistName
        artistGenresTextView = binding.tvArtistGenres

        playImageButton = binding.ibArtistPlay
        backToolbarImageButton = binding.ibBackToolbar

        artistPhotoImageView = binding.ivArtistPhoto

        artistPopularAlbumsRecyclerView = binding.rvArtistPopularAlbum
        similarArtistRecyclerView = binding.rvSimilarArtists

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupViews() {
        artistNameTextView.text = getArtistName()
        artistNameToolbarTextView.text = getArtistName()
        setupTopAlbumsRecyclerView()
        setupSimilarArtistsRecyclerView()
        setupAristPhotoImageView()
        setupBackButton()
        setupCollapsingToolbarLayoutRatio()
        setOnAppBarLayoutOffsetChangeListener()
        Helper.setViewPaddings(this, listOf(similarArtistRecyclerView, binding.nsvArtistDetails))
    }

    private fun setupBackButton() {
        backToolbarImageButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupAristPhotoImageView() {

        val imageSize: String = "500x500"
        val artistPhotoUrl = ApiConstants.getImageLink(
            ApiConstants.ARTISTS,
            getArtistID(), imageSize
        )

        Helper.loadImageIntoImageViewAndSetupViewBackgroundsWithPalette(
            arrayListOf(
                artistPhotoImageView,
                artistDetailsConstraintLayout,
                playImageButton,
                artistCollapsingToolbarLayout
            ),
            artistPhotoUrl
        )


    }

    private fun setupSimilarArtistsRecyclerView() {
        similarArtistRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(
                this@ArtistDetailsFragment,
                isOrientationVertical = false
            )
        similarArtistRecyclerView.adapter = similarArtistRecyclerViewAdapter
    }

    private fun setupTopAlbumsRecyclerView() {
        //   artistPopularAlbumsRecyclerView.itemAnimator = null
        artistPopularAlbumsRecyclerViewAdapter =
            ResponseObjectsRecyclerViewAdapter(this, isOrientationVertical = true)
        artistPopularAlbumsRecyclerView.adapter = artistPopularAlbumsRecyclerViewAdapter
    }

    private fun setupCollapsingToolbarLayoutRatio() {
        // ImageView containing artist photo is also visible from behind of the status bar
        // collapsing toolbar layout should only be visible below the status bar

        val layout: ConstraintLayout = binding.clCollapsingToolbar
        val statusBarParams: Pair<Int?, Int> =
            (requireActivity() as MainActivity).getStatusBarHeight()
        val statusBarWidth = statusBarParams.first
        val statusBarHeight = statusBarParams.second

        //  artistImageView dimen. ratio is "4:3" by default,
        // so (width(=statusBarWidth)/height) = 4/3
        val expectedImageViewHeight = statusBarWidth?.times((3.0 / 4.0))

        // set collapsing toolbar layout's dimen ratio
        (layout.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
            "${statusBarWidth}:${
                expectedImageViewHeight?.minus(
                    statusBarHeight
                )
            }"


    }

    private fun setupDataObservers() {
        setupArtistReleasesObserver()
        setupSimilarArtistsObserver()
        setupArtistsGenresObserver()


    }

    private fun setupArtistReleasesObserver() {
        artistDetailsViewModel.artistReleasesList.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                if (it.second.isNotEmpty()) {
                    binding.clArtistReleases.isVisible = true
                    binding.tvArtistReleases.text = it.first
                    for (album in it.second) {
                        updateArtistTopAlbumsRecyclerView(album)
                    }
                }
            }
        }
    }

    private fun setupSimilarArtistsObserver() {
        artistDetailsViewModel.similarArtistsList.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val similarArtistsList = it.artists
                if (similarArtistsList.isNotEmpty()) {
                    binding.clSimilarArtists.isVisible = true
                    for (artist in similarArtistsList) {
                        updateSimilarArtistsRecyclerView(artist)
                    }

                }

            }
        }
    }

    private fun setupArtistsGenresObserver() {
        artistDetailsViewModel.artistsGenresList.observe(
            viewLifecycleOwner,
        ) {
            it?.let {
                val genresList = it.genres
                for (index in genresList.indices) {
                    // taking first two genres to add to the textView
                    if (index < 2) {
                        artistGenresTextView.apply {
                            text = if (text.toString().isEmpty()) {
                                genresList[index].name
                            } else "$text • ${genresList[index].name}"
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateArtistTopAlbumsRecyclerView(album: Album) {
        artistPopularAlbumsRecyclerViewAdapter.responseObjectsList.add(album)
        artistPopularAlbumsRecyclerViewAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSimilarArtistsRecyclerView(artist: Artist) {
        similarArtistRecyclerViewAdapter.responseObjectsList.add(artist)
        similarArtistRecyclerViewAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setOnAppBarLayoutOffsetChangeListener() {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            // change toolbar's artist name textView's alpha depending on appBarLayout offset
            artistNameToolbarTextView.alpha =
                abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
            artistNameTextView.alpha = (1f - artistNameToolbarTextView.alpha)

            // scrim drawable gradually gets visible and gone on vertical offset change
            artistCollapsingToolbarLayout.background?.let {
                it.alpha =
                    (255 * abs(100 * verticalOffset / appBarLayout.totalScrollRange) / 100)
            }

        })

    }

    private fun getArtistName(): String {
        return artist.name
    }

    private fun getArtistID(): String {
        return artist.id
    }


}
