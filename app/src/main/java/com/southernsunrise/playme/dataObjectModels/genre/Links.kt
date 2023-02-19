package com.southernsunrise.playme.dataObjectModels.genre

data class Links(
    val childGenres: ChildGenres?,
    val parentGenres: ParentGenres?
):java.io.Serializable