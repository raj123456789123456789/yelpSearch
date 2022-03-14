package com.example.yelpsearch

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yelpsearch.models.YelpResturant
import com.example.yelpsearch.models.YelpSearchResponse


class RestaurantSearchAdapter(
    val context : Activity,
    val searchResponse : MutableList<YelpResturant>,
    var isLoadingAdded : Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SearchItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var tvRestaurantName: TextView? = null
         var tvRestaurantAddress: TextView? = null
         var imgSearchItem : ImageView? = null
         var tvStatus : TextView? = null
         var tvRating : TextView?  = null

        init {
            tvRestaurantName = itemView.findViewById<TextView>(R.id.tvRestaurantName)
            tvRestaurantAddress = itemView.findViewById<TextView>(R.id.tvRestaurantAddress)
            imgSearchItem = itemView.findViewById<ImageView>(R.id.imgSearchItem)
            tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
            tvRating  = itemView.findViewById<TextView>(R.id.tvRating)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater =
            LayoutInflater.from(parent.context)

                val viewItem: View = inflater.inflate(
                    R.layout.search_item_row,
                    parent,
                    false
                )
                viewHolder = SearchItemViewholder(viewItem)

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchItem = searchResponse.get(position)
                val searchItemHolder = holder as SearchItemViewholder
                searchItemHolder.tvRestaurantName?.setText(searchItem?.name)
                searchItemHolder.tvRestaurantAddress?.text =
                    "${searchItem?.distanceInMeters.toInt()}m.  ${searchItem?.location.address}"

                if(searchItem.is_closed){
                    searchItemHolder.tvStatus?.text = context.getString(R.string.status_close)
                }else{
                    searchItemHolder.tvStatus?.text = context.getString(R.string.status_open)
                }

                searchItemHolder.tvRating?.text = searchItem?.rating.toString()
                searchItemHolder.imgSearchItem?.let {
                    Glide.with(context).load(searchItem.imageUrl).apply(
                        RequestOptions.centerCropTransform()).into(it)
                };


    }

    override fun getItemCount() = searchResponse.size

    fun loadMore(loadData : List<YelpResturant>){
        for(i in 0 until loadData.size){
            searchResponse.add(loadData[i])
        }
        notifyDataSetChanged()

    }
}