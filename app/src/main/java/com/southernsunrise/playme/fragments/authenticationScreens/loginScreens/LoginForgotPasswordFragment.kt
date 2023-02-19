package com.southernsunrise.playme.fragments.authenticationScreens.loginScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentLoginForgotPasswordBinding

class LoginForgotPasswordFragment : Fragment() {

    private var _binding: FragmentLoginForgotPasswordBinding? = null
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding?.root


        return view
    }

}