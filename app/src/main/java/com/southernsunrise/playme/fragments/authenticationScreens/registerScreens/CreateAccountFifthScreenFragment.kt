package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.southernsunrise.playme.databinding.FragmentCreateAccountFifthScreenBinding


class CreateAccountFifthScreenFragment : Fragment() {

    private var _binding: FragmentCreateAccountFifthScreenBinding? = null
    val binding get() = _binding

    private lateinit var etUsername: TextInputEditText
    private lateinit var userName: String
    private var parentFrag: Fragment? = null

    private lateinit var createAccountButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountFifthScreenBinding.inflate(inflater, container, false)
        val view = binding?.root
        parentFrag = parentFragment?.parentFragment
        setupViews()

        return view
    }


    private fun setupViews() {
        setupUsernameEditText()
        setupCreateAccountButton()

        if (parentFrag is FinishGoogleAuthFragment) {
            (parentFrag as FinishGoogleAuthFragment).setToolbarNavigationButtonEnabled(true)
        }
    }

    private fun setupCreateAccountButton() {
        createAccountButton = binding!!.btnCreateAccount
        createAccountButton.apply {
            clipToOutline = true
            text = when (parentFrag) {
                is FinishGoogleAuthFragment -> "Finish registration"
                else -> "Create account"
            }
        }

        createAccountButton.setOnClickListener {
            if (userNameValid()) {
                userName = etUsername.text.toString()
                setUsernameAndFinishRegistration()
            } else Toast.makeText(requireContext(), "Invalid username", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupUsernameEditText() {
        etUsername = binding!!.etUserName
    }

    private fun getPreviouslyInputUsername(): String {
        return when (parentFrag) {
            is FinishGoogleAuthFragment -> {
                (parentFrag as FinishGoogleAuthFragment).getUser().userName ?: ""
            }
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).getUser().userName ?: ""
            }
            else -> {
                ""
            }
        }
    }


    private fun setUsernameAndFinishRegistration() {
        if (userNameValid()) {

            when (parentFrag) {
                is RegisterFragment -> {
                    (parentFrag as RegisterFragment).setUserName(userName.trim())
                    (parentFrag as RegisterFragment).onRegistrationFinish()
                }
                is FinishGoogleAuthFragment -> {
                    (parentFrag as FinishGoogleAuthFragment).setUserName(userName.trim())
                    (parentFrag as FinishGoogleAuthFragment).onRegistrationFinish()
                }
            }

        }
    }

    private fun userNameValid(): Boolean {
        userName = etUsername.text.toString()
        return userName.length >= 2
    }

    override fun onPause() {
        super.onPause()
        // save user name in case user navigates to the previous screen and comes back
        if (userNameValid()) {
            (parentFrag as RegisterFragment).setUserName(userName.trim())
        }

    }

    override fun onResume() {
        super.onResume()
        // get previously set username and update edit text
        etUsername.setText(getPreviouslyInputUsername())

    }


}