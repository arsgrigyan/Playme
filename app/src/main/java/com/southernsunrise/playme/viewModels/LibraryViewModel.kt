package com.southernsunrise.playme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.dataObjectModels.genre.GenreModel

class LibraryViewModel: ViewModel() {


    init {
        getUserData()
    }

    private fun getUserData() {
    }


}