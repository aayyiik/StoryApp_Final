package com.ari.submission.storyapp.ui.maps

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.ari.submission.storyapp.R
import com.ari.submission.storyapp.databinding.ActivityMapsBinding
import com.ari.submission.storyapp.preferences.SharedPreferences
import com.ari.submission.storyapp.utils.MyResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var mMap : GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var sph: SharedPreferences

    var fusedLocationProvider: FusedLocationProviderClient? =null
    var currentLoaction: Location? = null

    private val mapsViewModel by viewModels<MapsViewModel>{
        MapsViewModel.Factory(this)
    }

    private var storyName: ArrayList<String>? = null
    private var storyDesc: ArrayList<String>? = null
    private var storyLat: ArrayList<Float>? = null
    private var storyLong: ArrayList<Float>? = null


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sph = SharedPreferences(this)
        sph.setStatusLogin(true)

        binding.progressBar.visibility = View.GONE

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()


    }

    private fun fetchLocation(){
        if(ActivityCompat.checkSelfPermission(this@MapsActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MapsActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1000)
            return
        }

        val task = fusedLocationProvider?.lastLocation
        task?.addOnSuccessListener { location->
            if(location!=null){
                this.currentLoaction = location
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        val latlong = LatLng(currentLoaction?.latitude!!, currentLoaction?.longitude!!)
        drawMarker(latlong)
        listMapsUser()
    }

    private fun drawMarker(latlong: LatLng){
        val markerOptions = MarkerOptions().position(latlong)
            .title("Your Location Now")
            .snippet(getAddress(latlong.latitude, latlong.longitude))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlong))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
        mMap.addMarker(markerOptions)
    }

    private fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat,lon,1)
        return addresses[0].getAddressLine(0).toString()
    }

    private fun listMapsUser(){
        val token = "Bearer ${sph.getUserToken()}"
        mapsViewModel.getMapStories(token).observe(this@MapsActivity){
            if(it != null){
                when(it){
                    is MyResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MyResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val maps = it.list.listStory
                            storyName = ArrayList()
                            storyDesc = ArrayList()
                            storyLat = ArrayList()
                            storyLong = ArrayList()
                        for (i in maps.indices) {
                            storyName?.add(maps[i].name.toString())
                            storyDesc?.add(maps[i].description.toString())
                            storyLat?.add(maps[i].lat.toString().toFloat())
                            storyLong?.add(maps[i].lon.toString().toFloat())

                        }
                        maps.forEach { list ->
                            val latLng = LatLng(list.lat.toString().toDouble(), list.lon.toString().toDouble())
                            mMap.addMarker(MarkerOptions().position(latLng).title(list.name))
                        }
                    }
                    is MyResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

//    private fun listMapsUserBefore(){
//        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
//        val scope = CoroutineScope(dispatcher)
//        scope.launch {
//            val token = "Bearer ${sph.getUserToken()}"
//            withContext(Dispatchers.Main) {
//                val client = ApiConfig().getApiService().getAllStoriesWithLoc(token)
//                client.enqueue(object: Callback<GetAllStoryResponse> {
//                    override fun onResponse(
//                        call: Call<GetAllStoryResponse>,
//                        response: Response<GetAllStoryResponse>,
//                    ) {
//                        if (response.isSuccessful){
//                            val responseBody = response.body()
//                            if (responseBody!=null && !responseBody.error){
//                                val dataStories = responseBody.listStory
//                                storyName = ArrayList()
//                                storyDesc = ArrayList()
//                                storyLat = ArrayList()
//                                storyLong = ArrayList()
//                                    for (i in dataStories.indices) {
//                                        storyName?.add(dataStories[i].name.toString())
//                                        storyDesc?.add(dataStories[i].description.toString())
//                                        storyLat?.add(dataStories[i].lat.toString().toFloat())
//                                        storyLong?.add(dataStories[i].lon.toString().toFloat())
//
//                                    }
//
//                                dataStories.forEach { list ->
//                                    val latLng = LatLng(list.lat.toString().toDouble(), list.lon.toString().toDouble())
//                                    mMap.addMarker(MarkerOptions().position(latLng).title(list.name))
//                                }
//                            }
//                        }
//
//                    }
//
//                    override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
//                    }
//                })
//            }
//        }
//    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type-> {
                mMap.mapType= MAP_TYPE_NORMAL
                return true
            }
            R.id.satellite_type-> {
                mMap.mapType= MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type-> {
                mMap.mapType= MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type-> {
                mMap.mapType= MAP_TYPE_HYBRID
                true
            }
            else-> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
