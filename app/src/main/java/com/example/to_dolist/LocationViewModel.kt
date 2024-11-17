package com.example.to_dolist

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

//just the usual viewmodel thing
class LocationViewModel:ViewModel() {//creating a private mutable var and then passing its State into var
    private val _location = mutableStateOf<LocationAPIData?>(null)
    val location:State<LocationAPIData?> = _location


    private val _address= mutableStateOf(listOf<GeoCodingResult>())//Just the usual viewmodel thing but with address as well
    val address : State<List<GeoCodingResult>> = _address


    fun LocationUpdater(newLocationAPIData: LocationAPIData){//updates the global var with this new location data
        _location.value=newLocationAPIData
    }

    fun fetchAddress(latlng:String){//we are using a coroutine thats why we are using try-catch statement here.
        // We are fetching the address from the api service. We are going to call a suspend fun and that can be done inside a coroutine only.
        try {
                viewModelScope.launch {
                    val result=RetrofitClient.create().getAddressFromCoords(//here we are inputting the queries setup in GeocodingAPIService suspend fun
                        latlng,"AIzaSyD8VMhJjanjUOTGkINdqqeeCJ7VBcGSz2M"//combining the 2 parts of the url and fetching the data
                        )
                    _address.value=result.results//gets the addresses list in the datatype of "results" of the data class and copying it in the private address var
                }
        }catch(e:Exception) {
            Log.d("res1","${e.cause} ${e.message}")//it extracts the cause and message of the error occurred from the tag " res1 " and displays it if it runs into any error
            //the cause and message is obtained from the logcat
        }
    }
}