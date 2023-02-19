package com.southernsunrise.playme.dataObjectModels.playlist

import com.southernsunrise.playme.dataObjectModels.artist.Artist

data class Playlist(
    val description: String,
    val favoriteCount: Int,
    val trackCount:Int,
    val freePlayCompliant: Boolean,
    val href: String,
    val id: String,
    val images: List<Image?>?,
    val links: Links?,
    val modified: String,
    val name: String,
    val privacy: String,
    val type: String,
    var sampleArtistObjects: List<Artist>?
):java.io.Serializable