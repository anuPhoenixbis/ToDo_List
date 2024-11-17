package com.example.to_dolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.to_dolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

@Composable
fun Navigation(){
    val navController= rememberNavController()
    val viewmodel:LocationViewModel = viewModel()
    val context = LocalContext.current//getting the current context
    val  locationUtils = LocationUtils(context)//passing the current context into the LocationUtils and passing it in a var

    NavHost(navController = navController, startDestination = "To-DoListScreen" ){
        composable( "To-DoListScreen"){
            shoppinglist(
                locationUtils = locationUtils,
                viewModel = viewmodel,
                navController = navController,
                context = context,
                address = viewmodel.address.value.firstOrNull()?.formatted_Address?:"No Address"//in the list of addresses we pass the 1st one in the list
                //using the formatted_Address in an elvis operator  if not given No Address
            )
        }
        //this dialog is a composable popup.This popup shows us the Google map to select location on map
        dialog("LocationScreen"){ backstack->
            viewmodel.location.value?.let { it1->//whenever the above composable is called this dialog is triggered and in turn this let fun is triggered which runs the mentioned code in it
                //this let keyword lets us compile a specific code and returns the value from it
                LocationSelectioScreen(location = it1//it1 holds the location values and pass it into this fun
                    , onLocationSelected = {locationdata->//when location marker is selected then viewmodel helps us fetch data of the latlng at the selected position and hence, fetches the address
                    viewmodel.fetchAddress("${locationdata.latitude},${locationdata.longitude}")
                    navController.popBackStack()//moves to the previous screen whenever the BACK button is pushed
                })
            }
        }
    }


}
