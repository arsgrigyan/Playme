package com.southernsunrise.playme.dataObjectModels.track

data class Links(
    val albums: Albums?,
    val artists: Artists?,
    val composers: Composers?,
    val genres: Genres?,
    val tags: Tags?
):java.io.Serializable