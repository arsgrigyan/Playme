package com.southernsunrise.playme.dataObjectModels.searchResult

data class Format(
    val bitrate: Int,
    val name: String,
    val sampleBits: Int,
    val sampleRate: Int,
    val type: String
):java.io.Serializable