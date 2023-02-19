package com.southernsunrise.playme.fragments.authenticationScreens.loginScreens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentLoginBinding
import com.southernsunrise.playme.utilities.FirebaseHelper
import com.southernsunrise.playme.utilities.FirebaseHelper.isEmailValid

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var toolbar: Toolbar
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private var emailValid = false

    private lateinit var auth: FirebaseAuth

    val TAG = "LoginFragment"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = Firebase.auth
        setupViews()


        return view

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupViews() {
        setupEditTexts()
        setupToolbar()
        setupLoginButton()
        setupForgotPasswordTextView()

    }

    private fun setupEditTexts() {
        etEmail = binding.etEmail
        etPassword = binding.etPassword

        etEmail.doOnTextChanged { text, start, before, count ->
            emailValid = isEmailValid(text.toString())
            loginButton.isEnabled =
                emailValid && !etPassword.text.isNullOrBlank()
        }

        etPassword.doOnTextChanged { text, start, before, count ->
            loginButton.isEnabled = emailValid && !text.isNullOrBlank()
        }
    }

    private fun setupForgotPasswordTextView() {
        forgotPasswordTextView = binding.tvForgotPassword
        forgotPasswordTextView.apply {
            clipToOutline = true
            setOnClickListener(this@LoginFragment)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupLoginButton() {
        loginButton = binding.btnLogin
        loginButton.apply {
            clipToOutline = true
            foreground = requireActivity().getDrawable(R.drawable.foreground_on_view_press_dark)
            setOnClickListener(this@LoginFragment)
        }

    }

    private fun setupToolbar() {
        toolbar = binding.toolbar
        requireActivity().setActionBar(toolbar)          //activity?.setActionBar(toolbar)
        requireActivity().actionBar?.setDisplayShowTitleEnabled(false)
        requireActivity().actionBar?.setDisplayShowHomeEnabled(true)
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_login -> {
                signIn()
            }
            R.id.tv_forgot_password -> {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }

        }
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    navigateToMainFragment()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), task.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToMainFragment() {
        (parentFragment?.parentFragment as LoginForgotPasswordFragment).findNavController()
            .navigate(R.id.action_loginForgotPasswordFragment_to_mainFragment)
    }


}