package com.southernsunrise.playme.dataObjectModels.artist

data class Bio(
    val author: String,
    val bio: String,
    val publishDate: String?,
    val title: String
):java.io.Serializable