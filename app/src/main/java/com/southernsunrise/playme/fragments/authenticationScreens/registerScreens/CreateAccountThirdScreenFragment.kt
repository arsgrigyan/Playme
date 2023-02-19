package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.FragmentCreateAccountThirdScreenBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CreateAccountThirdScreenFragment : Fragment() {

    private lateinit var nextButton: AppCompatButton
    private lateinit var datePicker: DatePicker

    private var parentFrag: Fragment? = null

    private var binding: FragmentCreateAccountThirdScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateAccountThirdScreenBinding.inflate(inflater, container, false)
        parentFrag = parentFragment?.parentFragment

        val view = binding?.root
        setupViews()


        return view


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViews() {
        setupBirthdatePicker()
        setupNextButton()
        if (parentFrag is FinishGoogleAuthFragment) {
            (parentFrag as FinishGoogleAuthFragment).setToolbarNavigationButtonEnabled(false)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupBirthdatePicker() {
        datePicker = binding!!.spinnerDatePicker
        setDatePickerMaxMinValues()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPreviouslyInputDateAndUpdateDatePicker() {
        when (parentFrag) {
            is FinishGoogleAuthFragment -> {
                (parentFrag as FinishGoogleAuthFragment).getUser().apply {
                    val date: LocalDate? = this.birthdate?.getDate()
                    date?.let {
                        //set data picker data
                        datePicker.updateDate(it.year, it.month.value-1, it.dayOfMonth)

                    }
                }
            }
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).getUser().apply {
                    val date: LocalDate? = this.birthdate?.getDate()
                    date?.let {
                        //set data picker data
                        datePicker.updateDate(it.year, it.month.value-1, it.dayOfMonth)
                        /*Toast.makeText(requireContext(), birthdate.toString(), Toast.LENGTH_LONG)
                            .show()

                         */

                    }
                }
            }

        }
    }

    private fun setupNextButton() {
        nextButton = binding!!.btnNext

        nextButton.apply {
            clipToOutline = true
            setOnClickListener {
                 //updateBirthDate()
                navigateToNextFragment()
            }

        }
    }


    fun DatePicker.getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time
    }


    @SuppressLint("SimpleDateFormat")
    fun Date.getDateString(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val dateString: String = sdf.format(this)
        return dateString
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun String.getDate(): LocalDate {
        val localDate: LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        return localDate
    }

    private fun setDatePickerMaxMinValues() {
        // set max value to three years back from now
        datePicker.maxDate = getCurrentTimeInMillis() - 3 * 31556952000
        // set min value to 120 years back from now
        datePicker.minDate = getCurrentTimeInMillis() - 120 * 31556952000
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateBirthDate() {
        val dateString: String = datePicker.getDate().getDateString()

        when (parentFrag) {
            is RegisterFragment -> {
                (parentFrag as RegisterFragment).setUserBirthdate(dateString)

            }
            is FinishGoogleAuthFragment -> {
                (parentFrag as FinishGoogleAuthFragment).setUserBirthdate(dateString)
            }
        }
    }

    private fun navigateToNextFragment() {
        if (parentFrag is RegisterFragment) this.findNavController().navigate(
            R.id.action_createAccountThirdScreenFragment_to_createAccountFourthScreenFragment,
        ) else this.findNavController().navigate(
            R.id.action_createAccountThirdScreenFragment2_to_createAccountFourthScreenFragment2,
        )
    }


    private fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        updateBirthDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        getPreviouslyInputDateAndUpdateDatePicker()
    }
}
