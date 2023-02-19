package com.southernsunrise.playme.dataObjectModels.searchResult

data class Search(
    val `data`: Data,
    val order: List<String>,
    val query: String
):java.io.Serializable