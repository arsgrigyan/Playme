package com.southernsunrise.playme.dataObjectModels.album

import java.io.Serializable

data class Album(
    val accountPartner: String,
    val artistName: String,
    val contributingArtists: ContributingArtists?,
    val copyright: String,
    val discCount: Int,
    val discographies: Discographies,
    val href: String,
    val id: String,
    val isAvailableInAtmos: Boolean,
    val isAvailableInHiRes: Boolean,
    val isExplicit: Boolean,
    val isSingle: Boolean,
    val isStreamable: Boolean,
    val label: String?,
    val links: Links,
    val name: String,
    val originallyReleased: String,
    val released: String,
    val shortcut: String,
    val tags: List<String>,
    val trackCount: Int,
    val type: String,
    val upc: String,
    val amg: Long?,
    val reviews: Reviews?
) : Serializable