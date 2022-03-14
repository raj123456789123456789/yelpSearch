package com.example.yelpsearch.models

import com.google.gson.annotations.SerializedName

data class YelpSearchResponse(
    @SerializedName("total") val total : Int,
    @SerializedName("businesses") val restaurants : MutableList<YelpResturant>
)

data class YelpResturant(
    @SerializedName("name") val name : String,
    @SerializedName("rating") val rating : Double,
    @SerializedName("distance") val distanceInMeters : Double,
    @SerializedName("image_url") val imageUrl : String,
    @SerializedName("price") val price : String,
    @SerializedName("is_closed") val is_closed : Boolean,
    val categories : List<YelpCategory>,
    val location : YelpLocation
)

data class YelpCategory(
    val title : String
)

data class YelpLocation(
    @SerializedName("address1") val address : String
)


