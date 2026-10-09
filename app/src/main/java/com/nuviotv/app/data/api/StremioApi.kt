package com.nuviotv.app.data.api

import com.nuviotv.app.data.model.AddonManifest
import com.nuviotv.app.data.model.CatalogResponse
import com.nuviotv.app.data.model.MetaResponse
import com.nuviotv.app.data.model.StreamResponse
import com.nuviotv.app.data.model.SubtitlesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface StremioApi {

    @GET("manifest.json")
    suspend fun getManifest(@Url baseUrl: String): AddonManifest

    @GET("catalog/{type}/{id}.json")
    suspend fun getCatalog(
        @Url baseUrl: String,
        @Path("type") type: String,
        @Path("id") id: String,
        @Query("skip") skip: Int = 0,
        @Query("search") search: String? = null,
        @Query("genre") genre: String? = null
    ): CatalogResponse

    @GET("meta/{type}/{id}.json")
    suspend fun getMeta(
        @Url baseUrl: String,
        @Path("type") type: String,
        @Path("id") id: String
    ): MetaResponse

    @GET("stream/{type}/{id}.json")
    suspend fun getStreams(
        @Url baseUrl: String,
        @Path("type") type: String,
        @Path("id") id: String
    ): StreamResponse

    @GET("subtitles/{type}/{id}.json")
    suspend fun getSubtitles(
        @Url baseUrl: String,
        @Path("type") type: String,
        @Path("id") id: String
    ): SubtitlesResponse
}
