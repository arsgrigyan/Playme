package com.southernsunrise.playme.dataObjectModels.track

import com.southernsunrise.playme.dataObjectModels.track.Meta

data class TrackModel(
    val meta: Meta?,
    val tracks: List<Track>
):java.io.Serializable