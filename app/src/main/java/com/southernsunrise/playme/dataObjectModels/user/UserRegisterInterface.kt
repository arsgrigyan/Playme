package com.southernsunrise.playme.dataObjectModels.user

import android.net.Uri


interface UserRegisterInterface {
    fun getUser(): User
    fun setUserName(name: String)
    fun setUserPassword(password: String)
    fun setUserEmail(email: String)
    fun setUserBirthdate(birthdate: String)
    fun setUserGender(gender: String)
    fun setUserPhotoUrl(photoUrl: Uri)
    fun onRegistrationFinish()
}