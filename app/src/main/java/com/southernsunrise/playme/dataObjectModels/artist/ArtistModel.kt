package com.southernsunrise.playme.dataObjectModels.artist
import com.southernsunrise.playme.dataObjectModels.artist.Meta
data class ArtistModel(
    val artists: List<Artist>,
    val meta: Meta
):java.io.Serializable