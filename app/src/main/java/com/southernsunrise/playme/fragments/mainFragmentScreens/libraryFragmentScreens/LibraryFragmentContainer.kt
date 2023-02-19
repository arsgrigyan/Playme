package com.southernsunrise.playme.fragments.mainFragmentScreens.libraryFragmentScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentHomeContainerBinding
import com.southernsunrise.playme.databinding.FragmentLibraryContainerBinding

class LibraryFragmentContainer : Fragment() {


    private var _binding: FragmentLibraryContainerBinding? = null
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLibraryContainerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    fun navigateTo(destinationId: Int, arguments: Bundle) {
        binding?.containerLibraryFragmentContainer?.findNavController()
            ?.navigate(destinationId, arguments, getNavOptions())
    }

    private fun getNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.anim_fade_in)
            .setExitAnim(R.anim.anim_fade_out)
            .setPopEnterAnim(R.anim.anim_fade_in)
            .setPopExitAnim(R.anim.anim_fade_out)
            .build()
    }


}