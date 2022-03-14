package com.example.yelpsearch.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yelpsearch.api.Resource
import com.example.yelpsearch.repository.SearchRestaurantRepository
import kotlinx.coroutines.launch

class SearchRestaurantViewModel(
    private val repository: SearchRestaurantRepository
) : ViewModel(){

    private val _searchResponse : MutableLiveData<Resource<YelpSearchResponse>> = MutableLiveData()
    val searchResponse : LiveData<Resource<YelpSearchResponse>>
        get() = _searchResponse

    fun searchRestaurants(
        authToken : String,
        searchTerm : String,
        location : String,
        offset : Int,
        limit : Int,
        radius : Int
    ) = viewModelScope.launch{
        _searchResponse.value = repository.searchRestaurants(authToken,searchTerm,location,offset,limit,radius)
    }
}