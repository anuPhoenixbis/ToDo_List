package com.example.to_dolist




//Basic things given to us by the api is LatLng
data class LocationAPIData(
    val latitude:Double,
    val longitude:Double,
)
//The addresses given by the GeoCoding API enabled is each a string given in a list
data class GeoCodingResponse(
    val results:List<GeoCodingResult>
)
//Each address in the list is a string stored here
data class GeoCodingResult(
    val formatted_Address:String
)