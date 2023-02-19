package com.southernsunrise.playme.utilities

import android.util.Log
import android.util.Patterns
import com.google.common.base.Strings.isNullOrEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

object FirebaseHelper {
    val TAG = "Firebase helper"

    fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification().addOnSuccessListener {
            Log.d(TAG, "Verification email sent.")
        }
    }

    fun sendPasswordResetEmail(emailAddress: String) {
        Firebase.auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent.")
                }
            }
    }

    fun updateUserPassword(user: FirebaseUser, newPassword: String) {
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }
            }
    }

    fun updateUserEmail(user: FirebaseUser, newEmail: String) {
        user.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                }
            }
    }

    fun isEmailValid(email: String): Boolean {
        return !isNullOrEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun FirebaseUser.updateProf(profileUpdates:UserProfileChangeRequest){
        this.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }



}