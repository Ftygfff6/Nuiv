package com.nuviotv.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddonManifest(
    val id: String,
    val version: String,
    val name: String,
    val description: String? = null,
    val logo: String? = null,
    val background: String? = null,
    val contactEmail: String? = null,
    val types: List<String> = emptyList(),
    val resources: List<ResourceItem> = emptyList(),
    val catalogs: List<CatalogDef> = emptyList(),
    val idPrefixes: List<String>? = null,
    val behaviorHints: ManifestBehaviorHints? = null
)

@Serializable
data class ResourceItem(
    val name: String,
    val types: List<String> = emptyList(),
    val idPrefixes: List<String>? = null
)

@Serializable
data class CatalogDef(
    val type: String,
    val id: String,
    val name: String,
    val extra: List<CatalogExtra>? = null
)

@Serializable
data class CatalogExtra(
    val name: String,
    val isRequired: Boolean? = null,
    val options: List<String>? = null
)

@Serializable
data class ManifestBehaviorHints(
    val adult: Boolean? = null,
    val p2p: Boolean? = null,
    val configurable: Boolean? = null,
    val configurationRequired: Boolean? = null
)

@Serializable
data class Meta(
    val id: String,
    val type: String,
    val name: String,
    val poster: String? = null,
    val posterShape: String? = null,
    val background: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val releaseInfo: String? = null,
    val imdbRating: String? = null,
    val genres: List<String>? = null,
    val runtime: String? = null,
    val videos: List<Video>? = null,
    val trailerStreams: List<TrailerStream>? = null
)

@Serializable
data class Video(
    val id: String,
    val title: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val released: String? = null,
    val thumbnail: String? = null,
    val overview: String? = null
)

@Serializable
data class TrailerStream(
    val title: String? = null,
    val ytId: String? = null
)

@Serializable
data class Stream(
    val url: String? = null,
    val ytId: String? = null,
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val externalUrl: String? = null,
    val title: String? = null,
    val name: String? = null,
    val description: String? = null,
    val behaviorHints: StreamBehaviorHints? = null
)

@Serializable
data class StreamBehaviorHints(
    val bingeGroup: String? = null,
    val filename: String? = null,
    val videoHash: String? = null,
    val videoSize: Long? = null
)

@Serializable
data class CatalogResponse(
    val metas: List<Meta> = emptyList()
)

@Serializable
data class MetaResponse(
    val meta: Meta
)

@Serializable
data class StreamResponse(
    val streams: List<Stream> = emptyList()
)

@Serializable
data class SubtitlesResponse(
    val subtitles: List<Subtitle> = emptyList()
)

@Serializable
data class Subtitle(
    val id: String,
    val url: String,
    val lang: String
)

// Config model for saved addons
@Serializable
data class AddonConfig(
    val transportUrl: String,
    val manifest: AddonManifest,
    val addedAt: Long = System.currentTimeMillis(),
    val isEnabled: Boolean = true,
    val order: Int = 0
)
