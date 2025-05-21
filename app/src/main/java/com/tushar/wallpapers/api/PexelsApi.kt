package com.tushar.wallpapers.api


import com.tushar.wallpapers.model.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PexelsApi {
    @Headers("Authorization: AFaJXkmdwUgdjAk06Z3cPXTz0k2ZbBDXH6U4dPV7jLGrPEDKrQWmNUJz")
    @GET("v1/search")
    suspend fun searchWallpapers(
        @Query("query") query: String,
        @Query("per_page") perPage: Int
    ): PexelsResponse
}
