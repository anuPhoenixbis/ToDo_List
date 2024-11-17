package com.example.to_dolist

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //getting the base url and combining it with the endpoint url to fetch us the code
    private const val BASE_URL = "https://maps.googleapis.com/"

    fun create(): GeocodingAPIService {//this fun holds the base url and converts the complete url when combined with the end point to readable kotlin code. It returns a type of GeocodingAPIService
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()//holds the baseurl
        return retrofit.create(GeocodingAPIService::class.java)//we have done this in CookingApp as well refer to it for notes
        //in the return statement we are combining the url to get us the data
    }
}