package com.southernsunrise.playme.dataObjectModels.searchResult

import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.artist.Artist
import com.southernsunrise.playme.dataObjectModels.track.Track

data class Data(
    val albums: List<Album>,
    val artists: List<Artist>,
    val tracks: List<Track>
) : java.io.Serializable