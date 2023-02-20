package com.southernsunrise.playme.fragments.mainFragmentScreens

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentMainBinding
import com.southernsunrise.playme.fragments.mainFragmentScreens.searchFragmentScreens.SearchFragment
import com.southernsunrise.playme.utilities.Helper.getBrowseNavOptions


class MainFragment() : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var bottomNavigationBar: BottomNavigationView
    private lateinit var fragmentContainerView: NavHostFragment
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        setupViews()

        return view
    }

    private fun setupViews() {
        setupBottomBar()
    }

    private fun setupBottomBar() {
        bottomNavigationBar = binding.bottomNavBar
        fragmentContainerView =
            this.childFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment

        val popupMenu = PopupMenu(requireContext(), null)
        popupMenu.inflate(R.menu.menu_main_fragment_bottom_nav_bar)
        val menu = popupMenu.menu
        navController = fragmentContainerView.findNavController()
        /* navController.let {
             bottomNavigationBar.setupWithNavController(it)
         }

         */

        bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragmentContainer -> {
                    navController.navigate(R.id.homeFragmentContainer, null, getBrowseNavOptions())
                }
                R.id.searchFragmentContainer -> {
                    navController.navigate(
                        R.id.searchFragmentContainer,
                        null,
                        getBrowseNavOptions()
                    )
                }
                R.id.libraryFragmentContainer -> {
                    navController.navigate(
                        R.id.libraryFragmentContainer,
                        null,
                        getBrowseNavOptions()
                    )
                }
            }
            true
        }
        bottomNavigationBar.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }

    }


}
