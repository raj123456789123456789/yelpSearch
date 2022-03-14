package com.example.yelpsearch.repository

import com.example.yelpsearch.api.ApiService
import com.example.yelpsearch.base.BaseRepository

class SearchRestaurantRepository(
    private val api : ApiService
) : BaseRepository(){

    suspend fun searchRestaurants(
        authToken : String,
        searchTerm : String,
        location : String,
        offset : Int,
        limit : Int,
        radius : Int
    ) = safeApiCall{
        api.searchRestaurants(authToken,searchTerm,location,offset,limit,radius)
    }
}