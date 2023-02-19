package com.southernsunrise.playme.dataObjectModels.album

data class Links(
    val artists: Artists?,
    val genres: Genres,
    val images: Images,
    val posts: Posts,
    val tracks: Tracks
):java.io.Serializable