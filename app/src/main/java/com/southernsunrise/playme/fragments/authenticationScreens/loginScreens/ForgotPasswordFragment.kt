package com.southernsunrise.playme.fragments.authenticationScreens.loginScreens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentForgotPasswordBinding
import com.southernsunrise.playme.utilities.FirebaseHelper
import com.southernsunrise.playme.utilities.FirebaseHelper.isEmailValid
import com.southernsunrise.playme.utilities.FirebaseHelper.sendPasswordResetEmail


class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    val binding get() = _binding

    private lateinit var toolbar: Toolbar
    private lateinit var etEmail: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding?.root

        setupViews()



        return view
    }

    private fun setupViews() {
        setupEmailEditText()
        setupLoginButton()
        setupToolBar()
    }

    fun setupLoginButton() {
        loginButton = binding!!.btnLogin
        loginButton.clipToOutline = true
        loginButton.setOnClickListener {
            sendPasswordResetEmail()
        }
    }


    private fun sendPasswordResetEmail() {
        Firebase.auth.sendPasswordResetEmail(etEmail.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Password reset email sent.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(FirebaseHelper.TAG, "Password reset email sent.")
                }else {
                    Toast.makeText(requireContext(), task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupEmailEditText() {
        etEmail = binding!!.etEmail
        etEmail.doOnTextChanged { text, start, before, count ->
            loginButton.isEnabled = isEmailValid(text.toString())
        }
    }

    private fun setupToolBar() {
        toolbar = binding!!.toolbar
        activity?.setActionBar(toolbar)
        val actionBar = activity?.actionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }


    }


}