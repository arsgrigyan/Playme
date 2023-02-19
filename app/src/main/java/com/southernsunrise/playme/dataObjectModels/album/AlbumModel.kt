package com.southernsunrise.playme.dataObjectModels.album

import com.southernsunrise.playme.dataObjectModels.album.Meta

data class AlbumModel(
    val albums: List<Album>,
    val meta: Meta
) : java.io.Serializable