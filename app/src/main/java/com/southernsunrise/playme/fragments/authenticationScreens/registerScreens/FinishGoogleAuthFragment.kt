package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.playme.R
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.dataObjectModels.user.User
import com.southernsunrise.playme.dataObjectModels.user.UserRegisterInterface
import com.southernsunrise.playme.databinding.FragmentFinishGoogleAuthBinding
import com.southernsunrise.playme.utilities.Constants


class FinishGoogleAuthFragment : Fragment(), UserRegisterInterface {

    private var _binding: FragmentFinishGoogleAuthBinding? = null
    val binding get() = _binding

    private lateinit var toolbar: Toolbar


    private lateinit var user: User

    val TAG = this@FinishGoogleAuthFragment.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = User(null, null, null, null, null, null, null)
        arguments?.let {
            user = it.getSerializable(Constants.USER) as User

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishGoogleAuthBinding.inflate(inflater, container, false)
        val view = binding?.root
        setupToolbar()
        return view
    }

    private fun setupToolbar() {
        toolbar = binding!!.toolbar
        activity?.setActionBar(toolbar)
        activity?.actionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun setToolbarNavigationButtonEnabled(enabled: Boolean) {
        activity?.actionBar?.setDisplayShowHomeEnabled(enabled)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(enabled)
    }

    override fun setUserName(name: String) {
        user.userName = name
        Log.i(TAG, "User name set successfully: $name")

    }

    override fun setUserBirthdate(birthdate: String) {
        user.birthdate = birthdate
        Log.i(TAG, "User birthdate set successfully: $birthdate")
    }

    override fun setUserGender(gender: String) {
        user.gender = gender
        Log.i(TAG, "User gender set successfully: $gender")

    }

    @SuppressLint("LongLogTag")
    override fun setUserEmail(email: String) {
        user.email = email
        Log.i(TAG, "User email set successfully: $email")

    }

    override fun getUser(): User {
        return user
    }

    override fun setUserPhotoUrl(photoUrl: Uri) {
        user.photoUrl = photoUrl
        Log.i(TAG, "User photo url set successfully: $photoUrl")

    }


    override fun onRegistrationFinish() {
        (requireActivity() as MainActivity).showLoadingLayer(true)
        registerUserInFirestoreAndNavigateToMainFragment()
    }

    override fun setUserPassword(password: String) {}


    private fun registerUserInFirestoreAndNavigateToMainFragment() {
        // create user in firestore
        if (user.userID != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(user.userID!!).set(user).addOnSuccessListener {
                    (requireActivity() as MainActivity).showLoadingLayer(false)
                    //navigate to Main fragment when user is user is successfully added
                    navigateToMainFragment()
                    Log.i(TAG, "registration success")
                }
                .addOnFailureListener {
                    Log.e(TAG, it.stackTraceToString())
                }
        }
    }

    private fun navigateToMainFragment() {
        this.findNavController().navigate(R.id.action_finishGoogleAuthFragment_to_mainFragment)

    }


}