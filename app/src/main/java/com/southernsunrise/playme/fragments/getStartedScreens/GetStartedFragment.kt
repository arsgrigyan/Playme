package com.southernsunrise.playme.fragments.getStartedScreens

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.R
import com.southernsunrise.playme.dataObjectModels.user.User
import com.southernsunrise.playme.databinding.FragmentGetStartedBinding
import com.southernsunrise.playme.utilities.Constants.USER

class GetStartedFragment : Fragment(), View.OnClickListener {

    var binding: FragmentGetStartedBinding? = null
    private lateinit var gettingStartedImageView: ImageView
    private lateinit var signupButton: androidx.appcompat.widget.AppCompatButton
    private lateinit var authenticateWithGoogleButton: ConstraintLayout
    private lateinit var loginButton: TextView

    private lateinit var userDataBundle: Bundle

    companion object {
        const val REQ_ONE_TAP = 5
        const val TAG = "GetStartedFragment"
    }

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private var showOneTapUI: Boolean = true

    private var mUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGetStartedBinding.inflate(inflater, container, false)
        val view = binding!!.root

        userDataBundle = Bundle()
        firebaseAuth = Firebase.auth

        oneTapClient = Identity.getSignInClient(requireActivity())
        setupSignInRequest()

        setupViews()

        return view
    }

    fun setupSignInRequest() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }

    fun requestSignInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, Companion.REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(Companion.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(Companion.TAG, e.localizedMessage)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.

                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        mUser = firebaseAuth.currentUser

                                        goToFinishRegistrationScreen()

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                            Log.d(TAG, "Got ID token.")
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d(TAG, "Got password.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(
                                TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }


    private fun goToFinishRegistrationScreen() {
        mUser?.apply {
            val user = User(
                uid,
                displayName,
                email,
                null,
                null,
                null,
                photoUrl
            )

            userDataBundle.putSerializable(USER, user)

            findNavController().navigate(
                R.id.action_getStartedFragment_to_finishGoogleAuthFragment,
                userDataBundle
            )
        }


    }


    private fun setupViews() {
        setupGettingStartedImageView()
        setupButtons()
    }

    private fun setupButtons() {
        signupButton = binding!!.btnSignup
        authenticateWithGoogleButton = binding!!.clBtnContinueWithGoogle
        loginButton = binding!!.tvLogin

        signupButton.setOnClickListener(this)
        authenticateWithGoogleButton.apply {
            clipToOutline = true
            setOnClickListener(this@GetStartedFragment)
        }
        loginButton.apply {
            clipToOutline = true
            setOnClickListener(this@GetStartedFragment)
        }

    }

    private fun setupGettingStartedImageView() {
        gettingStartedImageView = binding!!.ivGettingStarted
        gettingStartedImageView.apply {
            clipToOutline = true
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.btn_signup -> {
                navigateToRegisterFragment()
            }

            R.id.tv_login -> {
                navigateToLoginFragment()
            }
            R.id.cl_btn_continue_with_google -> {
                requestSignInWithGoogle()
            }

        }
    }

    private fun navigateToRegisterFragment() {
        this.findNavController()
            .navigate(R.id.action_getStartedFragment_to_registerFragment)
    }

    private fun navigateToLoginFragment() {
        this.findNavController()
            .navigate(R.id.action_getStartedFragment_to_loginForgotPasswordFragment)
    }
}


