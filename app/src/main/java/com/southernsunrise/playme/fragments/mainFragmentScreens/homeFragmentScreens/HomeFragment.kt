package com.southernsunrise.playme.fragments.mainFragmentScreens.homeFragmentScreens

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.southernsunrise.playme.R
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.databinding.FragmentHomeBinding
import com.southernsunrise.playme.fragments.mainFragmentScreens.MainFragment
import com.southernsunrise.playme.utilities.Constants
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var settingsImageButton: ImageButton
    private lateinit var musicPodcastsRadioGroup: RadioGroup
    private lateinit var greetTextView: TextView


    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding?.root


        setupViews()


        return view
    }


    fun setupViews() {
        binding?.let {
            settingsImageButton = it.ibSettings
            settingsImageButton.setOnClickListener {

            }
            setupRadioGroup()
            setupTextViews()

            // setting nested scroll view's bottom padding to screen height's 5%
            val bottomPaddingValue =
                (requireActivity() as MainActivity).getDisplayMatrix()["height"]?.times(0.05)
                    ?.toInt()
            bottomPaddingValue?.let { it1 -> it.nsvHomeFrag.setPadding(0, 0, 0, it1) }


        }


    }

    private fun setupTextViews() {
        greetTextView = binding!!.tvGreet
        getCurrentTimeAndSetupGreetTextView()
    }

    private fun setupRadioGroup() {
        musicPodcastsRadioGroup = binding!!.rgMusicPodcasts
        musicPodcastsRadioGroup.check(R.id.rbtn_music)

        musicPodcastsRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbtn_music -> {
                    this.childFragmentManager.findFragmentById(R.id.music_podcast_fragmentContainerView)
                        ?.findNavController()
                        ?.navigate(R.id.action_podcastFragment_to_musicFragment)
                }
                R.id.rbtn_podcasts -> {
                    this.childFragmentManager.findFragmentById(R.id.music_podcast_fragmentContainerView)
                        ?.findNavController()
                        ?.navigate(R.id.action_musicFragment_to_podcastFragment)

                }
            }
        }
    }

    private fun getCurrentTimeAndSetupGreetTextView() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        when (currentHour) {
            in 5 until 12 -> {
                greetTextView.text = Constants.GOOD_MORNING
            }
            in 12 until 18 -> {
                greetTextView.text = Constants.GOOD_AFTERNOON
            }
            else -> {
                greetTextView.text = Constants.GOOD_EVENING
            }
        }
    }

    fun showLoadingScreen(backgroundVisible: Boolean, progressbarVisible: Boolean = true) {
        binding?.let {
            val loadingBackground = it.bgDarkLoading
            val progressbar = it.pbLoading

            progressbar.visibility = if (progressbarVisible) {
                View.VISIBLE
            } else View.GONE

            if (backgroundVisible) {
                loadingBackground.isVisible = true
            } else {
                loadingBackground.animate()
                    .alpha(0f)
                    .setDuration(250L)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            it.bgDarkLoading.visibility = View.GONE
                        }
                    })
            }

        }


    }


}