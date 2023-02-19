package com.southernsunrise.playme.dataObjectModels.artist

import com.southernsunrise.playme.dataObjectModels.searchResult.Followers

data class Links(
    val albums: Albums,
    val genres: Genres,
    val images: Images,
    val influences: Influences?,
    val followers: Followers?,
    val posts: Posts,
    val stations: Stations,
    val topTracks: TopTracks,
    val contemporaries:Contemporaries?,
    val relatedProjects: RelatedProjects?,
):java.io.Serializable