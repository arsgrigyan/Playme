package com.southernsunrise.playme.retrofit.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.southernsunrise.playme.BuildConfig

object ApiConstants {

    const val BASE_URL = "https://api.napster.com"
    const val API_KEY = BuildConfig.API_KEY

    const val ARTISTS = "artists"
    const val ALBUMS = "albums"
    const val PLAYLISTS = "playlists"
    const val GENRES = "genres"

    fun getImageLink(photoType: String, idOrName: String, imageSize: String): String {

        return when (photoType) {
            ARTISTS, ALBUMS -> {
                "https://api.napster.com/imageserver/v2/${photoType}/${idOrName}/images/${imageSize}.jpg"
            }

            GENRES -> {
                "https://api.napster.com/imageserver/images/${idOrName}/${imageSize}.jpg"
            }
            PLAYLISTS -> {
                "https://api.napster.com/imageserver/v2/playlists/${idOrName}/artists/images/${imageSize}.jpg?order=frequency&montage=2x2"
            }
            else -> {
                ""
            }
        }
    }
}