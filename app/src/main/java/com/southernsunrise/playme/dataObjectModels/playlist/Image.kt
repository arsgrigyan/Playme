package com.southernsunrise.playme.dataObjectModels.playlist

data class Image(
    val contentId: String,
    // val defaultImage: String,
    val isDefault: Boolean,
    val id: String,
    val imageType: String,
    val url: String,
    val version: Int,
    val type: String,
    val width: Int,
    val height: Int

) : java.io.Serializable

