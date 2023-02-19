package com.southernsunrise.playme.dataObjectModels.playlist

import com.southernsunrise.playme.dataObjectModels.playlist.Meta

data class PlaylistModel(
    val playlists: List<Playlist>,
    val meta: Meta
):java.io.Serializable