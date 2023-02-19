package com.southernsunrise.playme.fragments.authenticationScreens.registerScreens

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adwardstark.mtextdrawable.MaterialTextDrawable
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.southernsunrise.playme.R
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.dataObjectModels.user.User
import com.southernsunrise.playme.dataObjectModels.user.UserRegisterInterface
import com.southernsunrise.playme.databinding.FragmentRegisterBinding
import com.southernsunrise.playme.utilities.FirebaseHelper
import com.southernsunrise.playme.utilities.Helper.toByteArray


class RegisterFragment : Fragment(), UserRegisterInterface {

    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding
    private lateinit var toolbar: Toolbar

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private var firebaseUser: FirebaseUser? = null
    private lateinit var user: User
    private lateinit var userPassword: String


    val TAG = "RegisterFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = User(null, null, null, null, null, null, null)
        userPassword = ""
        auth = Firebase.auth
        firebaseStorage = Firebase.storage
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding?.root
        setupViews()

        return view
    }

    private fun setupViews() {
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar = binding!!.toolbar
        activity?.setActionBar(toolbar)
        activity?.actionBar?.setDisplayShowTitleEnabled(false)
        activity?.actionBar?.setDisplayShowHomeEnabled(true)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
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

    override fun setUserPassword(password: String) {
        userPassword = password
    }

    fun getUserPassword(): String = userPassword

    override fun onRegistrationFinish() {
        (requireActivity() as MainActivity).showLoadingLayer(true)
        createFirebaseUserWithEmailAndPassword()
    }

    private fun createFirebaseUserWithEmailAndPassword() {
        user.email?.let {
            auth.createUserWithEmailAndPassword(it, userPassword)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        firebaseUser = auth.currentUser
                        user.userID = firebaseUser?.uid
                        createUserAvatarAndUploadToCloudStorage()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            requireContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

    private fun createUserAvatarAndUploadToCloudStorage() {

        val userNameFirstLetter = user.userName?.first()

        val avatarTextDrawable: BitmapDrawable =
            MaterialTextDrawable.with(requireContext()).text(userNameFirstLetter.toString()).get()

        val reference =
            firebaseStorage.reference.child("user_data/media/${user.userID}/userTextAvatar")

        reference.putBytes(avatarTextDrawable.toBitmap().toByteArray()).addOnSuccessListener {
            val getDownloadUri: Task<Uri> = reference.downloadUrl
            getDownloadUri.addOnSuccessListener {
                val profileUpdates: UserProfileChangeRequest = userProfileChangeRequest {
                    photoUri = it
                }
                firebaseUser?.updateProfile(profileUpdates)?.addOnSuccessListener {
                    registerUserInFirestore()
                }?.addOnFailureListener {
                    Log.e(TAG, it.message + it.stackTraceToString())
                }
            }
            Log.i(TAG, "user profile photo successfully set")

        }.addOnFailureListener {
            Log.e(TAG, it.message + it.stackTraceToString())
        }

    }


    private fun registerUserInFirestore() {
        if (user.userID != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(user.userID!!).set(user).addOnSuccessListener {
                    //log user in when user is user is successfully added
                    //createUserAvatarAndUploadToCloudStorage()
                    logUserIn()
                    Log.i(TAG, "registration success")
                }
                .addOnFailureListener {
                    Log.e(TAG, it.stackTraceToString())
                }
        }
    }

    private fun logUserIn() {
        user.email?.let {
            auth.signInWithEmailAndPassword(it, userPassword)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val firebaseUser: FirebaseUser? = auth.currentUser
                        firebaseUser?.let { FirebaseHelper.sendVerificationEmail(firebaseUser) }
                        (requireActivity() as MainActivity).showLoadingLayer(false)
                        navigateToMainFragment()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            requireContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    private fun navigateToMainFragment() {
        this.findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
    }


}

