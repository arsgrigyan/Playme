package com.southernsunrise.playme.dataObjectModels.track

import com.southernsunrise.playme.dataObjectModels.searchResult.LosslessFormat
import java.io.Serializable

data class Track(
    val albumId: String,
    val albumName: String,
    val artistName: String,
    val blurbs: List<Any>,
    val contributors: Contributors,
    val disc: Int,
    val explicit: Boolean,
    val formats: List<Format>,
    val href: String,
    val id: String,
    val index: Int,
    val isStreamable: Boolean,
    val isrc: String,
    val links: Links,
    val name: String,
    val playbackSeconds: Int,
    val previewURL: String,
    val shortcut: String,
    val type: String,

    val amg: String?,
    val artistId: String?,
    val isAvailableInAtmos: Boolean?,
    val isAvailableInHiRes: Boolean?,
    val isExplicit: Boolean?,
    val losslessFormats: List<LosslessFormat>?,


) : Serializable