package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentCreateAccountFourthScreenBinding
import com.southernsunrise.playme.utilities.Constants


class CreateAccountFourthScreenFragment : Fragment() {

    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var checkedGender: String


    var _binding: FragmentCreateAccountFourthScreenBinding? = null
    val binding get() = _binding

    private var parentFrag: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountFourthScreenBinding.inflate(inflater, container, false)
        val view = binding?.root

        parentFrag = parentFragment?.parentFragment

        setupViews()


        return view
    }

    private fun setupViews() {
        setupRadioGroup()
        if (parentFrag is FinishGoogleAuthFragment) {
            (parentFrag as FinishGoogleAuthFragment).setToolbarNavigationButtonEnabled(true)
        }
    }


    private fun setupRadioGroup() {
        genderRadioGroup = binding!!.rgGender
        getAndSetPreviouslyCheckedGender()
        setRadioButtonChangeListener()
    }

    private fun setRadioButtonChangeListener() {
        genderRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbtn_gender_female -> {
                    checkedGender = Constants.GENDER_FEMALE
                    updateUserDataAndNavigateToFifthFragment()

                }
                R.id.rbtn_gender_male -> {
                    checkedGender = Constants.GENDER_MALE
                    updateUserDataAndNavigateToFifthFragment()
                }
                R.id.rbtn_gender_non_binary -> {
                    checkedGender = Constants.GENDER_NON_BINARY
                    updateUserDataAndNavigateToFifthFragment()
                }

            }
        }
    }


    private fun checkGender(gender: String) {

        if (gender.isNotEmpty()) {
            when (gender) {

                Constants.GENDER_FEMALE -> {
                    genderRadioGroup.check(R.id.rbtn_gender_female)
                    // if the user selects the same radio button it will now just navigate to the next screen
                    genderRadioGroup[0].setOnClickListener {
                        navigateToFifthScreen()
                    }
                }

                Constants.GENDER_MALE -> {
                    genderRadioGroup.check(R.id.rbtn_gender_male)

                    // if the user selects the same radio button it will now just navigate to the next screen
                    genderRadioGroup[1].setOnClickListener {
                        navigateToFifthScreen()
                    }

                }

                Constants.GENDER_NON_BINARY -> {
                    genderRadioGroup.check(R.id.rbtn_gender_non_binary)

                    // if the user selects the same radio button it will now just navigate to the next screen
                    genderRadioGroup[2].setOnClickListener {
                        navigateToFifthScreen()
                    }
                }
            }
        }
    }

    private fun getAndSetPreviouslyCheckedGender() {
        when (parentFrag) {
            is FinishGoogleAuthFragment -> {
                (parentFrag as FinishGoogleAuthFragment).getUser().gender?.let {
                    checkGender(it)
                }
            }
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).getUser().gender?.let {
                    checkGender(it)
                }
            }
        }
    }


    private fun updateUserDataAndNavigateToFifthFragment() {

        when (parentFrag) {
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).setUserGender(checkedGender)
            }
            is FinishGoogleAuthFragment -> {
                (parentFrag as FinishGoogleAuthFragment).setUserGender(checkedGender)
            }
        }

        navigateToFifthScreen()
    }

    private fun navigateToFifthScreen() {
        if (parentFrag is FinishGoogleAuthFragment) {
            findNavController().navigate(R.id.action_createAccountFourthScreenFragment2_to_createAccountFifthScreenFragment2)
        } else findNavController().navigate(R.id.action_createAccountFourthScreenFragment_to_createAccountFifthScreenFragment)
    }

}