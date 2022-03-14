package com.example.yelpsearch.api

import com.example.yelpsearch.models.YelpSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("businesses/search")
    suspend fun searchRestaurants(
        @Header("Authorization") authHeader : String,
        @Query("term") searchTerm : String,
        @Query("location") location : String,
        @Query("offset") offset : Int,
        @Query("limit") limit : Int,
        @Query("radius") radius : Int
    ) : YelpSearchResponse
}