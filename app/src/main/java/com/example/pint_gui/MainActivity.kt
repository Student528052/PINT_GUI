package com.example.pint_gui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import java.net.ServerSocket
import java.net.Socket
import kotlin.system.measureTimeMillis


var timeout_value = 15000
var ESP32_port = 80
var ESP32_IP = "192.168.32.41"
var floadValues : List<Float> = emptyList()
var statusval : String = ""
class MainActivity : ComponentActivity() {
    private var connectionJob: Job? = null
     val dataList = mutableListOf<Float>()
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //controlling between the main screen and the history screen
            val navController = rememberNavController()

            var connectionJob = CoroutineScope(Dispatchers.Default).launch{
                while(isActive){
                    val ESP32_connection = connect_to_ESP32();
                    if(ESP32_connection)
                        //TODO: add function that updates periodically

                break;

                }
                delay(timeMillis = measureTimeMillis { timeout_value }) //15 seconds
            }

            PINT_GUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "mainscreen") {
                        composable("mainscreen"){
                            mainscreen(navController = navController)
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

/*connect_to_ESP32 Funcion
* This funcion's purpose is to constantly check the availability of the ESP32. It SHOUD NOT send or recieve any data */

 fun connect_to_ESP32(): Boolean{
        return try{
            //TODO: Make a sketch that should work on an arduino as well as an esp32 and test it using this application
            val socket = ServerSocket(ESP32_port)
            //val client: Socket = socket.accept()
            //This line breaks shit, so beweare of it
            //val reader = BufferedReader(InputStreamReader(client.getInputStream()))// Blocking call, waits for ESP32 connection
            //val message = reader.readLine()
            //val values = message.split(',').map{it.toFloat()}
           // floadValues = values.take(6).map { it.toFloat() }
            //statusval = values[6].toString()
            //store_sen_data(floadValues, statusval)
            return true
        }catch(e : Exception){
            e.printStackTrace()
            return false
        }

}
fun store_sen_data(floatValue: List<Float>, stringValue: String){
    var file = File("Sensor_data_temp.txt")
try{

    file.appendText("${floatValue.joinToString(",")}, $stringValue\n")
}
catch(e: Exception){
    file.appendText("Failed to read data ")

}
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PINT_GUITheme {

    }
}