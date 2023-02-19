package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentCreateAccountFirstScreenBinding


class CreateAccountFirstScreenFragment : Fragment() {

    private var _binding: FragmentCreateAccountFirstScreenBinding? = null
    val binding get() = _binding

    private lateinit var etEmail: TextInputEditText
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
        _binding = FragmentCreateAccountFirstScreenBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root
        parentFrag = parentFragment?.parentFragment
        setupViews()


        return view
    }

    private fun setupViews() {
        nextButton = binding!!.btnNext
        setupEmailEditText()
        setupNextButton()
    }

    private fun setupEmailEditText() {
        etEmail = binding!!.etUserEmail
        setOnEdittextTextChangeListener()

    }

    private fun getPreviouslyInputEmail(): String {
        return when (parentFrag) {
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).getUser().email ?: ""
            }
            else -> { //FinishGoogleAuthFragment
                ""
            }

        }
    }

    private fun getCurrentlyInputEmailText(): String = etEmail.text?.toString() ?: ""


    private fun setupNextButton() {
        nextButton.apply {
            clipToOutline = true
            setOnClickListener {
                setUserEmailAndNavigateToSecondFragment()
            }
            isEnabled = getCurrentlyInputEmailText().isValidEmail()
        }

    }

    private fun setOnEdittextTextChangeListener() {
        etEmail.doOnTextChanged { text, start, before, count ->
            nextButton.isEnabled = text.isValidEmail()
        }
    }


    private fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


    private fun setUserEmailAndNavigateToSecondFragment() {
        when (parentFrag) {
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).setUserEmail(getCurrentlyInputEmailText())
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        findNavController().navigate(
            R.id.action_createAccountFirstScreenFragment_to_createAccountSecondScreenFragment,
        )
    }

    override fun onResume() {
        super.onResume()
        etEmail.setText(getPreviouslyInputEmail())

    }

}