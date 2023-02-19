package com.southernsunrise.playme.dataObjectModels.searchResult

data class LosslessFormat(
    val bitrate: Int,
    val name: String,
    val sampleBits: Int,
    val sampleRate: Int,
    val type: String
):java.io.Serializable