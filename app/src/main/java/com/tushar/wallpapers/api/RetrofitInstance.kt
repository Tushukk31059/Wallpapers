package com.tushar.wallpapers.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: PexelsApi by lazy {
        retrofit.create(PexelsApi::class.java)
    }
}
