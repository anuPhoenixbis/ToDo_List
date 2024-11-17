package com.example.to_dolist

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun LocationSelectioScreen(//now we can call this composable to get the address using the users' set location
    location: LocationAPIData,//it contains the selected LatLng
    onLocationSelected: (LocationAPIData)->Unit//it tells what to do with the selected location
){
    //this var gets the users' coords
    val userLocator = remember {
        mutableStateOf(LatLng(location.latitude,location.longitude))//stores the users' latlng
    }

//this var shows us the state of the position of the device for us to zoom in or out from that position
    var cameraPositionState = rememberCameraPositionState {
    position=CameraPosition.fromLatLngZoom(userLocator.value,10f)
    }


    Column(//this column takes the whole screen which includes the google map to set the location and the actual set location button
        modifier= Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        GoogleMap(//the actual google map to choose the location
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            cameraPositionState= cameraPositionState,//this passes the current position of the device on the map
            onMapClick = {
                userLocator.value=it//when the map is clicked the device's location is the current location as shown on the map
            }
        ){
            Marker(state = MarkerState(position = userLocator.value))//Its the set location marker (the red marker on the google map)
        }

        var newLocation:LocationAPIData


        //whenever the button is clicked the current location shall be set to this new selected location
        Button(onClick = {
            newLocation=LocationAPIData(userLocator.value.latitude,userLocator.value.longitude)//when the button is clicked after selecting the location on the map which is the userLocator now is stored in this var
            onLocationSelected(newLocation)//THIS new LatLng stored in this var "newLocation" as the device's location is now passed to this fun
            //this fun can further be called to be converted to the address of the given LatLng.

        }) {//the button to cnf the location
            Text(text = "Set Location")
        }
    }
}








