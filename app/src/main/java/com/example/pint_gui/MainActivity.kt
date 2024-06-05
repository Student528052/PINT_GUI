package com.example.pint_gui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pint_gui.ui.theme.PINT_GUITheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.net.URLConnection
import kotlin.system.measureTimeMillis

//NOTE:
/*When testing the http server, make sure to change the IP address on this file, as well as the
network_security_config.xml file located in the /res/xml folder to prevent security issures

 */
var timeout_value = 5000
var ESP32_port = 80
var ESP32_IP = "192.168.147.145:${ESP32_port}"
//Used to store the string
var esp32_status : String = "No data";

class MainActivity : ComponentActivity() {
    private var connectionJob: Job? = null
     val dataList = mutableListOf<Float>()
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //controlling between the main screen and the history screen
            val navController = rememberNavController()
            val result  = remember { mutableStateOf("Gathering ") }
            var connectionJob = CoroutineScope(Dispatchers.Default).launch{
                while(isActive){
                    delay(timeout_value.toLong()) //5 seconds
                   result.value = fetchData();
                    esp32_status = result.value;

                }
            }

            PINT_GUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "mainscreen") {
                        composable("mainscreen"){
                            mainscreen(navController = navController, esp32values = result)
                        }
                        composable("history_screen"){
                            History_screen(navController = navController)
                        }

                    }
                }
            }
        }
    }
}



//The actual function used to recieve data. It
private  suspend  fun fetchData() : String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("http://$ESP32_IP/")
            val urlConnection = url.openConnection() as HttpURLConnection

            val responseCode = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = urlConnection.inputStream
                inputStream.bufferedReader().use {
                    it.readText()
                }
            } else {
                "Server returned non-OK status: $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error : ${e.localizedMessage}"
        }
    }
}

/*
fun store_sen_data(floatValue: List<Float>, stringValue: String){
    var file = File("Sensor_data_temp.txt")
try{

    file.appendText("${floatValue.joinToString(",")}, $stringValue\n")
}
catch(e: Exception){
    file.appendText("Failed to read data ")

}
}
*/



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PINT_GUITheme {
        mainscreen(navController = rememberNavController(), esp32values =  remember {
            mutableStateOf("Sample")
        } )
    }
}