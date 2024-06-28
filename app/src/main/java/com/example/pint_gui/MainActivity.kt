package com.example.pint_gui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Entity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pint_gui.ui.theme.PINT_GUITheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

//NOTE:
/*When testing the http server, make sure to change the IP address on this file, as well as the
network_security_config.xml file located in the /res/xml folder to prevent security issures

 */
var timeout_value = 2000
var ESP32_port = 80
var ESP32_IP = "192.168.147.26:${ESP32_port}"
//Used to store the string
var esp32_status : String = "No data";
var history_count = 0;
var history_interval_sec = 30;

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
                while(isActive) {

                    delay(timeout_value.toLong()) //5 seconds
                    result.value = fetchData("http://$ESP32_IP");
                    esp32_status = result.value;
                    history_count++;
                    if (history_count > history_interval_sec){
                        val timestamp = SimpleDateFormat("yyyy:HH:mm:ss").format(Date());

                        Write_History(this@MainActivity,file, "$timestamp$esp32_status")
                        history_count = 0;

                }

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
  suspend  fun fetchData( fun_url: String) : String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(fun_url)
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
suspend  fun recordHistory(context: Context, file: String, status: String){

   // if(status != "Good" && status != "Bad" && status != "Very Bad") return;

    val timestamp = SimpleDateFormat("yyyy:HH:mm:ss").format(Date());
    val entry = "$timestamp $status\n"


    //writeFile(context , file, entry);



}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PINT_GUITheme {
        mainscreen(navController = rememberNavController(), esp32values =  remember {
            mutableStateOf("Sample")
        }, viewModel = MainViewModel() )
    }
}

