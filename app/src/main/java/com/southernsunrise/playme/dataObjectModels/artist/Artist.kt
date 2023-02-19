package com.southernsunrise.playme.dataObjectModels.artist

import java.io.Serializable

data class Artist(
    val albumGroups: AlbumGroups?,
    val blurbs: List<Any>,
    val href: String,
    val id: String,
    val links: Links,
    val name: String,
    val shortcut: String,
    val type: String,
    val amg: Int?,
    val bios: List<Bio>?,
) : Serializable