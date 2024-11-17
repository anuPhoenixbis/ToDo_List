package com.example.to_dolist

import android.Manifest
import android.content.Context
import android.location.Address
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

//refer to the original one for the notes
@Composable
fun shoppinglist (
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String
) {

    var sitems by remember { mutableStateOf(listOf<shoppingitems>()) }
    var showdialog by remember { mutableStateOf(false) }
    var itemquantity by remember { mutableStateOf("") }
    var itemname by remember { mutableStateOf("") }



//This var basically holds the boolean whether the permission is granted or not in the "onResult"
// and also the contract for making multiple requests to show the popup to get the permission for location
//the rememberLauncherForActivityResult function registers a request to start an activity for a result(result if permission granted or not)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()//this take as what we want to do with our Activity
        , onResult ={ permissions->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                //Permission granted
                //requestLoactionUpdates fun setup in the locationUtils file and updates the location details in the viewmodel file
                locationUtils.requestLoactionUpdates(viewModel = viewModel)

            }else{
                //Ask for permission
                //This val is a Boolean type, so we can use it to show the statement to be displayed for asking permission
                val rationalRequried= ActivityCompat.shouldShowRequestPermissionRationale(//This val asks for request of Permissions
                    //This fun requires an activity( we pass context as an activity) and a permission to rationalize+
                    context as MainActivity, // giving the MainActivity as the context here
                    Manifest.permission.ACCESS_FINE_LOCATION //permission given to it
                ) || ActivityCompat.shouldShowRequestPermissionRationale(//it also asks for this permission as well
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                //Showing the requesting statement for the user to give these permission
                if (rationalRequried){
                    Toast.makeText(//Toasts are little popups. Here, we are using them to take the context for the permission
                        // and a text requesting for the permission
                        context,
                        "Please Give the Permission",
                        Toast.LENGTH_LONG//duration of the toast
                    ).show()//to display the toast
                }else{
                    Toast.makeText(//Toasts are little popups. Here, we are using them to take the context for the permission
                        // and a text requesting for the permission
                        context,
                        "Please give the permission from the settings of your device",
                        Toast.LENGTH_LONG//duration of the toast
                    ).show()//to display the toast
                }
            }
        } )
//this step is vital for ANY permission.Syntax is the same just change the permissions




    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
    )
    {
        Button(
            onClick = {
                showdialog = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)


        ) {
            Text("Add items")

        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

                .padding(16.dp)
        ){
            items(sitems){
                    item ->
                if(item.isEditing) {
                    shoppingitemEditor(items = item, onEditComplete ={
                            editname, editquatinty->
                        sitems=sitems.map{ it.copy(isEditing = false) }
                        var edititem = sitems.find { it.id==item.id}
                        edititem?.let {
                            it.name=editname
                            it.quantity= editquatinty
                            it.address=address
                        }


                    })
                } else {
                    shoppinglistitems(items = item , onEditClick = { sitems=sitems.map{ it.copy(isEditing = it.id==item.id )}

                    } , onDeleteClick = {sitems=sitems-item}
                    )
                }


            }


        }

        if (showdialog) {
            AlertDialog(onDismissRequest = { showdialog = false },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            if (itemname.isNotBlank()) {
                                val newitem = shoppingitems(
                                    id = sitems.size + 1,
                                    name = itemname,
                                    quantity = itemquantity.toInt(),
                                    address=address
                                )
                                sitems = sitems + newitem
                                showdialog = false
                                itemname = ""
                                showdialog = false
                                itemquantity = ""
                            }
                        }) {
                            Text("Add")
                            Icon(imageVector =Icons.Default.CheckCircle , contentDescription = null)
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        Button(onClick = { showdialog = false }) {
                            Text("Cancel")
                            Icon(imageVector =Icons.Default.Clear , contentDescription = null)

                        }
                    }
                },
                title = { Text("Add shopping items") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemname,
                            onValueChange = { itemname = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = { Text("Enter items") }
                        )

                        OutlinedTextField(
                            value = itemquantity,
                            onValueChange = { itemquantity = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = { Text(" Quantity")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                        )
                        Button(onClick = {
                            if (locationUtils.hasLocalPermission(context)){//checking if we have the permission of location or not
                                locationUtils.requestLoactionUpdates(viewModel)//if yes, requesting for the Location Update in the locationutlis
                                navController.navigate("locationscreen"){//also we will navigate to this location screen if permission is granted
                                    this.launchSingleTop// for this navigation screen there should only be one screen and not multiple screen should be called if the "if" statement is true
                                }
                            }else{//if the permission is not granted
                                requestPermissionLauncher.launch(arrayOf(//launching the requestpermissionLauncher again for an array of given requests
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ))
                            }
                        }) {
                                Text(text = "Address")
                        }
                    }
                }

            )

        }
    }
}
@Composable
fun shoppingitemEditor(items: shoppingitems, onEditComplete: (  String, Int ) -> Unit) {
    var editname by remember { mutableStateOf(items.name) }
    var editquantity by remember { mutableStateOf(items.quantity.toString())}
    var isEditing by remember { mutableStateOf(items.isEditing) }
    Row (modifier= Modifier
        .fillMaxWidth()
        .background(color = Color.White)
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {
        Column {
            BasicTextField(value = editname, onValueChange = {editname=it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(value = editquantity, onValueChange = {editquantity=it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp))
        }
        Button(onClick = {
            isEditing = false
            onEditComplete(editname,editquantity.toIntOrNull()?:1)
        })
        {
            Text(text = "Save")
        }
    }

}




@Composable
fun shoppinglistitems(
    items: shoppingitems,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(2.dp, color = Color.Black),
                shape = RoundedCornerShape(20)
            ), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ){

            Row {
                Text(text = items.name, modifier = Modifier.padding(8.dp))
                Text(text = " Qty :${items.quantity}", modifier = Modifier.padding(8.dp))
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(imageVector = Icons.Default.LocationOn, contentDescription =null )
                Text(text = items.address)//storing the address in another row
            }

        }
        Row( modifier = Modifier.padding(8.dp)) {
            IconButton(onClick =  onEditClick ) {
                Icon(imageVector = Icons.Default.Edit,contentDescription=null)
            }
            IconButton(onClick =  onDeleteClick ) {
                Icon(imageVector = Icons.Default.Delete,contentDescription=null)
            }


        }

    }

}



data class shoppingitems(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false,
    var address: String=""//stores the address of where to buy the item

)

//
//@Preview(showBackground = true)
//@Composable
//fun shoppinglistPreview() {
//    shoppinglist()
//}