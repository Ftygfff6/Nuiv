package com.nuviotv.app.data.api

import com.nuviotv.app.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface StremioApi {
    @GET
    suspend fun getManifest(@Url fullUrl: String): AddonManifest

    @GET
    suspend fun getCatalog(
        @Url fullUrl: String,
        @Query("skip") skip: Int = 0,
        @Query("search") search: String? = null
    ): CatalogResponse

    @GET
    suspend fun getMeta(@Url fullUrl: String): MetaResponse

    @GET
    suspend fun getStreams(@Url fullUrl: String): StreamResponse

    @GET
    suspend fun getSubtitles(@Url fullUrl: String): SubtitlesResponse
}
