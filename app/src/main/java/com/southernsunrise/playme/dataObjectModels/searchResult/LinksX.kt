package com.southernsunrise.playme.dataObjectModels.searchResult

data class LinksX(
    val albums: AlbumsX,
    val artists: Artists,
    val composers: Composers,
    val genres: Genres,
    val tags: Tags
):java.io.Serializable