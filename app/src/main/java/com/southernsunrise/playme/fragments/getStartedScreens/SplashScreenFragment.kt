package com.southernsunrise.playme.fragments.getStartedScreens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentSplashScreenBinding


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {

    var binding: FragmentSplashScreenBinding? = null
    private lateinit var appIconImageView: ImageView

    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val view = binding!!.root
        appIconImageView = binding!!.ivAppIcon
        animateImageAndTextViews()

        firebaseAuth = Firebase.auth
        currentUser = firebaseAuth.currentUser



        Handler().postDelayed({
            navigateToGreetOrMainFragment()
        }, 3000)


        return view
    }

    private fun navigateToGreetOrMainFragment() {
        if(currentUser != null) {
            this.findNavController().navigate(R.id.action_flashScreenFragment_to_mainFragment)
        }else {
            this.findNavController().navigate(R.id.action_flashScreenFragment_to_greetFragment)
        }
    }

    private fun animateImageAndTextViews() {

        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_in_bottom_to_top)
        anim.duration = 100
        appIconImageView.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.anim_fade_in_bottom_to_top
            )
        )


    }


}