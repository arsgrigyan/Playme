package com.southernsunrise.playme.dataObjectModels.album

data class ContributingArtists(
    val primaryArtist: String,
    val composer:String?,
    val featuredPerformer:String?,
    val guestMusician:String?,
    val producer :String?,
):java.io.Serializable