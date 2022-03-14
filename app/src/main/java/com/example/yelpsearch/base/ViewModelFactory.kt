package com.example.yelpsearch.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yelpsearch.models.SearchRestaurantViewModel
import com.example.yelpsearch.repository.SearchRestaurantRepository
import java.lang.IllegalStateException

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(SearchRestaurantViewModel::class.java) ->
                SearchRestaurantViewModel(repository as SearchRestaurantRepository) as T
            else -> throw IllegalStateException("ViewModel Class Not Found")
        }
    }
}