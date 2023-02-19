package com.southernsunrise.playme.dataObjectModels.album

data class Discographies(
    val main: List<String>?,
    val singlesAndEPs: List<String>?,
    val others: List<String>?,
) : java.io.Serializable