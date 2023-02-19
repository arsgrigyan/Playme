package com.southernsunrise.playme.dataObjectModels.user

import android.net.Uri

data class User(
    var userID: String?,
    var userName: String?,
    var email: String?,
    var birthdate: String?,
    var gender: String?,
    var phoneNumber: String?,
    var photoUrl: Uri?
) : java.io.Serializable
