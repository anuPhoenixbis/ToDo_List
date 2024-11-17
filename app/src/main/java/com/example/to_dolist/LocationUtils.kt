package com.example.to_dolist

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


//This is a helper class created to check if any permission is granted or not.
/*Helper classes in Kotlin are a great way to encapsulate reusable functionality
 and promote code organization. They can be used to store common functions, constants,
  and other utilities that can be used throughout your codebase.

  To create a helper class in Kotlin, simply create a new class file and add your
  desired functionality. */

class LocationUtils(val context : Context) {

    //FusedLocationProviderClient allows us to get the latitude and longitude of the user/device
    //LocationServices : The main entry point for location services integration.We are passing context which holds the data class LocationData
    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")//in line 46, we faced a missing permission error and this suppress composable is used supress the error and ignore it
    fun requestLoactionUpdates(viewModel: LocationViewModel) {//to get the updated location from the viewmodel
        val locationCallback = object : LocationCallback() {
            //LocationCallback() rechecks for the latitude and longitude details and updates this var accordingly
            @SuppressLint("SuspiciousIndentation")
            override fun onLocationResult(locationResult: LocationResult) {//It overrides the previous onLocationResult with the new one . locationResult var holds the value of type LocationResult obj
                super.onLocationResult(locationResult)//It calls itself to override but with new location details
                locationResult.lastLocation?.let {//here the "it" holds the location details from the google maps which is passed here
                    val location = LocationAPIData(
                        latitude = it.latitude,
                        longitude = it.longitude
                    )//the location var will be of type LocationData data class and will contain the new details
                    viewModel.LocationUpdater(location)//updating the updater fun in the viewmodel with new details
                }//After the location is shown it declares itself as the last location(its type is nullable) for the next override
            }
        }
        val locationrequest =
            LocationRequest.Builder(//characteristic of the location request from the gmaps must of type, the following
                Priority.PRIORITY_HIGH_ACCURACY, 1000//high accuracy means low battery consumption
            ).build()
        //_fusedLocationClient(contains context: responsible for the permissions) takes request characteristics , the location details and a looper which runs the message again and again in a queue
        _fusedLocationClient.requestLocationUpdates(
            locationrequest,
            locationCallback,
            Looper.getMainLooper()
        )//runs requestlocationupdates fun in a loop
    }


    fun hasLocalPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION//here we are looking whether ACCESS_FINE_LOCATION permission is granted or not, for if it is granted , this statement returns a int which is then compared
        ) == PackageManager.PERMISSION_GRANTED//PackageManager.PERMISSION_GRANTED is the int that confirms that the permission is granted
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION//here we are looking whether ACCESS_COARSE_LOCATION permission is granted or not, for if it is granted , this statement returns a int which is then compared
                ) == PackageManager.PERMISSION_GRANTED
                ) //returns true if the permissionS for fine and coarse location is granted by the user or not in the package manager
    } //now whenever we wanted to check whether the user gave us permission or not we can call this fun and get a boolean in return


    fun reverseGeoDecodeLocation(location: LocationAPIData):String{//takes location details and returns the address as the String
        val geocoder=
            Geocoder(context, Locale.ENGLISH)//Geocoder is the fun which takes context(containing location details) and the language in which the address is to be written
        val coords= LatLng(location.latitude,location.longitude)//LatLng fun gets us the coords of this locarion details
        val add : MutableList<Address>? = geocoder.getFromLocation(
            coords.latitude,coords.longitude, 1
        ) //There can be several addresses(nullable) fiiting in the coords hence stored in a List
        // it takes the info of the latitude and longitude and the geocoder returns us the results
        return if (add?.isNotEmpty()==true){
            add[0].getAddressLine(0)//gets the 1st address in the list
        }else{
            "Address not Found"
        }
    }


}

//context in Android Kotlin is a crucial component that provides access to resources,
// system services, and environment information, and it is necessary for creating and
// interacting with various Android components and services. It is essential for
// building robust and feature-rich Android applications.