package com.southernsunrise.playme.fragments.mainFragmentScreens

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentUserProfileBinding
import com.squareup.picasso.Picasso
import kotlin.math.abs


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userPhotoImageView: ImageView
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = Firebase.auth.currentUser
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        setupViews()
        setOnAppBarLayoutOffsetChangeListener()

        return view
    }

    fun setupViews() {
        setupTextViews()
        setupUserPhotoImageView()
        setupNavigateBackButton()
    }

    private fun setupTextViews() {
        currentUser?.displayName.let {
            binding.tvUserName.text = it
            binding.tvTooolbarUserName.text = it
        }

    }

    private fun setupNavigateBackButton() {
        binding.ibArrowBack.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    private fun setupUserPhotoImageView() {
        userPhotoImageView = binding.ivUserPhoto
        userPhotoImageView.apply {
            clipToOutline = true
            Picasso.get().load(currentUser?.photoUrl).into(this)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun setOnAppBarLayoutOffsetChangeListener() {
        binding.profileAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            // change toolbar's user name textView's alpha depending on appBarLayout offset
            binding.tvTooolbarUserName.alpha =
                abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())

        })

    }

}