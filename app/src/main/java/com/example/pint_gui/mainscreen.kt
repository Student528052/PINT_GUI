package com.example.pint_gui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class ModelData(val name: String, val value: Int)
private fun LoadSensorData(context: Context, filename : String): List<ModelData> {
    val sensorDataList = mutableListOf<ModelData>()
   try{
       val inputStream: InputStream = context.assets.open(filename)
       val lines = inputStream.bufferedReader().useLines { lines -> lines.toList() }

    lines.forEach { line ->
        val parts =
            line.split("=").map { it.trim() }
        if (parts.size == 2) {
            val name = parts[0]
            val value = parts[1].toIntOrNull() ?: 0
            sensorDataList.add(ModelData(name, value))
        }
    }
    } catch (e: Exception) {
        // Handle exceptions, perhaps log them or handle differently depending on your app's needs
        e.printStackTrace()
    }

    return sensorDataList
}

@Composable
fun BarChart(SensorData: List<ModelData>, modifier: Modifier = Modifier){

    Column (modifier = Modifier
        .padding(60.dp)
        .fillMaxSize()){
        SensorData.forEach { data ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${data.name}", modifier = Modifier.width(70.dp))
                Box(
                    modifier = Modifier
                        .height(45.dp)
                        .width(data.value.dp * 3) // Scale factor to adjust bar width
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${data.value}", color = Color.White, style = TextStyle(fontSize = 25.sp), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun Calibrate(){

    try{
        val url = URL(ESP32_IP)
        val connect = url.openConnection() as HttpURLConnection
        connect.requestMethod = "GET"
        val calrequest = URL("$ESP32_IP?message=Cal")
        val responseCode = connect.responseCode

    connect.connect()
    if(responseCode == HttpURLConnection.HTTP_OK)

    else{}
    connect.disconnect()
}
catch (e: Exception ){

}

}
/*
MAIN FUNCTION
 */
@Composable
fun mainscreen(navController: NavHostController){

    val sensorData = remember { mutableStateListOf<ModelData>() }
    val context = LocalContext.current;

    // Load data asynchronously
    LaunchedEffect(Unit) {
        /*TODO: THIS IS A TEMPORARY DEMONSTRATION OF HOW THE APPLICATION WILL GET THE DATA, CONSULT STILIYAN AND HUGO ABOUT THIS

         */
        sensorData.addAll(LoadSensorData( context,"Sensor_data_temp.txt"))
    }
    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        //timer
        //TODO: change this to start a function and start the timer
        Box(modifier = Modifier.padding(10.dp) ) {
            Text(text = "00:00:00", style = TextStyle(fontSize = 40.sp), fontWeight = FontWeight.Bold )
        }

        Button(onClick = { navController.navigate("history_screen")}, modifier = Modifier.padding(10.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_history_24),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

        }
        Button(onClick = { Calibrate() }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_build_24),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }



        //assuming we get the data live(and not store it in a file), we can choose not to display anytning
        if(connect_to_ESP32()) {
            BarChart(SensorData = sensorData)
            Box(modifier = Modifier.padding(100.dp).background(color = Color(30,200,30))){
                Text(text = "Status: Connected", modifier = Modifier.padding( ).background( color = Color(20,200,20)))
            }

        }

        else{
            Text(text="Sorry, nothing to show",modifier = Modifier.padding(150.dp))
        }
        Box( modifier = Modifier.padding(100.dp)){
            Text(text = "STATUS: GOOD(TEST TEST TEST)", modifier = Modifier.padding( ))
        }

}
}