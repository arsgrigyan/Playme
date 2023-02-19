package com.southernsunrise.playme.fragments.mainFragmentScreens.libraryFragmentScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentLibraryBinding
import com.squareup.picasso.Picasso


class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    val binding get() = _binding

    private lateinit var profilePhotoImageView: ImageView
    private var user: FirebaseUser? = null
    private lateinit var recentlyPlayedSongsRecyclerView: RecyclerView

    private lateinit var playlistsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = Firebase.auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val view = binding?.root

        setupViews()

        return view
    }

    private fun setupViews() {
        setupUserProfilePhotoImageView()
    }

    private fun setupUserProfilePhotoImageView() {
        profilePhotoImageView = binding!!.ivUserProfile
        profilePhotoImageView.apply {
            clipToOutline = true
            Picasso.get().load(user?.photoUrl).into(this)
            setOnClickListener {
                this.findNavController()
                    .navigate(R.id.action_libraryFragment_to_userProfileFragment)
            }
        }


    }

}