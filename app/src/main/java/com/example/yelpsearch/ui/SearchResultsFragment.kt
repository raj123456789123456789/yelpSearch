package com.example.yelpsearch.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yelpsearch.R
import com.example.yelpsearch.RestaurantSearchAdapter
import com.example.yelpsearch.api.ApiService
import com.example.yelpsearch.api.Resource
import com.example.yelpsearch.base.BaseFragment
import com.example.yelpsearch.constants.Constants
import com.example.yelpsearch.databinding.FragmentSearchResultsBinding
import com.example.yelpsearch.models.SearchRestaurantViewModel
import com.example.yelpsearch.models.YelpResturant
import com.example.yelpsearch.repository.SearchRestaurantRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.*


class SearchResultsFragment : BaseFragment<SearchRestaurantViewModel,
        FragmentSearchResultsBinding,SearchRestaurantRepository>() {

    private var searchAdapter : RestaurantSearchAdapter? = null
    private var MAX_LIMIT = 15
    var manager : LinearLayoutManager? = null
    private var offset = 0

    private var pastVisibleItem : Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var previous_total = 0
    private var view_treshold = 0
    private var isLoading : Boolean = true
    private val PERMISSION_ID = 42
    private var mCityName : String? = null
    var location: Location? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSeekBar()
        getLastLocation()

        initSwipeRefresh()
    }

    private fun observeData(radius : Int,cityName : String){
        println("cityName $cityName")
        viewModel.searchRestaurants("Bearer ${Constants.API_KEY}","restaurants",cityName,0,MAX_LIMIT,radius)
        binding.progress.visibility = View.VISIBLE
        viewModel.searchResponse.observe(viewLifecycleOwner,{
            when(it){
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    println("OnSuccess ${Gson().toJson(it)}")
                    if(it.value.restaurants.size <= 0){
                        Toast.makeText(requireActivity(), "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        initViews(it.value?.restaurants)
                    }

                }else -> {
                    binding.progress.visibility = View.GONE
                    println("Failure ")
                }
            }
        })
    }

    override fun getViewModel() = SearchRestaurantViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchResultsBinding.inflate(inflater,container,false)

    override fun getFragmentRepository() = SearchRestaurantRepository(remoteDataSource.buildApi(ApiService::class.java))

    private fun initViews(restaurants : MutableList<YelpResturant>){
        searchAdapter = RestaurantSearchAdapter(requireActivity(),restaurants)
        binding.rvSearch.adapter = searchAdapter
        manager = LinearLayoutManager(requireActivity())
        binding.rvSearch.layoutManager = manager

        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                manager?.let {
                    visibleItemCount = it.childCount
                    totalItemCount = it.itemCount
                    pastVisibleItem = it.findFirstVisibleItemPosition()
                }


                if(dy > 0){
                    if(isLoading){
                        if(totalItemCount > previous_total){
                            isLoading = false
                            previous_total = totalItemCount
                        }
                    }
                    if(!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItem+view_treshold)){
                        offset+=15
                        loadMore(offset)
                    }
                }

            }
        })
    }

    private fun loadMore(offset : Int){
        viewModel.searchRestaurants("Bearer ${Constants.API_KEY}","restaurants","New York",offset,MAX_LIMIT,800)
        binding.lodMoreProgress.visibility = View.VISIBLE
        viewModel.searchResponse.observe(viewLifecycleOwner,{
            when(it){
                is Resource.Success -> {
                    binding.lodMoreProgress.visibility = View.GONE
                    if(it.value.restaurants.isNotEmpty())
                    searchAdapter?.loadMore(it.value.restaurants)
                }else -> {
                binding.lodMoreProgress.visibility = View.GONE
                println("Failure ")
            }
            }
        })
    }

    private fun initSeekBar(){
        binding.distanceSlider.progress = 100
        binding.distanceSlider.incrementProgressBy(100)
        binding.distanceSlider.max = 5000
        binding.distanceSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p1 > 1000){
                    binding.selectedDistance.text =  (p1/1000).toString() + "KM"

                }else{
                    binding.selectedDistance.text = p1.toString() + "M"
                }
                if(mCityName != null){
                    observeData(p1,mCityName.toString())
                }
                else{
                    observeData(p1,"New York City")
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private fun initSwipeRefresh(){
        binding.pageRefresh.setOnRefreshListener {
            if(mCityName!=null){
                observeData(binding.distanceSlider.progress,mCityName.toString())
            }else{
                observeData(binding.distanceSlider.progress,"New York City")
            }

            binding.pageRefresh.isRefreshing = false
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun setLocationListner() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000000).setFastestInterval(2000000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (locationData in locationResult.locations) {
                        location = locationData
                    }
                    getCityName()
                }
            },
            Looper.getMainLooper()
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (
                ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getLastLocation()
            }
        }
        else{
            observeData(binding.distanceSlider.progress,"New York City")
        }
    }

    private fun getLastLocation() {
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                setLocationListner()
            } else {
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.require_location),
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun getCityName() {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(location!!.latitude.toDouble(), location!!.longitude.toDouble(), 1)
        if (addresses.size > 0) {
            val cityName: String = addresses.get(0).locality + " " + addresses.get(0).countryName
            if(!cityName.contains("null")){
                mCityName = cityName
                observeData(binding.distanceSlider.progress,cityName)
            }else{
                observeData(binding.distanceSlider.progress,"New York City")
            }

        }


    }
}