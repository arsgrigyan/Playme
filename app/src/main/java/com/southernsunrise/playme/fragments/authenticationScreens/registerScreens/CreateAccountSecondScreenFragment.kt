package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentCreateAccountSecondScreenBinding

class CreateAccountSecondScreenFragment : Fragment() {


    private var _binding: FragmentCreateAccountSecondScreenBinding? = null
    val binding get() = _binding


    private lateinit var etPassword:TextInputEditText
    private lateinit var nextButton: AppCompatButton

    private var parentFrag: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountSecondScreenBinding.inflate(inflater, container, false)
        val view = binding?.root
        parentFrag = parentFragment?.parentFragment

        setupViews()

        return view
    }

    private fun setupViews() {
        setupPasswordEditText()
        nextButton = binding!!.btnNext
        setupNextButton()
    }


    private fun setupNextButton() {
        nextButton.apply {
            clipToOutline = true
            isEnabled = getCurrentlyInputPasswordText().isValidPassword()
            setOnClickListener {
                updateUserDataAndNavigateToThirdFragment()
            }

        }
    }


    private fun updateUserDataAndNavigateToThirdFragment() {
        when (parentFrag) {
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).setUserPassword(getCurrentlyInputPasswordText())
                navigateToNextScreen()
            }
        }

    }

    private fun navigateToNextScreen() {
        this.findNavController().navigate(
            R.id.action_createAccountSecondScreenFragment_to_createAccountThirdScreenFragment,
        )
    }

    private fun getCurrentlyInputPasswordText(): String {
        return etPassword.text?.toString() ?: ""
    }

    private fun setupPasswordEditText() {
        etPassword = binding!!.etUserPassword
        setOnPasswordEditTextTextChangeListener()
    }

    private fun setOnPasswordEditTextTextChangeListener() {
        etPassword.doOnTextChanged { text, start, before, count ->
            text?.let { nextButton.isEnabled = it.isValidPassword() }
        }
    }

    private fun getPreviouslyInputPassword(): String {
        return when (parentFrag) {
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).getUserPassword()
            }
            else -> ""
        }
    }

    internal fun CharSequence.isValidPassword(): Boolean {
        if (this.length < 8) return false
        if (this.filter { it.isDigit() }.firstOrNull() == null) return false
        if (this.filter { it.isLetter() }.filter { it.isUpperCase() }
                .firstOrNull() == null) return false
        if (this.filter { it.isLetter() }.filter { it.isLowerCase() }
                .firstOrNull() == null) return false
        //if (this.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    override fun onResume() {
        super.onResume()
        etPassword.setText(getPreviouslyInputPassword())

    }

}
