package com.example.to_dolist

import com.google.android.gms.maps.model.LatLng
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingAPIService {


    @GET("maps/api/geocode/json")//getting the endpoint of the url
    suspend fun getAddressFromCoords(
    @Query("latlng") latLng: String,//passing the latitude and longitude along with the key to fetch us the address
    @Query("key") apikey:String
    ):GeoCodingResponse//this fun returns a list and string as mentioned in the data classes file
    //This here fetch us the json code and converts it to kotlin readable code
}