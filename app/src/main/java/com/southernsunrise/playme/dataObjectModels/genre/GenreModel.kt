package com.southernsunrise.playme.dataObjectModels.genre
import com.southernsunrise.playme.dataObjectModels.genre.Meta

data class GenreModel(
    val genres: List<Genre>,
    val meta: Meta
):java.io.Serializable