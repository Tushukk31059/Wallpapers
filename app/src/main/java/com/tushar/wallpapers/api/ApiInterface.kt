package com.tushar.wallpapers.api

import com.tushar.wallpapers.model.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("search")
    fun getSearchImage(
        @Query("query") query: String,
        @Query("per_page") perPage: Int
    ): Call<SearchResult>
}
