package com.example.yelpsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.yelpsearch.ui.SearchResultsFragment


class MainActivity : AppCompatActivity() {
    private var searchResultsFragment : SearchResultsFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment()
    }


    private fun addFragment(){
        searchResultsFragment = SearchResultsFragment()
        searchResultsFragment?.let {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.flContainer, it)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        searchResultsFragment?.let {
            it.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}